package com.arrwhi.pbox.server;

import com.arrwhi.pbox.crypto.HashFactory;
import com.arrwhi.pbox.msg.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.arrwhi.pbox.netty.PipelineAttributes.MESSAGE_TYPE;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static Logger LOGGER = LogManager.getLogger();
    private FileWriter writer;

    public ServerHandler(FileWriter writer) {
        this.writer = writer;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("Error caught: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        LOGGER.info("Client connected.");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        LOGGER.info("Client disconnected.");
    }

    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        MessageType type = ctx.channel().attr(MESSAGE_TYPE).get();

        switch (type) {
            case TRANSPORT_FILE:
                handleTransportFile((TransportFileMessage) msg, ctx);
                break;
            case DELETE_FILE:
                handleDeleteFile((DeleteFileMessage) msg, ctx);
                break;
            default:
                LOGGER.error("Received an unsupported MessageType: " + type);
        }
    }

    void handleTransportFile(TransportFileMessage msg, ChannelHandlerContext ctx) {
        String path = msg.getMetaData().getTo();
        String hash = HashFactory.create(path, msg.getPayload());

        if (msg.getMetaData().getHash().equals(hash)) {
            if (msg.getFlags().isDirectory()) {
                writer.createDir(path);
            } else {
                writer.write(path, msg.getPayload());
            }

            writeAck(ctx, MessageFactory.createTransportFileAckMessage(hash));
        } else {
            LOGGER.error("Invalid hash - throwing away data.");
        }
    }

    // TODO - do not forget to handle the delete a directory scenario.
    void handleDeleteFile(DeleteFileMessage msg, ChannelHandlerContext ctx) {
        String path = msg.getMetaData().getFrom();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("RECV [DeleteFileMessage] Path=" + path);
        }

        writer.delete(path);
        writeAck(ctx, MessageFactory.createDeleteFileAckMessage(path));
    }

    private void writeAck(ChannelHandlerContext ctx, Message ack) {
        ctx.writeAndFlush(ack.writeToNewBuffer());
    }
}