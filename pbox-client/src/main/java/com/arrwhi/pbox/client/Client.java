package com.arrwhi.pbox.client;

import com.arrwhi.pbox.client.netty.ClientHandler;
import com.arrwhi.pbox.client.netty.ServerChannelFutureListener;
import com.arrwhi.pbox.netty.LengthAndChunkDecoder;
import com.arrwhi.pbox.netty.LengthAndChunkEncoder;
import com.arrwhi.pbox.util.PropertiesHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {

    private EventLoopGroup workerGroup;

    public static void main(String[] args) throws Exception {
        final String host = PropertiesHelper.get("serverHost");
        final int port = Integer.parseInt(PropertiesHelper.get("serverPort"));

        Client client = new Client();
        ChannelFuture f = client.start(host, port, new ClientHandler());
        f.addListener(new ServerChannelFutureListener());

        // Wait until the connection is closed.
        f.channel().closeFuture().sync();
    }

    public ChannelFuture start(String serverHost, int serverPort, ChannelInboundHandlerAdapter clientHandler) throws Exception {
        workerGroup = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LengthAndChunkEncoder());
                ch.pipeline().addLast(new LengthAndChunkDecoder());
                ch.pipeline().addLast(clientHandler);
            }
        });

        // Connect to the server
        return b.connect(serverHost, serverPort).sync();
    }

    public void stop() {
        workerGroup.shutdownGracefully().awaitUninterruptibly();
    }
}