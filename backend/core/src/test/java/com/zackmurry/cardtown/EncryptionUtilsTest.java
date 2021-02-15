package com.zackmurry.cardtown;

import com.zackmurry.cardtown.util.EncryptionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.Arrays;

import static com.zackmurry.cardtown.util.EncryptionUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class EncryptionUtilsTest {

    @Autowired
    private EncryptionUtils encryptionUtils;

    @DisplayName("Test SHA-256")
    @Test
    public void testSHA256() {
        for (int i = 0; i < 10; i++) {
            assertEquals("nZbr3RH5tG7mntAV0QNW1Syznddn059DeXy3eMvuCbY=", encryptionUtils.getSHA256HashBase64("this is a test password"));
            assertEquals("a9BFcBLFw47+oRD4nq17zXxmcFJRNDnX9J06EOQqd88=", encryptionUtils.getSHA256HashBase64("37495245a28916bab6766c133fd42a39b46fa839c2a05c3f9cae2ffc95197e30"));
            assertEquals("STAfnSecd7xf8f+EkfBlwSdhlQlmJr1wMNRaodpAq8c=", encryptionUtils.getSHA256HashBase64("dakdsnadkdnsnklanksansadlm,nvcoiishiadhisadncjbnx"));
            final String sameText = "this is the same hash";
            assertEquals(encryptionUtils.getSHA256HashBase64(sameText), encryptionUtils.getSHA256HashBase64(sameText), "Two hashes of the same password should return the same result.");
        }
    }

    @DisplayName("Test generation of AES key")
    @Test
    public void testGenerateAESKey() {
        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> generateStrongAESKey(256));
            assertEquals(32, generateStrongAESKey(256).getEncoded().length);
            assertThrows(InvalidParameterException.class, () -> generateStrongAESKey(2));
            assertThrows(InvalidParameterException.class, () -> generateStrongAESKey(23212321));
            assertThrows(InvalidParameterException.class, () -> generateStrongAESKey(255));
        }
    }

    @DisplayName("Test encryption and decryption using AES")
    @Test
    public void testEncryptDecryptAES() {
        final byte[] key = generateStrongAESKey(256).getEncoded();
        String toBeEncrypted = "this is the first text";
        try {
            assertEquals(Arrays.toString(toBeEncrypted.getBytes(StandardCharsets.UTF_8)), Arrays.toString(decryptAES(encryptAES(toBeEncrypted.getBytes(StandardCharsets.UTF_8), key), key)));
            toBeEncrypted = "this is another cool test that happens to be a bit longer";
            assertEquals(Arrays.toString(toBeEncrypted.getBytes(StandardCharsets.UTF_8)), Arrays.toString(decryptAES(encryptAES(toBeEncrypted.getBytes(StandardCharsets.UTF_8), key), key)));
            assertNotEquals(toBeEncrypted.getBytes(StandardCharsets.UTF_8), encryptAES(toBeEncrypted.getBytes(StandardCharsets.UTF_8), key));
            assertEquals(toBeEncrypted, decryptStringAES(encryptStringAES(toBeEncrypted, key), key));
        } catch (Exception e) {
            fail("Valid encryption/decryption should never throw an exception", e);
        }

        for (int i = 0; i < 100; i++) {
            toBeEncrypted = RandomStringUtils.randomAlphanumeric(25);
            try {
                assertEquals(Arrays.toString(toBeEncrypted.getBytes(StandardCharsets.UTF_8)), Arrays.toString(decryptAES(encryptAES(toBeEncrypted.getBytes(StandardCharsets.UTF_8), key), key)));
            } catch (Exception e) {
                fail("Valid encryption/decryption should never throw an exception", e);
            }
        }
    }

}
