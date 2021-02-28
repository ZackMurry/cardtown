package com.zackmurry.cardtown.util;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

public class UUIDCompressor {

    public static String compress(UUID uuid) {
        final ByteBuffer bb = ByteBuffer.allocate(Long.BYTES * 2);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        final byte[] array = bb.array();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(array);
    }

    public static UUID decompress(String compressed) {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(Base64.getUrlDecoder().decode(compressed));
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

}
