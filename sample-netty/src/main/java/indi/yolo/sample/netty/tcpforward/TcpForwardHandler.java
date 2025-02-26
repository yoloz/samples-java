package indi.yolo.sample.netty.tcpforward;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author yolo
 */
public class TcpForwardHandler extends ChannelInboundHandlerAdapter {


    private final String remoteHost;
    private final int remotePort;
    private Channel remoteChannel;

    public TcpForwardHandler(String remoteHost, int remotePort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        connectToRemoteServer(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (remoteChannel != null && remoteChannel.isActive()) {
            if (msg instanceof ByteBuf) {
                // 输出数据
                ByteBuf buf = (ByteBuf) msg;
                StringBuilder asciiData = new StringBuilder();
                for (int i = 0; i < buf.readableBytes(); i++) {
                    byte b = buf.readByte();
                    asciiData.append((char) (b >= 32 && b < 127 ? b : '.'));
                }
                OutputBytes.output(((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress(),
                        ((InetSocketAddress) ctx.channel().remoteAddress()).getPort(),
                        ((InetSocketAddress) ctx.channel().localAddress()).getAddress().getHostAddress(),
                        ((InetSocketAddress) ctx.channel().localAddress()).getPort(),
                        asciiData
                );
                buf.resetReaderIndex();
                //转发数据
                remoteChannel.writeAndFlush(msg);
                // 释放
//                ReferenceCountUtil.safeRelease(msg);
            } else {
                // 处理其他类型的消息
                System.out.println("接受到非ByteBuf数据，直接转发......");
                remoteChannel.writeAndFlush(msg);
            }
        } else {
            // 如果远程连接未建立或已关闭，则关闭本地连接
            System.out.println("远程连接未建立或已关闭，关闭本地连接......");
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("本地连接关闭，关闭远程连接......");
        if (remoteChannel != null) {
            remoteChannel.close();
        }
    }

    private void connectToRemoteServer(ChannelHandlerContext ctx) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
//                            p.addLast(new ByteArrayDecoder());
//                            p.addLast(new ByteArrayEncoder());
                            p.addLast(new TargetRespHandler(ctx));
                        }
                    });

            ChannelFuture f = b.connect(remoteHost, remotePort).sync();
            remoteChannel = f.channel();

            // 当远程连接关闭时，也关闭本地连接
            remoteChannel.closeFuture().addListener(future -> {
                ctx.channel().close();
                group.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
            ctx.close();
        }
    }
}

