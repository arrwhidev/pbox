package com.arrwhi.pbox.client;

import com.arrwhi.pbox.CollectingChannelHandlerContext;
import com.arrwhi.pbox.RandomTestUtils;
import io.netty.buffer.ByteBuf;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

/**
 * Created by arran on 05/02/17.
 */
public class LengthAndChunkEncoderTest {

    @Test
    public void shouldWriteLengthAs4BytesFollowedByBuffer() throws Exception {
        CollectingChannelHandlerContext ctx = new CollectingChannelHandlerContext();
        ByteBuf byteBuf = RandomTestUtils.randomByteBuf(123456);

        LengthAndChunkEncoder lengthAndChunkEncoder = new LengthAndChunkEncoder();
        lengthAndChunkEncoder.write(ctx, byteBuf, null);

        assertThat(ctx.buffer.readableBytes(), equalTo(123456));
    }
}