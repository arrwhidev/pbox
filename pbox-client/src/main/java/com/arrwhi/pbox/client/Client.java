package com.arrwhi.pbox.client;

import com.arrwhi.pbox.util.PropertiesHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

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
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1048576, 0, 8, 0, 8));
                ch.pipeline().addLast(new LengthFieldPrepender(8));
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
