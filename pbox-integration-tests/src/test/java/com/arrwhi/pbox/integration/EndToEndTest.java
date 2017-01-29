package com.arrwhi.pbox.integration;

import com.arrwhi.pbox.RandomTestUtils;
import com.arrwhi.pbox.client.Client;
import com.arrwhi.pbox.server.Server;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

/**
 * Created by arran on 28/01/17.
 */
public class EndToEndTest {

    private TestClientAndServerWrapper wrapper;
    private TestServerHandler serverHandler;
    private Channel clientChannel;

    @Before
    public void setUp() throws Exception {
        wrapper = new TestClientAndServerWrapper();
        wrapper.start();

        serverHandler = wrapper.getServerHandler();
        clientChannel = wrapper.getClientChannel();
    }

    @After
    public void tearDown() throws Exception {
        wrapper.shutdown();
    }

    @Test
    public void shouldHandleSmallMessages() throws Exception {
        final int numMsg = 2;
        final CountDownLatch messageLatch = new CountDownLatch(numMsg);
        serverHandler.setLatch(messageLatch);

        byte[] buf1 = RandomTestUtils.randomBytes(16);
        byte[] buf2 = RandomTestUtils.randomBytes(32);
        clientChannel.writeAndFlush(Unpooled.copiedBuffer(buf1));
        clientChannel.writeAndFlush(Unpooled.copiedBuffer(buf2));
        messageLatch.await();

        assertThat(serverHandler.didHaveErrors(), equalTo(false));
        assertThat(serverHandler.getRecvedMsgs().size(), equalTo(numMsg));
        assertThat(buf1, equalTo(serverHandler.getRecvedMsgs().get(0)));
        assertThat(buf2, equalTo(serverHandler.getRecvedMsgs().get(1)));
    }

    @Test
    public void shouldHandleLargeMessage() throws Exception {
        final int numMsg = 1;
        final CountDownLatch messageLatch = new CountDownLatch(numMsg);
        serverHandler.setLatch(messageLatch);

        byte[] buf1 = RandomTestUtils.randomBytes(1048576 * 2); // Larger than max!
        clientChannel.writeAndFlush(Unpooled.copiedBuffer(buf1));
        messageLatch.await();

        assertThat(serverHandler.didHaveErrors(), equalTo(false));
        assertThat(serverHandler.getRecvedMsgs().size(), equalTo(numMsg));
        assertThat(buf1, equalTo(serverHandler.getRecvedMsgs().get(0)));
    }
}