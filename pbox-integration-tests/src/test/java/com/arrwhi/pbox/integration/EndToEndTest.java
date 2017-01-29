package com.arrwhi.pbox.integration;

import com.arrwhi.pbox.RandomTestUtils;
import com.arrwhi.pbox.client.Client;
import com.arrwhi.pbox.client.index.difference.DifferenceType;
import com.arrwhi.pbox.server.Server;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

/**
 * Created by arran on 28/01/17.
 */
public class EndToEndTest {

    Server server;
    ServerHandler serverHandler;

    Client client;
    Channel clientChannel;

    @Before
    public void setUp() throws Exception {
        final CountDownLatch connectionLatch = new CountDownLatch(1);

        serverHandler = new ServerHandler();
        server = new Server();
        ChannelFuture serverFuture = server.start(9991, Arrays.asList(serverHandler));
        serverFuture.awaitUninterruptibly();

        client = new Client();
        ChannelFuture clientFuture = client.start("localhost", 9991, new ClientHandler());
        TestFutureListener futureListener = new TestFutureListener(connectionLatch);
        clientFuture.addListener(futureListener);
        clientChannel = clientFuture.channel();
        clientFuture.awaitUninterruptibly();

        connectionLatch.await();
    }

    @Test
    public void shouldRecvSentMessage() throws Exception {
        final int numMsg = 2;
        final CountDownLatch messageLatch = new CountDownLatch(numMsg);
        serverHandler.setLatch(messageLatch);

        ByteBuf buf1 = RandomTestUtils.randomByteBuf(16);
        ByteBuf buf2 = RandomTestUtils.randomByteBuf(16);
        clientChannel.writeAndFlush(buf1);
        clientChannel.writeAndFlush(buf2);

        messageLatch.await();

        assertThat(serverHandler.getRecvedMsgs().size(), equalTo(numMsg));
        // TODO: Assert that sent buffers are equal.
        // assertThat(serverHandler.getRecvedMsgs().get(0).compareTo(buf1), equalTo(0));
    }
}

// TODO: Tidy all this junk up.

class TestFutureListener implements ChannelFutureListener {

    CountDownLatch latch;
    Channel channel;

    TestFutureListener(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void operationComplete(ChannelFuture future) {
        channel = future.channel();
        latch.countDown();
    }
}

class ClientHandler extends ChannelInboundHandlerAdapter {

    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        // TODO
    }
}

class ServerHandler extends ChannelInboundHandlerAdapter {

    private CountDownLatch latch;
    private List<ByteBuf> recvedMsgs = new ArrayList<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        recvedMsgs.add((ByteBuf)msg);
        if (latch != null) {
            latch.countDown();
        }
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    public List<ByteBuf> getRecvedMsgs() {
        return recvedMsgs;
    }
}