package com.zackmurry.cardtown.dao.action;

import com.zackmurry.cardtown.model.action.ActionEntity;

import java.util.List;
import java.util.UUID;

public interface ActionDao {

    void createAction(ActionEntity actionEntity);

    List<ActionEntity> getActionsByTeam(UUID teamId);

    List<ActionEntity> getRecentActionsByTeam(UUID teamId, int count);

    List<ActionEntity> getRecentActionsByTeam(UUID teamId, int count, int offset);

}
