package com.arrwhi.pbox.server;

import com.arrwhi.pbox.crypto.HashFactory;
import com.arrwhi.pbox.json.MetaData;
import com.arrwhi.pbox.msg.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import com.arrwhi.pbox.exception.InvalidMessageTypeException;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private FileWriter writer;

    public ServerHandler(FileWriter writer) {
        this.writer = writer;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        ByteBuf src = (ByteBuf) msg;
        short messageType = src.readShort();
        src.resetReaderIndex();

        switch(messageType) {
            case MessageFactory.TRANSPORT_FILE:
                handleTransportFile((ByteBuf) msg, ctx);
                break;
            case MessageFactory.DELETE_FILE:
                handleDeleteFile((ByteBuf) msg);
                writeAck(ctx, new DeleteFileAckMessage());
                break;
            default:
                System.out.println("Unexpected message type:" + messageType);
        }
    }

    void handleTransportFile(ByteBuf src, ChannelHandlerContext ctx) {
        try {
            TransportFileMessage msg = MessageFactory.createTransportFileMessageFromBuffer(src);
            MetaData recvMetaData = MetaData.fromJsonBytes(msg.getMetaData());
            String path = recvMetaData.getTo();
            String hash = HashFactory.create(path, msg.getPayload());

            if (recvMetaData.getHash().equals(hash)) {
                if (msg.getFlags().isDirectory()) {
                    writer.createDir(path);
                } else {
                    writer.write(path, msg.getPayload());
                }

                writeAck(ctx, MessageFactory.createTransportFileAckMessage(hash));
            } else {
                System.out.println("Invalid hash - throwing away data.");
            }
        } catch (InvalidMessageTypeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO - do not forget to handle the delete a directory scenario.
    void handleDeleteFile(ByteBuf src) {
        try {
            DeleteFileMessage msg = MessageFactory.createDeleteMessageFromBuffer(src);
            String path = MetaData.fromJsonBytes(msg.getMetaData()).getFrom();
            writer.delete(path);
        } catch (InvalidMessageTypeException e) {
            e.printStackTrace();
        }
    }

    private void writeAck(ChannelHandlerContext ctx, Message ack) {
        ctx.writeAndFlush(ack.writeToNewBuffer());
    }
}