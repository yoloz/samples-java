package com.yoloz.sample.netty.multi_port;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) {
        String address = ctx.channel().remoteAddress().toString();
        ByteBuf src = (ByteBuf) obj;
        byte[] value = (address + " 返回").getBytes(StandardCharsets.UTF_8);
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
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().remoteAddress().toString() + " connect");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().remoteAddress().toString() + " disconnect");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//        String address = ctx.channel().remoteAddress().toString();
        cause.printStackTrace();
        ctx.close();
    }
}
