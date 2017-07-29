package com.arrwhi.pbox.netty;

import com.arrwhi.pbox.CollectingReadChannelHandlerContext;
import com.arrwhi.pbox.RandomTestUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

/**
 * Created by arran on 05/02/17.
 */
public class LengthAndChunkDecoderTest {

    @Test
    public void shouldHandleWhenLengthArrivesSeparatelyFromPayload() throws Exception {
        CollectingReadChannelHandlerContext ctx = new CollectingReadChannelHandlerContext();
        ByteBuf payload = RandomTestUtils.randomByteBuf(73);

        LengthAndChunkDecoder decoder = new LengthAndChunkDecoder();
        decoder.channelRead(ctx, lengthByteBufForPayload(payload));
        decoder.channelRead(ctx, payload);

        assertThat(ctx.buffers.get(0).readableBytes(), equalTo(73));
        assertThat(decoder.waitingForLength, equalTo(true));
        assertThat(decoder.expecting, equalTo(0));
        assertThat(decoder.bytesRead, equalTo(0));
    }

    @Test
    public void shouldHandleWhenLengthArrivesWithPayload() throws Exception {
        CollectingReadChannelHandlerContext ctx = new CollectingReadChannelHandlerContext();
        ByteBuf payload = RandomTestUtils.randomByteBuf(73);

        ByteBuf lengthAndPayload = Unpooled.copiedBuffer(lengthByteBufForPayload(payload), payload);

        LengthAndChunkDecoder decoder = new LengthAndChunkDecoder();
        decoder.channelRead(ctx, lengthAndPayload);

        assertThat(ctx.buffers.get(0).readableBytes(), equalTo(73));
        assertThat(decoder.waitingForLength, equalTo(true));
        assertThat(decoder.expecting, equalTo(0));
        assertThat(decoder.bytesRead, equalTo(0));
    }

    @Test
    public void shouldHandleWhenPayloadArrivesInMultipleParts() throws Exception {
        CollectingReadChannelHandlerContext ctx = new CollectingReadChannelHandlerContext();
        ByteBuf payload = RandomTestUtils.randomByteBuf(73);

        LengthAndChunkDecoder decoder = new LengthAndChunkDecoder();
        decoder.channelRead(ctx, lengthByteBufForPayload(payload));
        decoder.channelRead(ctx, payload.slice(0, 50));
        decoder.channelRead(ctx, payload.slice(0, 23));

        assertThat(ctx.buffers.get(0).readableBytes(), equalTo(73));
        assertThat(decoder.waitingForLength, equalTo(true));
        assertThat(decoder.expecting, equalTo(0));
        assertThat(decoder.bytesRead, equalTo(0));
    }

    @Test
    public void shouldHandleWhenPayloadIncludesAnotherLengthAndPayload() throws Exception {
        CollectingReadChannelHandlerContext ctx = new CollectingReadChannelHandlerContext();
        ByteBuf payload1 = RandomTestUtils.randomByteBuf(73);
        ByteBuf payload2 = RandomTestUtils.randomByteBuf(119);
        ByteBuf megaPayload = Unpooled.copiedBuffer(
            payload1,
            lengthByteBufForPayload(payload2),
            payload2
        );

        LengthAndChunkDecoder decoder = new LengthAndChunkDecoder();
        decoder.channelRead(ctx, lengthByteBufForPayload(payload1));
        decoder.channelRead(ctx, megaPayload);

        assertThat(ctx.buffers.size(), equalTo(2));
        assertThat(ctx.buffers.get(0).readableBytes(), equalTo(73));
        assertThat(ctx.buffers.get(1).readableBytes(), equalTo(119));
        assertThat(decoder.waitingForLength, equalTo(true));
        assertThat(decoder.expecting, equalTo(0));
        assertThat(decoder.bytesRead, equalTo(0));
    }

    @Test
    public void shouldHandleWhenPayloadIncludesAnotherLength() throws Exception {
        CollectingReadChannelHandlerContext ctx = new CollectingReadChannelHandlerContext();
        ByteBuf payload1 = RandomTestUtils.randomByteBuf(73);
        ByteBuf payload2 = RandomTestUtils.randomByteBuf(119);
        ByteBuf megaPayload = Unpooled.copiedBuffer(
                payload1,
                lengthByteBufForPayload(payload2)
        );

        LengthAndChunkDecoder decoder = new LengthAndChunkDecoder();
        decoder.channelRead(ctx, lengthByteBufForPayload(payload1));
        decoder.channelRead(ctx, megaPayload);
        decoder.channelRead(ctx, payload2);

        assertThat(ctx.buffers.size(), equalTo(2));
        assertThat(ctx.buffers.get(0).readableBytes(), equalTo(73));
        assertThat(ctx.buffers.get(1).readableBytes(), equalTo(119));
        assertThat(decoder.waitingForLength, equalTo(true));
        assertThat(decoder.expecting, equalTo(0));
        assertThat(decoder.bytesRead, equalTo(0));
    }

    @Test
    public void shouldHandleWhenTwoMessagesArriveTogether() throws Exception {
        CollectingReadChannelHandlerContext ctx = new CollectingReadChannelHandlerContext();
        ByteBuf payload1 = RandomTestUtils.randomByteBuf(73);
        ByteBuf payload2 = RandomTestUtils.randomByteBuf(119);
        ByteBuf megaPayload = Unpooled.copiedBuffer(
                lengthByteBufForPayload(payload1),
                payload1,
                lengthByteBufForPayload(payload2),
                payload2
        );

        LengthAndChunkDecoder decoder = new LengthAndChunkDecoder();
        decoder.channelRead(ctx, megaPayload);

        assertThat(ctx.buffers.size(), equalTo(2));
        assertThat(ctx.buffers.get(0).readableBytes(), equalTo(73));
        assertThat(ctx.buffers.get(1).readableBytes(), equalTo(119));
        assertThat(decoder.waitingForLength, equalTo(true));
        assertThat(decoder.expecting, equalTo(0));
        assertThat(decoder.bytesRead, equalTo(0));
    }

    @Test
    @Ignore("Come back to this, not sure if required...")
    public void shouldHandleWhenLengthFieldArrivesInTwoHalves() throws Exception {
        CollectingReadChannelHandlerContext ctx = new CollectingReadChannelHandlerContext();
        ByteBuf payload = RandomTestUtils.randomByteBuf(73);
        ByteBuf lengthBuffer = lengthByteBufForPayload(payload);

        LengthAndChunkDecoder decoder = new LengthAndChunkDecoder();
        decoder.channelRead(ctx, lengthBuffer.slice(0, 2));
        decoder.channelRead(ctx, lengthBuffer.slice(0, 2));
        decoder.channelRead(ctx, payload);

        assertThat(ctx.buffers.size(), equalTo(2));
        assertThat(ctx.buffers.get(0).readableBytes(), equalTo(73));
        assertThat(ctx.buffers.get(1).readableBytes(), equalTo(119));
        assertThat(decoder.waitingForLength, equalTo(true));
        assertThat(decoder.expecting, equalTo(0));
        assertThat(decoder.bytesRead, equalTo(0));
    }

    private ByteBuf lengthByteBufForPayload(ByteBuf payload) {
        ByteBuf lengthBuffer = Unpooled.buffer();
        lengthBuffer.writeInt(payload.readableBytes());
        return lengthBuffer;
    }
}