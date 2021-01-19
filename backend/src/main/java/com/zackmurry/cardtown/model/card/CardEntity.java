package com.zackmurry.cardtown.model.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * a card that models the cards in the database
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class CardEntity extends EncryptedCard implements UUIDIdCard, UUIDOwnerCard {

    private UUID id;
    private UUID ownerId;

    public CardEntity(UUID ownerId, String tag, String cite, String citeInformation, String bodyHtml, String bodyDraft, String bodyText) {
        super(tag, cite, citeInformation, bodyHtml, bodyDraft, bodyText);
        this.ownerId = ownerId;
    }

    public CardEntity(UUID id, UUID ownerId, String tag, String cite, String citeInformation, String bodyHtml, String bodyDraft, String bodyText) {
        super(tag, cite, citeInformation, bodyHtml, bodyDraft, bodyText);
        this.id = id;
        this.ownerId = ownerId;
    }
}
