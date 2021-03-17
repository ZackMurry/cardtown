package com.zackmurry.cardtown.service;

import com.zackmurry.cardtown.dao.action.ActionDao;
import com.zackmurry.cardtown.model.action.ActionEntity;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.model.team.TeamEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.zackmurry.cardtown.exception.InternalServerException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ActionService {

    @Autowired
    private ActionDao actionDao;

    @Autowired
    private TeamService teamService;

    /**
     * Creates an action in the database
     *
     * @param actionEntity Details of action to create
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public void createAction(@NonNull ActionEntity actionEntity) {
        actionDao.createAction(actionEntity);
    }

    /**
     * Gets all actions that should be visible to the principal
     *
     * @return Actions that are visible to the user
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public List<ActionEntity> getAllActionsVisibleToUser() {
        final Optional<TeamEntity> optionalTeamEntity = teamService.getTeamOfUser();
        if (optionalTeamEntity.isPresent()) {
            return actionDao.getAllActionsByTeam(optionalTeamEntity.get().getId());
        }
        final UUID userId = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return actionDao.getAllActionsByUser(userId);
    }

    // todo add methods that can handle pagination

}
