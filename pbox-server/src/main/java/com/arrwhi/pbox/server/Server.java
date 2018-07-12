package com.arrwhi.pbox.server;

import com.arrwhi.pbox.netty.*;
import com.arrwhi.pbox.util.PropertiesHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server {

    private static Logger LOGGER = LogManager.getLogger();

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public static void main(String[] args) throws Exception {
        final int port = Integer.parseInt(PropertiesHelper.get("port"));
        final String destinationDir = PropertiesHelper.get("destinationDirectory");
        FileWriter writer = new FileWriter(destinationDir);

        Server server = new Server();
        ChannelFuture f = server.start(port, writer);

        LOGGER.info("Server started on localhost:" + port);
        f.channel().closeFuture().sync();
    }

    private ChannelFuture start(int port, FileWriter writer) throws Exception {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new LengthAndChunkEncoder());
                        ch.pipeline().addLast(new LengthAndChunkDecoder());
                        ch.pipeline().addLast(new MessageTypeDecoder());
//                        ch.pipeline().addLast(new InboundMessageLogger());
//                        ch.pipeline().addLast(new OutboundMessageLogger());
                        ch.pipeline().addLast(new ServerHandler(writer));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        return b.bind(port).sync();
    }

    public void stop() {
        bossGroup.shutdownGracefully().awaitUninterruptibly();
        workerGroup.shutdownGracefully().awaitUninterruptibly();
    }
}