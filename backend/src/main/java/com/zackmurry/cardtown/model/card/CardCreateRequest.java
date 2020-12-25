package com.zackmurry.cardtown.model.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardCreateRequest implements EmailOwnerCard {

    private String ownerEmail;
    private String tag;
    private String cite;
    private String citeInformation;
    private String body;

    public CardEntity toCardEntity(UUID ownerId) {
        return new CardEntity(
                ownerId,
                tag,
                cite,
                citeInformation,
                body
        );
    }

}
