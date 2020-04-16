package ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;


public class ClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * Creates a client-side handler.
     */

    public ClientHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().remoteAddress().toString() + " active");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf src = (ByteBuf) msg;
        byte[] bytes = ByteBufUtil.getBytes(src, src.readerIndex(), src.writerIndex());
        src.readerIndex(src.readerIndex() + src.writerIndex());
        System.out.println("client receive: " + new String(bytes, StandardCharsets.UTF_8));
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