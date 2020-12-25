package com.zackmurry.cardtown.model.card;

import com.zackmurry.cardtown.util.UUIDUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * the same as card, but it has the abbreviated id instead of the full uuid
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCard implements UUIDOwnerCard, ShortenedIdCard {

    private String id;
    private UUID ownerId;
    private String tag;
    private String cite;
    private String citeInformation;
    private String body;

    public static ResponseCard fromCard(UUIDOwnerCard card, String shortenedId) {
        return new ResponseCard(
                shortenedId,
                card.getOwnerId(),
                card.getTag(),
                card.getCite(),
                card.getCiteInformation(),
                card.getBody()
        );
    }

    public static ResponseCard fromCard(CardEntity card) {
        return fromCard(card, UUIDUtils.compress(card.getId()));
    }

}
