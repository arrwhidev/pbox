package com.arrwhi.pbox.netty;

import com.arrwhi.pbox.exception.InvalidMessageTypeException;
import com.arrwhi.pbox.msg.Message;
import com.arrwhi.pbox.msg.MessageFactory;
import com.arrwhi.pbox.msg.MessageType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.arrwhi.pbox.netty.PipelineAttributes.MESSAGE_TYPE;

public class MessageTypeDecoder extends ChannelInboundHandlerAdapter {

    private static Logger LOGGER = LogManager.getLogger();

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        MessageType type = readMessageType(buf);
        if (type != null) {
            ctx.channel().attr(MESSAGE_TYPE).set(type);
            Message message = MessageFactory.fromBuffer(buf, type);
            ctx.fireChannelRead(message);
        }
    }

    private MessageType readMessageType(ByteBuf buf) {
        try {
            MessageType type = MessageType.from(buf.readShort());
            buf.resetReaderIndex();
            return type;
        } catch (InvalidMessageTypeException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }
}
