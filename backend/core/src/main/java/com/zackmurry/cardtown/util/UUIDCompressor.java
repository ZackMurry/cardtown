package com.zackmurry.cardtown.util;

import org.springframework.lang.NonNull;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

public class UUIDCompressor {

    public static String compress(@NonNull UUID uuid) {
        final ByteBuffer bb = ByteBuffer.allocate(Long.BYTES * 2);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        final byte[] array = bb.array();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(array);
    }

    public static UUID decompress(@NonNull String compressed) {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(Base64.getUrlDecoder().decode(compressed));
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

}
