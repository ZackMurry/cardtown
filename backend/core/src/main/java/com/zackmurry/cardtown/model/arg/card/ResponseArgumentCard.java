package com.zackmurry.cardtown.model.arg.card;

import com.zackmurry.cardtown.model.auth.ResponseUserDetails;
import com.zackmurry.cardtown.model.card.CardEntity;
import com.zackmurry.cardtown.model.card.ResponseCard;
import com.zackmurry.cardtown.util.UUIDCompressor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseArgumentCard extends ResponseCard {

    private short position;

    public ResponseArgumentCard(String id,
                                ResponseUserDetails owner,
                                String tag,
                                String cite,
                                String citeInformation,
                                String bodyHtml,
                                String bodyDraft,
                                String bodyText,
                                long timeCreatedAt,
                                long lastModified,
                                boolean deleted,
                                short position) {
        super(id, owner, tag, cite, citeInformation, bodyHtml, bodyDraft, bodyText, timeCreatedAt, lastModified, deleted);
        this.position = position;
    }

    public static ResponseArgumentCard fromCard(CardEntity card, ResponseUserDetails responseUserDetails, String shortenedId, short position) {
        return new ResponseArgumentCard(
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
                card.isDeleted(),
                position
        );
    }

    public static ResponseArgumentCard fromCard(CardEntity card, ResponseUserDetails responseUserDetails, short position) {
        return fromCard(card, responseUserDetails, UUIDCompressor.compress(card.getId()), position);
    }

    public static ResponseArgumentCard of(ResponseCard card, short position) {
        return new ResponseArgumentCard(
                card.getId(),
                card.getOwner(),
                card.getTag(),
                card.getCite(),
                card.getCiteInformation(),
                card.getBodyHtml(),
                card.getBodyDraft(),
                card.getBodyText(),
                card.getTimeCreatedAt(),
                card.getLastModified(),
                card.isDeleted(),
                position
        );
    }

}
