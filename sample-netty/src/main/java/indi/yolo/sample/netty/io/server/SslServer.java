package indi.yolo.sample.netty.io.server;

import indi.yolo.sample.netty.io.ArrayBlockingInputStream;
import indi.yolo.sample.netty.io.ArrayBlockingReader;
import indi.yolo.sample.netty.io.ByteBufOutputStream;
import indi.yolo.sample.netty.io.ByteBufWriter;
import indi.yolo.sample.netty.io.IOUtils;
import indi.yolo.sample.netty.io.In2OutBytes;
import indi.yolo.sample.netty.io.In2OutChars;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.*;


public final class SslServer {

    public static void main(String[] args) throws Exception {
        SslServer sslServer = new SslServer();
        sslServer.start();
    }

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ArrayBlockingInputStream stream;
    private Future<Boolean> in2OutBytesFuture;
    private ArrayBlockingReader reader;
    private Future<Boolean> in2OutCharsFuture;

    public void start() throws Exception {
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(sslCtx.newHandler(ch.alloc()));
                            //p.addLast(new LoggingHandler(LogLevel.INFO));
                            p.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,
                                    4, 0, 4));
                            p.addLast(new Handler());
                        }
                    });

            // Start the server.
            ChannelFuture f = b.bind(8007).sync();
            if (f.isSuccess()) {
                System.out.println("服务端启动成功");
            } else {
                System.out.println("服务端启动失败");
                f.cause().printStackTrace();
                //关闭线程组
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @ChannelHandler.Sharable
    private class Handler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf src = (ByteBuf) msg;
            String action = IOUtils.readString(src, src.readShort());
            if ("clientWriteBytes".equals(action)) {
                if (stream == null) {
                    stream = new ArrayBlockingInputStream();
                    in2OutBytesFuture = executorService.submit(new In2OutBytes(stream,
                            Files.newOutputStream(Paths.get(System.getProperty("user.dir"), action))));
                }
                int symbol = src.getByte(src.readerIndex());
                stream.appendBuf(src);
                if (symbol == -1) {
                    in2OutBytesFuture.get();
                    stream = null;
                    complete(ctx);
                }
            } else if ("clientWriteChars".equals(action)) {
                if (reader == null) {
                    reader = new ArrayBlockingReader();
                    in2OutCharsFuture = executorService.submit(new In2OutChars(reader,
                            Files.newBufferedWriter(Paths.get(System.getProperty("user.dir"), action))));
                }
                int symbol = src.getByte(src.readerIndex());
                reader.appendBuf(src);
                if (symbol == -1) {
                    in2OutCharsFuture.get();
                    stream = null;
                    complete(ctx);
                }
            } else if ("clientReadBytes".equals(action)) {
                try (InputStream inputStream = Files.newInputStream(IOUtils.path);
                     ByteBufOutputStream outputStream = new ByteBufOutputStream(ctx.channel())) {
                    byte[] bytes = new byte[1024];
                    int len;
                    while ((len = inputStream.read(bytes)) > 0) outputStream.write(bytes, 0, len);
                }
                complete(ctx);
            } else if ("clientReadChars".equals(action)) {
                try (BufferedReader reader = Files.newBufferedReader(IOUtils.path);
                     ByteBufWriter writer = new ByteBufWriter(ctx.channel())) {
                    char[] chars = new char[2048];
                    int len;
                    while ((len = reader.read(chars)) > 0) writer.write(chars, 0, len);
                }
                complete(ctx);
            } else System.out.println(action + " undefined");
            if (src.refCnt() > 0) src.release();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            System.out.println(ctx.channel().remoteAddress().toString() + " connect");
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            System.out.println(ctx.channel().remoteAddress().toString() + " disconnect");
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            // Close the connection when an exception is raised.
            cause.printStackTrace();
            ctx.close();
        }
    }

    private void complete(ChannelHandlerContext ctx) {
        ByteBuf buf = ctx.alloc().buffer(5);
        buf.writeInt(1);
        buf.writeByte(0);
        ctx.writeAndFlush(buf);
    }
}