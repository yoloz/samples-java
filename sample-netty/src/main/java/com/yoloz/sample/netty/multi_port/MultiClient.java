package com.yoloz.sample.netty.multi_port;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class MultiClient {

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();
    private int startPort;
    private int endPort;
    private String serverHost = System.getProperty("host", "127.0.0.1");
    private ChannelFuture[] channelFutures = null;

    public MultiClient(int startPort, int endPort) {
        this.startPort = startPort;
        this.endPort = endPort;
    }

    public void start() throws Exception {
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        System.out.println("正在连接中...");
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });

        if (channelFutures == null) channelFutures = new ChannelFuture[endPort - startPort + 1];

        //同时连接多个端口
        for (int i = startPort; i <= endPort; i++) {
            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(serverHost, i));
            int port = i;
            channelFutures[i - startPort] = channelFuture;
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    System.out.println("连接服务器成功: " + port);
                    ByteBuf buf = Unpooled.buffer(520);
                    byte[] bytes = ("端口" + port + "连接").getBytes(StandardCharsets.UTF_8);
                    buf.writeInt(bytes.length + 4);
                    buf.writeInt(bytes.length);
                    buf.writeBytes(bytes);
                    channelFuture.channel().writeAndFlush(buf);
                } else {
                    System.out.println("连接服务器失败: " + port);
                    future.cause().printStackTrace();
                    channelFuture.channel().close();
                }
            });
        }
    }

    public void stop() {
        eventLoopGroup.shutdownGracefully();
        System.out.println("stop");
    }


    public void closeChannel(int port) {
        int i = port - startPort;
        if (0 <= i && i <= channelFutures.length) {
            channelFutures[i].channel().close();
            System.out.println("close port " + port);
        }
    }
}
