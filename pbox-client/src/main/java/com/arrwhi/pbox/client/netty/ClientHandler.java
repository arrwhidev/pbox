package com.arrwhi.pbox.client.netty;

import com.arrwhi.pbox.client.index.IndexService;
import com.arrwhi.pbox.msg.*;
import com.arrwhi.pbox.msg.metadata.MetaData;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.arrwhi.pbox.netty.PipelineAttributes.MESSAGE_TYPE;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private static Logger LOGGER = LogManager.getLogger();

    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        MessageType type = ctx.channel().attr(MESSAGE_TYPE).get();

        switch (type) {
            case TRANSPORT_FILE_ACK:
                handleTransportFileAck((TransportFileAckMessage) msg);
                break;
            case DELETE_FILE_ACK:
                handleDeleteFileAck((DeleteFileAckMessage) msg);
                break;
            default:
                LOGGER.error("Received an unsupported MessageType: " + type);
        }
    }

    private void handleTransportFileAck(TransportFileAckMessage msg) {
        MetaData md = msg.getMetaData();
        IndexService.INSTANCE.confirmTransportFileDelivery(md.getHash());
    }

    private void handleDeleteFileAck(DeleteFileAckMessage msg) {
        String path = msg.getMetaData().getFrom();
        IndexService.INSTANCE.confirmDeleteFileDelivery(path);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
