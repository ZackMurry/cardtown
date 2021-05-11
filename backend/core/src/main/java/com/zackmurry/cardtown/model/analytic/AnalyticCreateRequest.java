package com.zackmurry.cardtown.model.analytic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class AnalyticCreateRequest extends EncryptedAnalytic {

    private short position;

    public AnalyticCreateRequest(String body, short position) {
        this.body = body;
        this.position = position;
    }

}
