package com.zackmurry.cardtown.model.card;

import com.zackmurry.cardtown.util.EncryptionUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
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

    public void decryptFields(byte[] secretKey) throws Exception {
        tag = EncryptionUtils.decryptStringAES(tag, secretKey);
        cite = EncryptionUtils.decryptStringAES(cite, secretKey);
        citeInformation = EncryptionUtils.decryptStringAES(citeInformation, secretKey);
        bodyHtml = EncryptionUtils.decryptStringAES(bodyHtml, secretKey);
        bodyDraft = EncryptionUtils.decryptStringAES(bodyDraft, secretKey);
    }

    public void encryptFields(byte[] secretKey) throws Exception {
        tag = EncryptionUtils.encryptStringAES(tag, secretKey);
        cite = EncryptionUtils.encryptStringAES(cite, secretKey);
        citeInformation = EncryptionUtils.encryptStringAES(citeInformation, secretKey);
        bodyHtml = EncryptionUtils.encryptStringAES(bodyHtml, secretKey);
        bodyDraft = EncryptionUtils.encryptStringAES(bodyDraft, secretKey);
    }

}
