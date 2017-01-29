package com.arrwhi.pbox.client;

import com.arrwhi.pbox.util.PropertiesHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.Arrays;

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
//                ch.pipeline().addLast(new LengthAndChunkEncoder());
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

//
//class LengthAndChunkEncoder extends ChannelOutboundHandlerAdapter {
//    @Override
//    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//        ByteBuf buf = (ByteBuf) msg;
//
//        // First write length as 8 bytes.
//        buf.readableBytes();
//
//
//
//        // now chunk and write....
//
//
////        int bytesToWrite = buf1.length;
////        int offset = 0;
////        final int chunkSize = 1024;
////        while (offset < buf1.length) {
////            int endIndex = offset + chunkSize;
////            if (endIndex > buf1.length) endIndex = buf1.length;
////            ByteBuf buffer = Unpooled.copiedBuffer(Arrays.copyOfRange(buf1, offset, endIndex));
////
////            System.out.println("Offset: " + offset);
////            System.out.println("EndIndex: " + endIndex);
////
////            while (!clientChannel.isWritable()) {
////                System.out.println("waiting for channel to be writable...");
////                Thread.sleep(10);
////            }
////            clientChannel.write(buffer);
////            offset += chunkSize;
////        }
//
//    }
//}

