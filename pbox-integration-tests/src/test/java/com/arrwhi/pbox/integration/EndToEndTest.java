//package com.arrwhi.pbox.integration;
//
//import com.arrwhi.pbox.RandomTestUtils;
//import io.netty.buffer.Unpooled;
//import io.netty.channel.*;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//
//import static org.hamcrest.CoreMatchers.equalTo;
//import static org.hamcrest.junit.MatcherAssert.assertThat;
//
///**
// * Created by arran on 28/01/17.
// */
//public class EndToEndTest {
//
//    private TestClientAndServerWrapper wrapper;
//    private TestServerHandler serverHandler;
//    private Channel clientChannel;
//
//    @Before
//    public void setUp() throws Exception {
//        wrapper = new TestClientAndServerWrapper();
//        wrapper.start();
//
//        serverHandler = wrapper.getServerHandler();
//        clientChannel = wrapper.getClientChannel();
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        wrapper.shutdown();
//    }
//
//    @Test
//    public void shouldHandleSomeSmallMessages() throws Exception {
//        final int NUM_MSGS = 20;
//        final CountDownLatch messageLatch = new CountDownLatch(NUM_MSGS);
//        serverHandler.setLatch(messageLatch);
//
//        List<byte[]> buffersToWrite = new ArrayList<>();
//        for(int i = 0; i < NUM_MSGS; i++) {
//            byte[] msg = RandomTestUtils.randomBytes(16); // TODO: randomize the size too.
//            buffersToWrite.add(msg);
//            clientChannel.writeAndFlush(Unpooled.copiedBuffer(msg));
//        }
//        messageLatch.await();
//
//        assertThat(serverHandler.didHaveErrors(), equalTo(false));
//        assertThat(serverHandler.getRecvedMsgs().size(), equalTo(NUM_MSGS));
//        for(int i = 0; i < NUM_MSGS; i++) {
//            assertThat(buffersToWrite.get(i), equalTo(serverHandler.getRecvedMsgs().get(i)));
//        }
//    }
//
//    @Test
//    public void shouldHandleLargeMessage() throws Exception {
//        final int numMsg = 1;
//        final CountDownLatch messageLatch = new CountDownLatch(numMsg);
//        serverHandler.setLatch(messageLatch);
//        byte[] buf1 = RandomTestUtils.randomBytes(1024 * 1024 * 10);
//        clientChannel.writeAndFlush(Unpooled.copiedBuffer(buf1));
//        messageLatch.await();
//
//        assertThat(serverHandler.didHaveErrors(), equalTo(false));
//        assertThat(serverHandler.getRecvedMsgs().size(), equalTo(numMsg));
//        assertThat(buf1, equalTo(serverHandler.getRecvedMsgs().get(0)));
//    }
//
//    @Test
//    public void shouldHandleLotsOfSmallMessages() throws Exception {
//        final int NUM_MSGS = 1000 * 1000;
//        final CountDownLatch messageLatch = new CountDownLatch(NUM_MSGS);
//        serverHandler.setLatch(messageLatch);
//
//        List<byte[]> buffersToWrite = new ArrayList<>();
//        for(int i = 0; i < NUM_MSGS; i++) {
//            byte[] msg = RandomTestUtils.randomBytes(16); // TODO: randomize the size too.
//            buffersToWrite.add(msg);
//            clientChannel.writeAndFlush(Unpooled.copiedBuffer(msg));
//        }
//        messageLatch.await();
//
//        assertThat(serverHandler.didHaveErrors(), equalTo(false));
//        assertThat(serverHandler.getRecvedMsgs().size(), equalTo(NUM_MSGS));
//        for(int i = 0; i < NUM_MSGS; i++) {
//            assertThat(buffersToWrite.get(i), equalTo(serverHandler.getRecvedMsgs().get(i)));
//        }
//    }
//
//    @Test
//    @Ignore
//    public void shouldHandleVeryLargeMessage() throws Exception {
//        // TODO
//        final int MSG_SIZE = 1024 * 1024 * 1024; // 1GB - throws heap error
//    }
//}