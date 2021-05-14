package com.zackmurry.cardtown.model.analytic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public final class AnalyticCreateRequest extends EncryptedAnalytic {

    public AnalyticCreateRequest(String body) {
        this.body = body;
    }

}
