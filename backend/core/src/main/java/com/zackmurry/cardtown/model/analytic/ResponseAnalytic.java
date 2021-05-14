package com.zackmurry.cardtown.model.analytic;

import com.zackmurry.cardtown.util.UUIDCompressor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAnalytic {

    private String id;
    private String body;
    private short position;

    public static ResponseAnalytic of(@NonNull AnalyticEntity analyticEntity) {
        return new ResponseAnalytic(
                UUIDCompressor.compress(analyticEntity.getId()),
                analyticEntity.getBody(),
                analyticEntity.getPosition()
        );
    }

}
