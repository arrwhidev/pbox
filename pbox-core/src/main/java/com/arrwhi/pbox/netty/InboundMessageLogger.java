package com.arrwhi.pbox.netty;

import com.arrwhi.pbox.msg.MessageType;
import io.netty.channel.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.arrwhi.pbox.netty.PipelineAttributes.MESSAGE_TYPE;

public class InboundMessageLogger extends ChannelInboundHandlerAdapter {

    private static Logger LOGGER = LogManager.getLogger();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageType type = ctx.channel().attr(MESSAGE_TYPE).get();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("---> RECV ["+type+"]");
        }

        super.channelRead(ctx, msg);
    }
}
