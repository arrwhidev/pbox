package com.arrwhi.pbox.integration;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by arran on 29/01/17.
 */
public class TestServerHandler extends ChannelInboundHandlerAdapter {

    private int numErrors = 0;
    private CountDownLatch latch;
    private List<byte[]> recvedMsgs = new ArrayList<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);

        recvedMsgs.add(data);
        if (latch != null) {
            latch.countDown();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        numErrors++;
        if (latch != null) {
            latch.countDown();
        }
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    public List<byte[]> getRecvedMsgs() {
        return recvedMsgs;
    }

    public boolean didHaveErrors() {
        return numErrors > 0;
    }
}