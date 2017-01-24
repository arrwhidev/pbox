package com.arrwhi.pbox.server;

import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by arran on 10/07/16.
 */
public class MessageTypeHandler extends ChannelInboundHandlerAdapter {

//    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
//        ByteBuf src = (ByteBuf) msg;
//        short messageType = src.readShort();
//        switch(messageType) {
//            case MessageFactory.TRANSPORT_FILE:
//                Log.out("Received TRANSPORT_FILE");
//                break;
//            case MessageFactory.DELETE_FILE:
//                Log.out("Received DELETE_FILE");
//                break;
//            default:
//                Log.out("Unexpected message type:" + messageType);
//        }
//    }
}
