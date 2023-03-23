package indi.yolo.sample.netty.io.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

public class SslClient implements AutoCloseable {

    private final EventLoopGroup group;
    private final SslContext sslCtx;

    private final Object lock = new Object();

    private final String ip;
    private final int port;

    private final ArrayBlockingQueue<Object> pull = new ArrayBlockingQueue<>(1000);

    private Channel channel;
    private Throwable failure = null;

    public SslClient(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        this.group = new NioEventLoopGroup();
        try {
            this.sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } catch (SSLException e) {
            throw new IllegalArgumentException("failed to initialize the SSL context", e);
        }
        this.initConnect();
    }

    public void sendRequest(ByteBuf buf) {
//        System.out.println("sendBuffer start:" + System.currentTimeMillis());
        buf.setInt(0, buf.writerIndex() - 4);
        channel.writeAndFlush(buf);
        while (true) if (buf.refCnt() == 0) break;
//        System.out.println("sendBuffer end:" + System.currentTimeMillis());
    }

    public Channel getChannel() {
        return channel;
    }

    //who uses who releases
    public ByteBuf getResponse() throws IOException {
        try {
            Object obj = pull.take();
            if (obj instanceof Throwable) throw (Throwable) obj;
            else return (ByteBuf) obj;
        } catch (Throwable e) {
            throw new IOException(e);
        }
    }

    public boolean isActive() {
        return channel.isActive();
    }

    private void initConnect() throws IOException {
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(sslCtx.newHandler(ch.alloc(), ip, port));
                            //p.addLast(new LoggingHandler(LogLevel.INFO));
                            p.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,
                                    4, 0, 4));
                            p.addLast(new Handler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
            channel = channelFuture.channel();
            channelFuture.addListener((ChannelFutureListener) arg0 -> {
                if (!channelFuture.isSuccess()) {
                    failure = channelFuture.cause();
                    close();
                }
                synchronized (lock) {
                    lock.notify();
                }
            });
            synchronized (lock) {
                lock.wait();
            }
            if (failure != null) throw failure;
        } catch (Throwable e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() {
        if (channel != null) channel.close();
        group.shutdownGracefully();
    }


    private class Handler extends ChannelInboundHandlerAdapter {

        private Handler() {
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object obj) throws InterruptedException {
            pull.put(obj);  //who uses who releases
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws InterruptedException {
            pull.put(cause);
            ctx.close();
        }
    }


}
