package com.arrwhi.pbox.util;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by arran on 29/07/17.
 */
public class UUIDHelper {

    public static UUID create() {
        return UUID.randomUUID();
    }

    public static byte[] toBytes(UUID id) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(id.getMostSignificantBits());
        bb.putLong(id.getLeastSignificantBits());
        return bb.array();
    }
}
