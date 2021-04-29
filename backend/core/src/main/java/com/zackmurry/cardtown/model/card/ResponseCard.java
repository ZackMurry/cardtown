package com.zackmurry.cardtown.model.card;

import com.zackmurry.cardtown.model.auth.ResponseUserDetails;
import com.zackmurry.cardtown.util.UUIDCompressor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * the same as card, but it has the abbreviated id instead of the full uuid
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCard implements ShortenedIdCard {

    private String id;
    private ResponseUserDetails owner;
    private String tag;
    private String cite;
    private String citeInformation;
    private String bodyHtml;
    private String bodyDraft;
    private String bodyText;
    private long timeCreatedAt;
    private long lastModified;
    private boolean deleted;

    public static ResponseCard fromCard(CardEntity card, ResponseUserDetails responseUserDetails, String shortenedId) {
        return new ResponseCard(
                shortenedId,
                responseUserDetails,
                card.getTag(),
                card.getCite(),
                card.getCiteInformation(),
                card.getBodyHtml(),
                card.getBodyDraft(),
                card.getBodyText(),
                card.getTimeCreatedAt(),
                card.getLastModified(),
                card.isDeleted()
        );
    }

    public static ResponseCard fromCard(CardEntity card, ResponseUserDetails responseUserDetails) {
        return fromCard(card, responseUserDetails, UUIDCompressor.compress(card.getId()));
    }

}
