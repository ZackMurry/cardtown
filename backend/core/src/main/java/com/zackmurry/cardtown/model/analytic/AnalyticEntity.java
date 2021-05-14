package com.zackmurry.cardtown.model.analytic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticEntity extends EncryptedAnalytic {

    private UUID id;
    private UUID argumentId;
    private short position;

    public AnalyticEntity(UUID id, UUID argumentId, String body, short position) {
        this.id = id;
        this.argumentId = argumentId;
        this.body = body;
        this.position = position;
    }

}
