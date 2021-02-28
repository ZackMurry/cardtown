package com.zackmurry.cardtown.service;

import com.zackmurry.cardtown.dao.team.TeamDao;
import com.zackmurry.cardtown.exception.BadRequestException;
import com.zackmurry.cardtown.exception.InternalServerException;
import com.zackmurry.cardtown.exception.TeamNotFoundException;
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
import java.util.UUID;

@Service
public class TeamService {

    private static final Logger logger = LoggerFactory.getLogger(TeamService.class);

    @Autowired
    private TeamDao teamDao;

    @Autowired
    private EncryptionUtils encryptionUtils;

    /**
     * Creates a team with the specified information
     * @param teamCreateRequest Details of new team
     * @return Id of new team in Base64
     * @throws com.zackmurry.cardtown.exception.LengthRequiredException If the team's name is > 128 chars or < 1 char
     * @throws InternalServerException If an error occurs during encryption
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public String createTeam(@NonNull TeamCreateRequest teamCreateRequest) {
        // todo show user an invite link after creating a team and include teamSecretKeyHash in the invite link
        teamCreateRequest.validateFields();

        final UserModel principal = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final SecretKey teamSecretKey = EncryptionUtils.generateStrongAESKey(256);
        // todo base64 may need to be base64url for request params
        final String teamSecretKeyHashBase64 = Base64.encodeBase64String(encryptionUtils.getSHA256Hash(teamSecretKey.getEncoded()));
        final TeamEntity teamEntity = new TeamEntity(teamCreateRequest.getName(), teamSecretKeyHashBase64);
        try {
            teamEntity.encryptFields(principal.getSecretKey());
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
        return UUIDCompressor.compress(teamId);
    }

    /**
     * Registers the principal with a team
     * @param teamJoinRequest Details of team to join
     * @throws TeamNotFoundException If the team could not be found
     * @throws BadRequestException If the alleged secret key does not match the actual secret key
     * @throws InternalServerException If an error occurs during encryption
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public void joinTeam(@NonNull TeamJoinRequest teamJoinRequest) {
        final UserModel principal = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final UUID teamId = UUIDCompressor.decompress(teamJoinRequest.getTeamId());
        final TeamEntity teamEntity = teamDao.getTeamById(teamId).orElseThrow(TeamNotFoundException::new);
        final String teamSecretKeyHashBase64 = encryptionUtils.getSHA256HashBase64(teamJoinRequest.getTeamSecretKey());
        if (!teamEntity.getSecretKeyHash().equals(teamSecretKeyHashBase64)) {
            throw new BadRequestException();
        }
        byte[] encryptedTeamSecretKey;
        try {
            encryptedTeamSecretKey = EncryptionUtils.encryptAES(Base64.decodeBase64(teamSecretKeyHashBase64), principal.getSecretKey());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
        final TeamMemberEntity teamMemberEntity = new TeamMemberEntity(teamId, principal.getId(), Base64.encodeBase64String(encryptedTeamSecretKey));
        teamDao.addMemberToTeam(teamMemberEntity);
    }

}
