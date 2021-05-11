package com.zackmurry.cardtown.model.analytic;

import com.zackmurry.cardtown.util.EncryptionUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EncryptedAnalytic {

    protected String body;

    public void encryptFields(byte[] secretKey) throws Exception {
        body = EncryptionUtils.encryptStringAES(body, secretKey);
    }

    public void decryptFields(byte[] secretKey) throws Exception {
        body = EncryptionUtils.decryptStringAES(body, secretKey);
    }

}
