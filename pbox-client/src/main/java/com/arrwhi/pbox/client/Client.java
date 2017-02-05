package com.arrwhi.pbox.client;

import com.arrwhi.pbox.util.PropertiesHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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


class LengthAndChunkEncoder extends ChannelOutboundHandlerAdapter {

    private final static int CHUNK_SIZE_IN_BYTES = 1024 * 8;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf buf = (ByteBuf) msg;

        // First write length as 4 bytes.
        final int length = buf.readableBytes();
        ByteBuf lengthBuffer = Unpooled.buffer();
        lengthBuffer.writeInt(length);
        ctx.write(lengthBuffer);
        lengthBuffer.release();

        // Write chunks.
        int offset = 0;
        byte[] data = new byte[CHUNK_SIZE_IN_BYTES];
        while (offset < length) {
            if(length - offset < CHUNK_SIZE_IN_BYTES) {
                data = new byte[length - offset];
            }

            buf.readBytes(data);
            ctx.write(Unpooled.copiedBuffer(data));

            offset += CHUNK_SIZE_IN_BYTES;
        }
        buf.release();
    }
}

