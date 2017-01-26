package com.arrwhi.pbox.client;

import com.arrwhi.pbox.exception.InvalidMessageTypeException;
import com.arrwhi.pbox.msg.MessageFactory;
import com.arrwhi.pbox.msg.TransportFileAckMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        handleTransportFileAck((ByteBuf) msg);
        // TODO: handle different acks.
    }

    private void handleTransportFileAck(ByteBuf src) {
        try {
            TransportFileAckMessage transportFileAck = MessageFactory.createTransportFileAckMessageFrom(src);
            // TODO: Handle ack
        } catch (InvalidMessageTypeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}