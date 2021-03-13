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
public class CardHeader {

    private String id;
    private ResponseUserDetails owner;
    private String tag;
    private String cite;
    private long timeCreatedAt; // todo maybe remove times from CardHeader
    private long lastModified;

    public static CardHeader of(ResponseCard responseCard) {
        return new CardHeader(
                responseCard.getId(),
                responseCard.getOwner(),
                responseCard.getTag(),
                responseCard.getCite(),
                responseCard.getTimeCreatedAt(),
                responseCard.getLastModified()
        );
    }

    public static CardHeader of(CardEntity cardEntity, ResponseUserDetails owner) {
        return new CardHeader(
                UUIDCompressor.compress(cardEntity.getId()),
                owner,
                cardEntity.getTag(),
                cardEntity.getCite(),
                cardEntity.getTimeCreatedAt(),
                cardEntity.getLastModified()
        );
    }

    public void decryptFields(byte[] secretKey) throws Exception {
        tag = EncryptionUtils.decryptStringAES(tag, secretKey);
        cite = EncryptionUtils.decryptStringAES(cite, secretKey);
    }

}
