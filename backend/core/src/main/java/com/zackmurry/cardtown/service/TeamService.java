package com.zackmurry.cardtown.service;

import com.zackmurry.cardtown.dao.team.TeamDao;
import com.zackmurry.cardtown.exception.BadRequestException;
import com.zackmurry.cardtown.exception.ForbiddenException;
import com.zackmurry.cardtown.exception.InternalServerException;
import com.zackmurry.cardtown.exception.TeamNotFoundException;
import com.zackmurry.cardtown.model.action.ActionEntity;
import com.zackmurry.cardtown.model.action.ActionType;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.model.team.*;
import com.zackmurry.cardtown.util.EncryptionUtils;
import com.zackmurry.cardtown.util.UUIDCompressor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamService {

    private static final Logger logger = LoggerFactory.getLogger(TeamService.class);

    @Autowired
    private TeamDao teamDao;

    @Autowired
    private ActionService actionService;

    @Autowired
    private EncryptionUtils encryptionUtils;

    /**
     * Creates a team with the specified information
     *
     * @param teamCreateRequest Details of new team
     * @return An object containing the id of the new team (encoded in Base64url) and the team's secret key (for an invite)
     * @throws com.zackmurry.cardtown.exception.LengthRequiredException If the team's name is > 128 chars or < 1 char
     * @throws InternalServerException                                  If an error occurs during encryption
     * @throws InternalServerException                                  If a <code>SQLException</code> occurs in the DAO layer
     */
    public TeamCreationResponse createTeam(@NonNull TeamCreateRequest teamCreateRequest) {
        // todo show user an invite link after creating a team and include teamSecretKey in the invite link
        teamCreateRequest.validateFields();

        final UserModel principal = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Creating an AES secret key for the team
        final SecretKey teamSecretKey = EncryptionUtils.generateStrongAESKey(256);
        // todo base64 may need to be base64url for request params
        // Generating hash for verifying invites
        final String teamSecretKeyHashBase64 = Base64.encodeBase64String(encryptionUtils.getSHA256Hash(teamSecretKey.getEncoded()));
        final TeamEntity teamEntity = new TeamEntity(teamCreateRequest.getName(), teamSecretKeyHashBase64);
        try {
            teamEntity.encryptFields(teamSecretKey.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
        final UUID teamId = teamDao.createTeam(teamEntity);

        // Adding the creator to the team
        byte[] encryptedTeamSecretKey;
        try {
            encryptedTeamSecretKey = EncryptionUtils.encryptAES(teamSecretKey.getEncoded(), principal.getSecretKey());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
        final TeamMemberEntity teamMemberEntity = new TeamMemberEntity(teamId, principal.getId(), Base64.encodeBase64String(encryptedTeamSecretKey), TeamRole.OWNER);
        teamDao.addMemberToTeam(teamMemberEntity);
        final String encodedTeamSecretKey = Base64.encodeBase64URLSafeString(teamSecretKey.getEncoded());
        // Additional link invites can be generated later based on the decryption of the team secret key by a member
        return new TeamCreationResponse(UUIDCompressor.compress(teamId), encodedTeamSecretKey);
    }

    /**
     * Registers the principal with a team
     *
     * @param teamJoinRequest Details of team to join
     * @throws TeamNotFoundException   If the team could not be found
     * @throws BadRequestException     If the alleged secret key does not match the actual secret key
     * @throws InternalServerException If an error occurs during encryption
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public void joinTeam(@NonNull TeamJoinRequest teamJoinRequest) {
        // todo: Transition old cards that were made before joining a team to the user's new encryption method
        final UserModel principal = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final UUID teamId = UUIDCompressor.decompress(teamJoinRequest.getTeamId());
        final TeamEntity teamEntity = teamDao.getTeamById(teamId).orElseThrow(TeamNotFoundException::new);
        final byte[] decodedTeamSecretKey = Base64.decodeBase64URLSafe(teamJoinRequest.getTeamSecretKey());
        final String teamSecretKeyHashBase64 = Base64.encodeBase64String(encryptionUtils.getSHA256Hash(decodedTeamSecretKey));
        if (!teamEntity.getSecretKeyHash().equals(teamSecretKeyHashBase64)) {
            throw new BadRequestException();
        }
        byte[] encryptedTeamSecretKey;
        try {
            // Encrypting the team secret key with the user's secret key
            encryptedTeamSecretKey = EncryptionUtils.encryptAES(decodedTeamSecretKey, principal.getSecretKey());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
        final TeamMemberEntity teamMemberEntity = new TeamMemberEntity(teamId, principal.getId(), Base64.encodeBase64String(encryptedTeamSecretKey));
        teamDao.addMemberToTeam(teamMemberEntity);

        actionService.createAction(
                ActionEntity.builder()
                        .type(ActionType.JOIN_TEAM)
                        .principal() // Not including teamId because that'd require another column in the table and it's kinda implied
                        .build()
        );
    }

    /**
     * Deletes the team of the current user
     *
     * @throws BadRequestException     If the current user is not part of a team
     * @throws ForbiddenException      If the current user is not the owner of their team
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public void deleteTeam() {
        // todo prompt for switching ownership of team if 1+ other members
        // todo for TeamResponse: include owner in preview of team
        final UUID userId = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        final TeamMemberEntity teamMemberEntity = teamDao.getTeamMemberEntityByUserId(userId).orElseThrow(BadRequestException::new);
        if (!teamMemberEntity.getRole().equals(TeamRole.OWNER)) {
            throw new ForbiddenException();
        }
        teamDao.deleteTeamById(teamMemberEntity.getTeamId());
    }

    /**
     * Gets the details of the current user's team (decrypted)
     *
     * @return If found: an <code>Optional</code> containing the current user's team details; else: <code>Optional.empty()</code>
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public Optional<TeamEntity> getTeamOfUser() {
        final UserModel principal = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.getTeamSecretKey() == null) {
            return Optional.empty();
        }
        final UUID teamId = teamDao.getTeamIdWithUser(principal.getId()).orElse(null);
        if (teamId == null) {
            return Optional.empty();
        }

        final TeamEntity teamEntity = teamDao.getTeamById(teamId).orElse(null);
        if (teamEntity == null) {
            return Optional.empty();
        }
        try {
            teamEntity.decryptFields(principal.getTeamSecretKey());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
        return Optional.of(teamEntity);
    }

    /**
     * Gets the team secret key of a given user
     *
     * @param userId        Id of user to get team secret key of
     * @param userSecretKey Secret key of user
     * @return If user is in a team: an <code>Optional</code> containing the team's secret key; else: <code>Optional.empty()</code>
     */
    public Optional<byte[]> getTeamSecretKeyByUser(@NonNull UUID userId, byte[] userSecretKey) {
        final TeamMemberEntity teamMemberEntity = teamDao.getTeamMemberEntityByUserId(userId).orElse(null);
        if (teamMemberEntity == null) {
            return Optional.empty();
        }
        final byte[] encryptedTeamSecretKey = Base64.decodeBase64(teamMemberEntity.getTeamSecretKey());
        try {
            return Optional.of(EncryptionUtils.decryptAES(encryptedTeamSecretKey, userSecretKey));
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    public boolean usersInSameTeam(@NonNull UUID userId1, @NonNull UUID userId2) {
        if (userId1.equals(userId2)) {
            return true;
        }
        final TeamMemberEntity teamMemberEntity1 = teamDao.getTeamMemberEntityByUserId(userId1).orElse(null);
        if (teamMemberEntity1 == null) {
            return false;
        }
        final TeamMemberEntity teamMemberEntity2 = teamDao.getTeamMemberEntityByUserId(userId2).orElse(null);
        if (teamMemberEntity2 == null) {
            return false;
        }
        return teamMemberEntity1.getTeamId().equals(teamMemberEntity2.getTeamId());
    }

}
