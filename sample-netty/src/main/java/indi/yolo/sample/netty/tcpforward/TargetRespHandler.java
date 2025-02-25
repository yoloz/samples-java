package indi.yolo.sample.netty.tcpforward;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

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
//        if (msg instanceof ByteBuf) {
//            ByteBuf in = (ByteBuf) msg;
//            // 读取数据
//            byte[] bytes = new byte[in.readableBytes()];
//            in.readBytes(bytes);
        if (msg instanceof byte[]) {
            byte[] bytes = (byte[]) msg;
//                byte[] bytes = new byte[in.length];
//                System.arraycopy(in,0,bytes,0,in.length);
            StringBuilder asciiData = new StringBuilder();
            for (byte b : bytes) {
                asciiData.append((char) (b >= 32 && b < 127 ? b : '.'));
            }
            OutputBytes.output(((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress(),
                    ((InetSocketAddress) ctx.channel().remoteAddress()).getPort(),
                    ((InetSocketAddress) ctx.channel().localAddress()).getAddress().getHostAddress(),
                    ((InetSocketAddress) ctx.channel().localAddress()).getPort(),
                    asciiData
            );
            //返回响应数据
            clientCtx.writeAndFlush(msg);
            // 释放
            ReferenceCountUtil.safeRelease(msg);
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
