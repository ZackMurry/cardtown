package com.zackmurry.cardtown.model.card;

import com.zackmurry.cardtown.util.EncryptionUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EncryptedCard {

    protected String tag;
    protected String cite;
    protected String citeInformation;
    protected String bodyHtml;
    protected String bodyDraft;

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
