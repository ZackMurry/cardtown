package com.zackmurry.cardtown;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.Arrays;

import static com.zackmurry.cardtown.util.EncryptionUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class EncryptionUtilsTest {

    @DisplayName("Test converting between hex and bytes")
    @Test
    public void testHexConversion() {
        assertEquals("55", bytesToHex(new byte[] { 0b01010101 }));
        assertEquals("ffff", bytesToHex(new byte[] { (byte) 0b11111111, (byte) 0b11111111 }));
        assertEquals(Arrays.toString(new byte[] { 0b01010101 }), Arrays.toString(hexToBytes("55")));
        assertEquals(Arrays.toString(new byte[] { (byte) 0b11111111, (byte) 0b11111111 }), Arrays.toString(hexToBytes("ffff")));
        assertEquals(Arrays.toString(hexToBytes("fa243eda")), Arrays.toString(hexToBytes(bytesToHex(hexToBytes("fa243eda")))));
        assertEquals(Arrays.toString(hexToBytes("aed394327243")), Arrays.toString(hexToBytes(bytesToHex(hexToBytes("aed394327243")))));
    }

    @DisplayName("Test SHA-256")
    @Test
    public void testSHA256() {
        assertEquals("9d96ebdd11f9b46ee69ed015d10356d52cb39dd767d39f43797cb778cbee09b6", getSHA256HashHex("this is a test password"));
        assertEquals("6bd0457012c5c38efea110f89ead7bcd7c667052513439d7f49d3a10e42a77cf", getSHA256HashHex("37495245a28916bab6766c133fd42a39b46fa839c2a05c3f9cae2ffc95197e30"));
        assertEquals("49301f9d279c77bc5ff1ff8491f065c1276195096626bd7030d45aa1da40abc7", getSHA256HashHex("dakdsnadkdnsnklanksansadlm,nvcoiishiadhisadncjbnx"));
        final String sameText = "this is the same hash";
        assertEquals(getSHA256HashHex(sameText), getSHA256HashHex(sameText), "Two hashes of the same password should return the same result.");
    }

    @DisplayName("Test generation of AES key")
    @Test
    public void testGenerateAESKey() {
        assertDoesNotThrow(() -> generateStrongAESKey(256));
        assertEquals(32, generateStrongAESKey(256).getEncoded().length);
        assertThrows(InvalidParameterException.class, () -> generateStrongAESKey(2));
        assertThrows(InvalidParameterException.class, () -> generateStrongAESKey(23212321));
        assertThrows(InvalidParameterException.class, () -> generateStrongAESKey(255));
    }

    @DisplayName("Test encryption and decryption using AES")
    @Test
    public void testEncryptDecryptAES() {
        final String key = "9d7c93f1ac171ee076a98b6bbf826d1d65f80f480b98805148c6f957dc665ae9";
        String toBeEncrypted = "this is the first text";
        try {
            assertEquals(Arrays.toString(toBeEncrypted.getBytes(StandardCharsets.UTF_8)), Arrays.toString(decryptAES(encryptAES(toBeEncrypted.getBytes(StandardCharsets.UTF_8), hexToBytes(key)), hexToBytes(key))));
            toBeEncrypted = "this is another cool test that happens to be a bit longer";
            assertEquals(Arrays.toString(toBeEncrypted.getBytes(StandardCharsets.UTF_8)), Arrays.toString(decryptAES(encryptAES(toBeEncrypted.getBytes(StandardCharsets.UTF_8), hexToBytes(key)), hexToBytes(key))));
            assertNotEquals(toBeEncrypted.getBytes(StandardCharsets.UTF_8), encryptAES(toBeEncrypted.getBytes(StandardCharsets.UTF_8), hexToBytes(key)));
            assertEquals(toBeEncrypted, decryptStringAES(encryptStringAES(toBeEncrypted, hexToBytes(key)), hexToBytes(key)));
        } catch (Exception e) {
            fail("Valid encryption/decryption should never throw an exception", e);
        }
    }

}
