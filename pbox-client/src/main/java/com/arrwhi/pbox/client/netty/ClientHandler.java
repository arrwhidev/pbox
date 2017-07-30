package com.arrwhi.pbox.client.netty;

import com.arrwhi.pbox.client.index.Index;
import com.arrwhi.pbox.client.index.IndexEntry;
import com.arrwhi.pbox.client.index.IndexIO;
import com.arrwhi.pbox.exception.InvalidMessageTypeException;
import com.arrwhi.pbox.msg.metadata.MetaData;
import com.arrwhi.pbox.msg.MessageFactory;
import com.arrwhi.pbox.msg.TransportFileAckMessage;
import com.arrwhi.pbox.util.PropertiesHelper;
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
            IndexIO io = new IndexIO(PropertiesHelper.get("indexFilePath"));
            Index index = io.read();
            IndexEntry ie = index.getByHash(md.getHash());

            if(ie != null) {
                ie.setSynced(true);
                io.write(index);
            } else {
                System.err.println("Did not find IndexEntry with hash: " + md.getHash());
            }
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