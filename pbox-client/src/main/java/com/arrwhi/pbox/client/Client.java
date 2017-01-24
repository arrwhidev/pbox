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

    public static void main(String[] args) throws Exception {
        new Client();
    }

    public Client() throws Exception {
        connectToServer();
    }

    private void connectToServer() throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1048576, 0, 8, 0, 8));
                    ch.pipeline().addLast(new LengthFieldPrepender(8));
                    ch.pipeline().addLast(new ClientHandler());
                }
            });

            // Connect to the server
            final String host = PropertiesHelper.get("serverHost");
            final int port = Integer.parseInt(PropertiesHelper.get("serverPort"));
            ChannelFuture f = b.connect(host, port).sync();
            f.addListener(new ServerChannelFutureListener());

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
