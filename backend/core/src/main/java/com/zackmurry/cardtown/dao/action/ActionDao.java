package com.zackmurry.cardtown.dao.action;

import com.zackmurry.cardtown.model.action.ActionEntity;
import com.zackmurry.cardtown.exception.InternalServerException;

import java.util.List;
import java.util.UUID;

public interface ActionDao {

    /**
     * Creates an action with the details of the actionEntity
     *
     * @param actionEntity Action to store
     * @throws InternalServerException If a <code>SQLException</code> occurs
     */
    void createAction(ActionEntity actionEntity);

    /**
     * Gets all the actions that have a subject that belongs to a team, ordered by time occurred (DESC)
     *
     * @param teamId Team to find actions of
     * @return The associated action entities
     * @throws InternalServerException If a <code>SQLException</code> occurs
     */
    List<ActionEntity> getAllActionsByTeam(UUID teamId);

    /**
     * Gets the <code>count</code> most recent actions by a team, ordered by time occurred (DESC)
     *
     * @param teamId Id of team to find actions of
     * @param count Number of actions to find
     * @return The associated action entities
     * @throws InternalServerException If a <code>SQLException</code> occurs
     */
    List<ActionEntity> getRecentActionsByTeam(UUID teamId, int count);

    /**
     * Gets the <code>count</code> most recent actions by a team, ordered by time occurred (DESC), skipping the first <code>offset</code> actions
     *
     * @param teamId Id of team to find actions of
     * @param count Number of actions to find
     * @param offset Number of actions to skip (for pagination and infinite scroll)
     * @return The associated actions
     * @throws InternalServerException If a <code>SQLException</code> occurs
     */
    List<ActionEntity> getRecentActionsByTeam(UUID teamId, int count, int offset);

    /**
     * Gets the <code>count</code> most recent actions by a user, ordered by time occurred (DESC), skipping the first <code>offset</code> actions
     *
     * @param userId Id of user to find actions of
     * @param count Number of actions to find
     * @param offset Number of actions to skip (for pagination and infinite scroll)
     * @return The associated actions
     * @throws InternalServerException If a <code>SQLException</code> occurs
     */
    List<ActionEntity> getRecentActionsByUser(UUID userId, int count, int offset);

    /**
     * Gets all actions by a user, ordered by time occurred (DESC)
     *
     * @param userId Id of user to find actions of
     * @return The associated actions
     * @throws InternalServerException If a <code>SQLException</code> occurs
     */
    List<ActionEntity> getAllActionsByUser(UUID userId);

}
