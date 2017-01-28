package com.arrwhi.pbox.server;

import com.arrwhi.pbox.util.PropertiesHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.Arrays;
import java.util.List;

public class Server {
    
    public static void main(String[] args) throws Exception {
        final int port = Integer.parseInt(PropertiesHelper.get("port"));
        final String destinationDir = PropertiesHelper.get("destinationDirectory");
        FileWriter writer = new FileWriter(destinationDir);

        Server server = new Server();
        ChannelFuture f = server.start(port, Arrays.asList(
//                new MessageTypeHandler(),
                new ServerHandler(writer)
        ));
        System.out.println("Server started on localhost:" + port);
        f.channel().closeFuture().sync();
    }

    public ChannelFuture start(int port,  List<ChannelInboundHandlerAdapter> handlers) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
         .channel(NioServerSocketChannel.class)
         .childHandler(new ChannelInitializer<SocketChannel>() {
             @Override
             public void initChannel(SocketChannel ch) throws Exception {
                 ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1048576, 0, 8, 0, 8));
                 ch.pipeline().addLast(new LengthFieldPrepender(8));
                 for(ChannelInboundHandlerAdapter handler : handlers) {
                     ch.pipeline().addLast(handler);
                 }
             }
         })
         .option(ChannelOption.SO_BACKLOG, 128)
         .childOption(ChannelOption.SO_KEEPALIVE, true);

        return b.bind(port).sync();
    }
}
