package com.arrwhi.pbox.client.netty;

import com.arrwhi.pbox.exception.InvalidMessageTypeException;
import com.arrwhi.pbox.json.MetaData;
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
            // TODO: Handle ack
            TransportFileAckMessage transportFileAck = MessageFactory.createTransportFileAckMessageFromBuffer(src);
            MetaData md = MetaData.fromJsonBytes(transportFileAck.getMetaData());
            System.out.println("Got hash: " + md.getHash());
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