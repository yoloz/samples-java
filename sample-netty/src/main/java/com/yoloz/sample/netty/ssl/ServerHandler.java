package com.yoloz.sample.netty.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;


@Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {


    /**
     * 一个连接即一个channel,上一次请求未处理结束则下一个请求阻塞直到上一次处理完成后才处理
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf src = (ByteBuf) msg;
        byte[] value = "返回信息".getBytes(StandardCharsets.UTF_8);
        if (src.readByte() == 1) {
            try {
                Thread.sleep(10 * 1000);
                value = "返回阻塞信息".getBytes(StandardCharsets.UTF_8);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int len = src.readInt();
        byte[] bytes = ByteBufUtil.getBytes(src, src.readerIndex(), len);
        System.out.println("server receive: " + new String(bytes, StandardCharsets.UTF_8));
        src.readerIndex(src.readerIndex() + len);
        ByteBuf buf = ctx.alloc().buffer(value.length);
        buf.writeBytes(value);
        ctx.writeAndFlush(buf);
        src.release();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress().toString() + " connect");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
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