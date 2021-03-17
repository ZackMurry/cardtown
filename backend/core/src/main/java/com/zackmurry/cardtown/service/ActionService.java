package com.zackmurry.cardtown.service;

import com.zackmurry.cardtown.dao.action.ActionDao;
import com.zackmurry.cardtown.model.action.ActionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class ActionService {

    @Autowired
    private ActionDao actionDao;

    public void createAction(@NonNull ActionEntity actionEntity) {
        actionDao.createAction(actionEntity);
    }

}
