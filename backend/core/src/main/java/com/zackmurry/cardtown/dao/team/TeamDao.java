package com.zackmurry.cardtown.dao.team;

import com.zackmurry.cardtown.exception.InternalServerException;
import com.zackmurry.cardtown.model.team.TeamEntity;
import com.zackmurry.cardtown.model.team.TeamMemberEntity;

import java.util.Optional;
import java.util.UUID;

public interface TeamDao {

    /**
     * Creates a new team with the specified information
     *
     * @param request Details of team entity to create
     * @return Id of new team
     * @throws InternalServerException If a <code>SQLException</code> occurs
     * @throws InternalServerException If an id is not generated
     */
    UUID createTeam(TeamEntity request);

    /**
     * Adds a member to a team with the specified details
     *
     * @param entity Details of user to add. <code>teamSecretKey</code> should be in Base64 and encrypted by the user's secret key
     * @throws InternalServerException If a <code>SQLException</code> occurs
     */
    void addMemberToTeam(TeamMemberEntity entity);

    /**
     * Gets a <code>TeamEntity</code> by its id
     *
     * @param id Id of team
     * @return If found: <code>Optional</code> containing <code>TeamEntity</code>; else: <code>Optional.empty()</code>
     * @throws InternalServerException If a <code>SQLException</code> occurs
     */
    Optional<TeamEntity> getTeamById(UUID id);

    /**
     * Gets the id of a team with a specified user
     *
     * @param userId Id of user to find team of
     * @return If found: an <code>Optional</code> containing the team's id; else: <code>Optional.empty()</code>
     * @throws InternalServerException If a <code>SQLException</code> occurs
     */
    Optional<UUID> getTeamIdWithUser(UUID userId);

    void deleteTeamById(UUID teamId);

    Optional<TeamMemberEntity> getTeamMemberEntityByUserId(UUID userId);

}
