package com.zackmurry.cardtown.model.arg;

import com.zackmurry.cardtown.model.analytic.ResponseAnalytic;
import com.zackmurry.cardtown.model.arg.card.ResponseArgumentCard;
import com.zackmurry.cardtown.model.auth.ResponseUserDetails;
import com.zackmurry.cardtown.model.card.ResponseCard;
import com.zackmurry.cardtown.util.UUIDCompressor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseArgument {

    private String id;
    private ResponseUserDetails owner;
    private String name;
    private List<ResponseArgumentCard> cards;
    private List<ResponseAnalytic> analytics;
    private boolean deleted;

    public static ResponseArgument fromArgumentEntity(ArgumentEntity argumentEntity, ResponseUserDetails userDetails, List<ResponseArgumentCard> cards, List<ResponseAnalytic> analytics) {
        return new ResponseArgument(UUIDCompressor.compress(argumentEntity.getId()), userDetails, argumentEntity.getName(), cards, analytics, argumentEntity.isDeleted());
    }

}
