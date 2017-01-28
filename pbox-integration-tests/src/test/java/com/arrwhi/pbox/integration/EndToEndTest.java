package com.arrwhi.pbox.integration;

import com.arrwhi.pbox.client.Client;
import com.arrwhi.pbox.server.Server;
import io.netty.channel.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

/**
 * Created by arran on 28/01/17.
 */
public class EndToEndTest {

    Server server;
    Channel serverChannel;

    Client client;
    Channel clientChannel;

    @Before
    public void setUp() throws Exception {
        final CountDownLatch connectionLatch = new CountDownLatch(1);

        server = new Server();
        ChannelFuture serverFuture = server.start(9991, Arrays.asList(
            new ServerHandler()
        ));
        serverChannel = serverFuture.channel();
        serverFuture.awaitUninterruptibly();
        System.out.println("Started server...");

        client = new Client();
        ChannelFuture clientFuture = client.start("localhost", 9991, new ClientHandler());
        TestFutureListener futureListener = new TestFutureListener(connectionLatch);
        clientFuture.addListener(futureListener);
        clientChannel = clientFuture.channel();
        clientFuture.awaitUninterruptibly();

        connectionLatch.await();
        System.out.println("Started client...");
    }

    @Test
    public void testing() throws Exception {
//        final CountDownLatch messageLatch = new CountDownLatch(1);
//
//        byte[] packet = new byte[3];
//        packet[0] = 'a';
//        packet[0] = 'b';
//        packet[0] = 'c';
//        clientChannel.writeAndFlush(packet);
//        System.out.println("Wrote msg to server.");
//
//        messageLatch.await();
    }
}

class TestFutureListener implements ChannelFutureListener {

    CountDownLatch latch;
    Channel channel;

    TestFutureListener(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void operationComplete(ChannelFuture future) {
        System.out.println("Client successfully connected to server!");
        channel = future.channel();
        latch.countDown();
    }
}

class ClientHandler extends ChannelInboundHandlerAdapter {

    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println(1);
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(2);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(3);
        super.channelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println(4);
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println(5);
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println(6);
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(7);
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println(8);
        super.channelRegistered(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(9);
        super.channelRead(ctx, msg);
    }
}