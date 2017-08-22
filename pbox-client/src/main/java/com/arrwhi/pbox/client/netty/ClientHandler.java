package com.arrwhi.pbox.client.netty;

import com.arrwhi.pbox.client.index.*;
import com.arrwhi.pbox.exception.InvalidMessageTypeException;
import com.arrwhi.pbox.msg.metadata.MetaData;
import com.arrwhi.pbox.msg.MessageFactory;
import com.arrwhi.pbox.msg.TransportFileAckMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        // TODO: handle different acks.
        handleTransportFileAck((ByteBuf) msg);
    }

    private void handleTransportFileAck(ByteBuf src) {
        try {
            TransportFileAckMessage transportFileAck = MessageFactory.createTransportFileAckMessageFromBuffer(src);
            MetaData md = transportFileAck.getMetaData();

            // TODO: Come back and use the IndexService.
            //IndexService.INSTANCE.confirmTransportFileDelivery(md.getHash());
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
