package com.zackmurry.cardtown.model.analytic;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AnalyticUpdateRequest extends EncryptedAnalytic {

    public AnalyticUpdateRequest(String body) {
        this.body = body;
    }

}
