import com.arrwhi.pbox.netty.LengthAndChunkDecoder;
import com.arrwhi.pbox.netty.LengthAndChunkEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


/**
 * Created by arran on 29/07/17.
 */
public class Sandbox {
    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            try {
                new Server();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Client();
    }
}

class Server {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public Server() throws Exception {
        final int port = 8888;

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LengthAndChunkEncoder());
                        ch.pipeline().addLast(new LengthAndChunkDecoder());
                        ch.pipeline().addLast(new ServerHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture f = b.bind(port).sync();
        System.out.println("Server started");
        f.channel().closeFuture().sync();
    }
}

class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Server got data.");
        ByteBuf buf = Unpooled.directBuffer();
        buf.writeByte(111);
        ctx.writeAndFlush(buf);
    }
}

class Client {
    private EventLoopGroup workerGroup;

    public Client() {
        try {
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
                    ch.pipeline().addLast(new ClientHandler());
                }
            });

            // Connect to the server
            System.out.println("Connecting to server...");
            ChannelFuture f = b.connect("localhost", 8888).sync();
            f.addListener(new ServerChannelFutureListener());
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends ChannelInboundHandlerAdapter {
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        System.out.println("Client got data.");
    }
}

class ServerChannelFutureListener implements ChannelFutureListener {
    @Override
    public void operationComplete(ChannelFuture future) {
        System.out.println("Connected to server!");
        ByteBuf buf = Unpooled.directBuffer();
        buf.writeByte(888);
        future.channel().writeAndFlush(buf);
    }
}