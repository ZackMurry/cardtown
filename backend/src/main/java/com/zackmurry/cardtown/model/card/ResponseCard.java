package com.zackmurry.cardtown.model.card;

import com.zackmurry.cardtown.model.auth.ResponseUserDetails;
import com.zackmurry.cardtown.util.UUIDCompressor;
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
    private ResponseUserDetails owner;
    private String tag;
    private String cite;
    private String citeInformation;
    private String bodyHtml;
    private String bodyDraft;

    public static ResponseCard fromCard(UUIDOwnerCard card, ResponseUserDetails responseUserDetails, String shortenedId) {
        return new ResponseCard(
                shortenedId,
                responseUserDetails,
                card.getTag(),
                card.getCite(),
                card.getCiteInformation(),
                card.getBodyHtml(),
                card.getBodyDraft()
        );
    }

    public static ResponseCard fromCard(CardEntity card, ResponseUserDetails responseUserDetails) {
        return fromCard(card, responseUserDetails, UUIDCompressor.compress(card.getId()));
    }

    @Override
    public UUID getOwnerId() {
        return UUIDCompressor.decompress(owner.getId());
    }
}
