package com.arrwhi.pbox.server;

import com.arrwhi.pbox.util.PropertiesHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class Server {
    
    public static void main(String[] args) throws Exception {
        new Server().run();
    }

    public void run() throws Exception {
        final int port = Integer.parseInt(PropertiesHelper.get("port"));
        final String destinationDir = PropertiesHelper.get("destinationDirectory");

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1048576, 0, 8, 0, 8));
                     ch.pipeline().addLast(new LengthFieldPrepender(8));
//                     ch.pipeline().addLast(new MessageTypeHandler());
                     
                     FileWriter writer = new FileWriter(destinationDir);
                     ch.pipeline().addLast(new ServerHandler(writer));
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)
             .childOption(ChannelOption.SO_KEEPALIVE, true);
            
            ChannelFuture f = b.bind(port).sync();
            System.out.println("Server started on localhost:" + port);
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
