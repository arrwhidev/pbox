package com.arrwhi.pbox.server;

import com.arrwhi.pbox.util.PropertiesHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Arrays;
import java.util.List;

public class Server {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public static void main(String[] args) throws Exception {
        final int port = Integer.parseInt(PropertiesHelper.get("port"));
        final String destinationDir = PropertiesHelper.get("destinationDirectory");
        FileWriter writer = new FileWriter(destinationDir);

        Server server = new Server();
        ChannelFuture f = server.start(port, Arrays.asList(
                new ServerHandler(writer)
        ));
        System.out.println("Server started on localhost:" + port);
        f.channel().closeFuture().sync();
    }

    public ChannelFuture start(int port, List<ChannelInboundHandlerAdapter> handlers) throws Exception {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LengthAndChunkDecoder());
                        for (ChannelInboundHandlerAdapter handler : handlers) {
                            ch.pipeline().addLast(handler);
                        }
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

class LengthAndChunkDecoder extends ChannelInboundHandlerAdapter {

    private boolean waitingForLength = true;
    private int expecting = 0;
    private int bytesRead = 0;
    private ByteBuf data;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;

        if(waitingForLength) {
            int length = buf.readInt();
            data = Unpooled.buffer(length);
            expecting = length;
            waitingForLength = false;
        }

        bytesRead += buf.readableBytes();
        data.writeBytes(buf);
        buf.release();

        if (bytesRead == expecting) {
            waitingForLength = true;
            bytesRead = 0;
            expecting = 0;
            ctx.fireChannelRead(data);
            data.release();
        }
    }
}