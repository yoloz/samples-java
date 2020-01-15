package io;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.io.IOException;
import java.io.OutputStream;

public class ByteBufOutputStream extends OutputStream {

    private final int DEFAULT_SIZE = 8192;
    private final byte[] header;
    private final Channel channel;

    private byte[] buf;
    private int count;

    public ByteBufOutputStream(byte[] header, Channel channel) {
        this.header = header;
        this.channel = channel;
        this.buf = new byte[DEFAULT_SIZE];
    }

    public ByteBufOutputStream(Channel channel) {
        this.header = null;
        this.channel = channel;
        this.buf = new byte[DEFAULT_SIZE];
    }

    @Override
    public void flush() {
        if (count > 0) {
            ByteBuf byteBuf = IOUtils.newBuf(count + 256);
            if (header != null) {
                byteBuf.writeShort(header.length);
                byteBuf.writeBytes(header);
            }
            byteBuf.writeByte(0);
            byteBuf.writeBytes(buf, 0, count);
            byteBuf.setInt(0, byteBuf.writerIndex() - 4);
            channel.writeAndFlush(byteBuf);
            while (true) if (byteBuf.refCnt() == 0) break;
            count = 0;
        }
    }

    @Override
    public void write(int b) throws IOException {
        ensureOpen();
        if (count == buf.length) flush();
        buf[count] = (byte) b;
        count += 1;
    }

    private void ensureOpen() throws IOException {
        if (!channel.isActive()) throw new IOException("Stream closed");
    }

    @Override
    public void close() {
        flush();
        ByteBuf byteBuf = IOUtils.newBuf();
        if (header != null) {
            byteBuf.writeShort(header.length);
            byteBuf.writeBytes(header);
        }
        byteBuf.writeByte(-1);
        byteBuf.setInt(0, byteBuf.writerIndex() - 4);
        channel.writeAndFlush(byteBuf);
        while (true) if (byteBuf.refCnt() == 0) break;
        count = 0;
    }
}
