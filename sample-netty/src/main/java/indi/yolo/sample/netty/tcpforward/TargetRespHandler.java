package indi.yolo.sample.netty.tcpforward;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;


/**
 * @author yolo
 */
public class TargetRespHandler extends ChannelInboundHandlerAdapter {

    private final ChannelHandlerContext clientCtx;

    public TargetRespHandler(ChannelHandlerContext clientCtx) {
        this.clientCtx = clientCtx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
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
            //返回响应数据
            clientCtx.writeAndFlush(msg);
            // 释放
//            ReferenceCountUtil.safeRelease(msg);
        } else {
            // 处理其他类型的消息
            System.out.println("接受到非ByteBuf数据，直接返回......");
            clientCtx.writeAndFlush(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
        clientCtx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        clientCtx.close();
    }
}
