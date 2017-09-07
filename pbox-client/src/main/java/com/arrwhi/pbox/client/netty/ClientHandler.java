package com.arrwhi.pbox.client.netty;

import com.arrwhi.pbox.client.index.IndexService;
import com.arrwhi.pbox.exception.InvalidMessageTypeException;
import com.arrwhi.pbox.msg.DeleteFileAckMessage;
import com.arrwhi.pbox.msg.metadata.MetaData;
import com.arrwhi.pbox.msg.MessageFactory;
import com.arrwhi.pbox.msg.TransportFileAckMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        ByteBuf src = (ByteBuf) msg;
        short messageType = src.readShort();
        src.resetReaderIndex();

        switch(messageType) {
            case MessageFactory.TRANSPORT_FILE_ACK:
                handleTransportFileAck(src);
                break;
            case MessageFactory.DELETE_FILE_ACK:
                handleDeleteFileAck(src);
                break;
            default:
                System.out.println("Unexpected message type:" + messageType);
        }
    }

    private void handleTransportFileAck(ByteBuf src) {
        try {
            TransportFileAckMessage transportFileAck = MessageFactory.createTransportFileAckMessageFromBuffer(src);
            MetaData md = transportFileAck.getMetaData();
            IndexService.INSTANCE.confirmTransportFileDelivery(md.getHash());
        } catch (InvalidMessageTypeException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteFileAck(ByteBuf src) {
        try {
            DeleteFileAckMessage deleteFileAck = MessageFactory.createDeleteMessageAckFromBuffer(src);
            String path = deleteFileAck.getMetaData().getFrom();
            IndexService.INSTANCE.confirmDeleteFileDelivery(path);
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
