package com.arrwhi.pbox.netty;

import com.arrwhi.pbox.msg.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.arrwhi.pbox.netty.PipelineAttributes.MESSAGE_TYPE;

public class OutboundMessageLogger extends ChannelOutboundHandlerAdapter {

    private static Logger LOGGER = LogManager.getLogger();

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        MessageType type = ctx.channel().attr(MESSAGE_TYPE).get();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("---> SEND ["+type+"]");
        }

        super.write(ctx, msg, promise);
    }
}
