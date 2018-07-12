package com.arrwhi.pbox.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by arran on 12/02/17.
 */
public class LengthAndChunkDecoder extends ChannelInboundHandlerAdapter {

    boolean waitingForLength = true;
    int expecting = 0;
    int bytesRead = 0;
    ByteBuf data;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;

        while (buf.readableBytes() > 0) {
            if(waitingForLength) {
                int length = buf.readInt();
                data = Unpooled.buffer(length);
                expecting = length;
                waitingForLength = false;
            }

            int bytesRemaining = expecting - bytesRead;
            if (buf.readableBytes() >= bytesRemaining) {
                // Buffer contains everything we need so just read what is required.
                bytesRead += bytesRemaining;
                buf.readBytes(data, bytesRemaining);

                // We are finished with this message, reset!
                waitingForLength = true;
                bytesRead = 0;
                expecting = 0;
                ctx.fireChannelRead(data);
                data.release();
            } else {
                // Buffer doesn't contain enough, gonna need more!
                bytesRead += buf.readableBytes();
                data.writeBytes(buf);
            }
        }
    }
}