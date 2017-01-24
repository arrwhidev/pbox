package com.arrwhi.pbox;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Random;

public class RandomTestUtils {
    
    public static byte[] randomBytes(int size) {
        byte[] b = new byte[size];
        new Random().nextBytes(b);
        return b;
    }
    
    public static ByteBuf randomByteBuf(int size) {
        return Unpooled.wrappedBuffer(randomBytes(size));
    }
}
