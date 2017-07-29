package com.arrwhi.pbox.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * Created by arran on 12/02/17.
 */
public class LengthAndChunkEncoder extends ChannelOutboundHandlerAdapter {

    private final static int CHUNK_SIZE_IN_BYTES = 1024 * 8; // 8k

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf buf = (ByteBuf) msg;

        // First write length as 4 bytes.
        final int length = buf.readableBytes();
        ByteBuf lengthBuffer = Unpooled.buffer();
        lengthBuffer.writeInt(length);
        ctx.writeAndFlush(lengthBuffer);

        // Write chunks.
        int offset = 0;
        byte[] data = new byte[CHUNK_SIZE_IN_BYTES];
        while (offset < length) {
            if(length - offset < CHUNK_SIZE_IN_BYTES) {
                data = new byte[length - offset];
            }

            buf.readBytes(data);
            ctx.write(Unpooled.copiedBuffer(data));

            offset += CHUNK_SIZE_IN_BYTES;
        }
        ctx.flush();
        buf.release();
    }
}

