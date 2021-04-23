package com.yoloz.sample.netty.multi_port;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.CountDownLatch;


public class MultiServer {


    private ChannelFuture[] channelFutures = null;
    private int beginPort, endPort;
    private final CountDownLatch block = new CountDownLatch(1);

    public MultiServer(int beginPort, int endPort) {
        this.beginPort = beginPort;
        this.endPort = endPort;
    }

    public void start() throws InterruptedException {

        EventLoopGroup bg = new NioEventLoopGroup();
        EventLoopGroup wg = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();

        try {
            bootstrap.group(bg, wg)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,
                                    4, 0, 4), new ServerHandler());
                        }
                    });

            //同时启动多个端口
            if (channelFutures == null) channelFutures = new ChannelFuture[endPort - beginPort + 1];
            for (int i = beginPort; i <= endPort; i++) {
                final int port = i;
                ChannelFuture channelFuture = bootstrap.bind(port);
                Channel channel = channelFuture.channel();
                channelFutures[i - beginPort] = channelFuture;
                channelFuture.addListener(future -> {
                    if (future.isSuccess()) {
                        System.out.println("服务端启动成功: " + port);
                    } else {
                        System.out.println("服务端启动失败: " + port);
                        future.cause().printStackTrace();
                        channelFuture.channel().close();
                    }
                });
                channel.closeFuture().addListener(future -> {
                    channel.close();
                });
            }

            block.await();
        } finally {
            bg.shutdownGracefully();
            wg.shutdownGracefully();
        }

    }

    public void stop() {
        block.countDown();
        System.out.println("stop");
    }

    //关闭单个端口的NioServerSocketChannel
    public void stopServerChannel(int port) {
        int i = port - beginPort;
        if (0 <= i && i <= channelFutures.length) {
            channelFutures[i].channel().close();
        }
    }
}
