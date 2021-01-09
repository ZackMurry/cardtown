package com.zackmurry.cardtown.model.arg;

import com.zackmurry.cardtown.util.EncryptionUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncryptedArgument {

    protected String name;

    // only encrypts the name because that's the only thing that's practical
    public void encryptFields(byte[] secretKey) throws Exception {
        name = EncryptionUtils.encryptStringAES(name, secretKey);
    }

    public void decryptFields(byte[] secretKey) throws Exception {
        name = EncryptionUtils.decryptStringAES(name, secretKey);
    }

}
