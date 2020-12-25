package com.zackmurry.cardtown.util;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

public class UUIDUtils {

    public static String compress(UUID uuid) {
        ByteBuffer bb = ByteBuffer.allocate(Long.BYTES * 2);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        byte[] array = bb.array();
        return Base64.getEncoder().encodeToString(array);
    }

    // todo unit test for decompressing an invalid uuid
    public static UUID decompress(String compressed) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(Base64.getDecoder().decode(compressed));
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

}
