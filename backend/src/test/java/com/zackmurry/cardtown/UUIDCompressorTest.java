package com.zackmurry.cardtown;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.BufferUnderflowException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.zackmurry.cardtown.util.UUIDCompressor.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class UUIDCompressorTest {

    @DisplayName("Test invalid and valid UUIDs")
    @Test
    public void testInvalidId() {
        assertThrows(BufferUnderflowException.class, () -> decompress(Base64.encodeBase64String("not valid".getBytes(StandardCharsets.UTF_8))));
        assertDoesNotThrow(() -> decompress(Base64.encodeBase64String(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8))));
    }

    @DisplayName("Test consistency between encoding and decoding")
    @Test
    public void testEncoding() {
        for (int i = 0; i < 100; i++) {
            final UUID uuid = UUID.randomUUID();
            assertEquals(uuid, decompress(compress(uuid)));
        }
    }

}
