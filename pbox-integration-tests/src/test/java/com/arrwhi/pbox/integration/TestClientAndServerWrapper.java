//package com.arrwhi.pbox.integration;
//
//import com.arrwhi.pbox.client.Client;
//import com.arrwhi.pbox.server.Server;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelFuture;
//
//import java.util.Arrays;
//import java.util.concurrent.CountDownLatch;
//
///**
// * Created by arran on 29/01/17.
// */
//public class TestClientAndServerWrapper {
//
//    private final static int SERVER_PORT = 9991;
//
//    private Server server;
//    private TestServerHandler serverHandler;
//    private Client client;
//    private Channel clientChannel;
//
//    public void start() throws Exception {
//        final CountDownLatch connectionLatch = new CountDownLatch(1);
//
//        serverHandler = new TestServerHandler();
//        server = new Server();
//        ChannelFuture serverFuture = server.start(SERVER_PORT, Arrays.asList(serverHandler));
//        serverFuture.awaitUninterruptibly();
//        System.out.println("Server started.");
//
//        client = new Client();
//        ChannelFuture clientFuture = client.start("localhost", SERVER_PORT, new TestClientHandler());
//        WaitingChannelFutureListener futureListener = new WaitingChannelFutureListener(connectionLatch);
//        clientFuture.addListener(futureListener);
//        clientChannel = clientFuture.channel();
//        clientFuture.awaitUninterruptibly();
//        System.out.println("Client started.");
//
//        connectionLatch.await();
//    }
//
//    public void shutdown() {
//        System.out.println("Stopping client & server...");
//        client.stop();
//        System.out.println("Client stopped.");
//        server.stop();
//        System.out.println("Server stopped.");
//    }
//
//    public TestServerHandler getServerHandler() {
//        return serverHandler;
//    }
//
//    public Channel getClientChannel() {
//        return clientChannel;
//    }
//}
