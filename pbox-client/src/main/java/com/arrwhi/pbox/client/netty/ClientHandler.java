package com.arrwhi.pbox.client.netty;

import com.arrwhi.pbox.client.index.Index;
import com.arrwhi.pbox.client.index.IndexEntry;
import com.arrwhi.pbox.client.index.IndexEntryNotFoundException;
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

    private IndexIO indexio;

    public ClientHandler() {
        indexio = new IndexIO(PropertiesHelper.get("indexFilePath"));
    }
    
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        // TODO: handle different acks.
        handleTransportFileAck((ByteBuf) msg);
    }

    private void handleTransportFileAck(ByteBuf src) {
        try {
            TransportFileAckMessage transportFileAck = MessageFactory.createTransportFileAckMessageFromBuffer(src);
            MetaData md = transportFileAck.getMetaData();

            // TODO: Need a singleton Index wrapper so we do not do read it every time.
            Index index = indexio.read();
            try {
                IndexEntry ie = index.getByHash(md.getHash());
                ie.setSynced(true);
                indexio.write(index);
            } catch (IndexEntryNotFoundException e) {
                e.printStackTrace();
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