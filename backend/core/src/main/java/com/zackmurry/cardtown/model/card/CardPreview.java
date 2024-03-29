package com.zackmurry.cardtown.model.card;

import com.zackmurry.cardtown.model.auth.ResponseUserDetails;
import com.zackmurry.cardtown.util.EncryptionUtils;
import com.zackmurry.cardtown.util.UUIDCompressor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardPreview {

    private String id;
    private ResponseUserDetails owner;
    private String tag;
    private String cite;
    private String bodyText;
    private long timeCreatedAt;
    private long lastModified;
    private boolean deleted;

    public static CardPreview of(ResponseCard responseCard) {
        return new CardPreview(
                responseCard.getId(),
                responseCard.getOwner(),
                responseCard.getTag(),
                responseCard.getCite(),
                responseCard.getBodyText(),
                responseCard.getTimeCreatedAt(),
                responseCard.getLastModified(),
                responseCard.isDeleted()
        );
    }

    public static CardPreview of(CardEntity cardEntity, ResponseUserDetails owner) {
        return new CardPreview(
                UUIDCompressor.compress(cardEntity.getId()),
                owner,
                cardEntity.getTag(),
                cardEntity.getCite(),
                cardEntity.getBodyText(),
                cardEntity.getTimeCreatedAt(),
                cardEntity.getLastModified(),
                cardEntity.isDeleted()
        );
    }

    public void decryptFields(byte[] secretKey) throws Exception {
        tag = EncryptionUtils.decryptStringAES(tag, secretKey);
        cite = EncryptionUtils.decryptStringAES(cite, secretKey);
        bodyText = EncryptionUtils.decryptStringAES(bodyText, secretKey);
    }

}
