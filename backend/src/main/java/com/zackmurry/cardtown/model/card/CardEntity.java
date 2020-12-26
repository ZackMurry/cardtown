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
public class CardEntity implements UUIDIdCard, UUIDOwnerCard {

    private UUID id;
    private UUID ownerId;
    private String tag;
    private String cite;
    private String citeInformation;
    private String bodyHtml;
    private String bodyDraft;

    public CardEntity(UUID ownerId, String tag, String cite, String citeInformation, String bodyHtml, String bodyDraft) {
        this.ownerId = ownerId;
        this.tag = tag;
        this.cite = cite;
        this.citeInformation = citeInformation;
        this.bodyHtml = bodyHtml;
        this.bodyDraft = bodyDraft;
    }

}
