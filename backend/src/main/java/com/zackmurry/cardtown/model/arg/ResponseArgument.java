package com.zackmurry.cardtown.model.arg;

import com.zackmurry.cardtown.model.auth.ResponseUserDetails;
import com.zackmurry.cardtown.model.card.ResponseCard;
import com.zackmurry.cardtown.util.UUIDUtils;
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
    private List<ResponseCard> cards;

    // todo list of cards

    public static ResponseArgument fromArgumentEntity(ArgumentEntity argumentEntity, ResponseUserDetails userDetails, List<ResponseCard> cards) {
        return new ResponseArgument(UUIDUtils.compress(argumentEntity.getId()), userDetails, argumentEntity.getName(), cards);
    }

}
