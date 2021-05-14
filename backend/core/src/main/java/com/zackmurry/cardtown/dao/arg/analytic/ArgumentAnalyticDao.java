package com.zackmurry.cardtown.dao.arg.analytic;

import com.zackmurry.cardtown.model.analytic.AnalyticCreateRequest;
import com.zackmurry.cardtown.model.analytic.AnalyticEntity;
import com.zackmurry.cardtown.model.analytic.EncryptedAnalytic;

import java.util.List;
import java.util.UUID;

public interface ArgumentAnalyticDao {

    /**
     * Creates an analytic in an argument at a position. Does not alter any other items in argument
     *
     * @param argumentId Id of argument to create analytic in
     * @param body Body of analytic (encrypted)
     * @param indexInArgument Index in argument to insert in
     * @return Id of new analytic
     */
    UUID createAnalytic(UUID argumentId, String body, short indexInArgument);

    void incrementPositionsOfAnalyticsInArgumentAtOrPastIndex(UUID argumentId, short index);

    void decrementPositionsOfAnalyticsInArgumentAtOrPastIndex(UUID argumentId, short index);

    /**
     *
     * @param id Id of analytic to update
     * @param analyticEntity
     */
    void updateAnalyticById(UUID id, EncryptedAnalytic analytic);

    List<AnalyticEntity> getAnalyticsByArgumentId(UUID id);
}
