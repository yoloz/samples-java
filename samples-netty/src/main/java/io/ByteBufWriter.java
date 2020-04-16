package io;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.io.IOException;
import java.io.Writer;

public class ByteBufWriter extends Writer {

    private final byte[] header;
    private final Channel channel;

    public ByteBufWriter(byte[] header, Channel channel) {
        this.header = header;
        this.channel = channel;
    }

    public ByteBufWriter(Channel channel) {
        this.header = null;
        this.channel = channel;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        ensureOpen();
        if ((off < 0) || (off > cbuf.length) || (len < 0) ||
                ((off + len) > cbuf.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) return;

        ByteBuf buf = IOUtils.newBuf((len - off) * 2 + 256);
        if (header != null) {
            buf.writeShort(header.length);
            buf.writeBytes(header);
        }
        buf.writeByte(0);
        for (int i = off; i < off + len; i++) buf.writeChar(cbuf[i]);
        buf.setInt(0, buf.writerIndex() - 4);
        channel.writeAndFlush(buf);
        while (true) if (buf.refCnt() == 0) break;
    }

    private void ensureOpen() throws IOException {
        if (!channel.isActive()) throw new IOException("Stream closed");
    }


    @Override
    public void flush() {

    }

    @Override
    public void close() {
        ByteBuf buf = IOUtils.newBuf();
        if (header != null) {
            buf.writeShort(header.length);
            buf.writeBytes(header);
        }
        buf.writeByte(-1);
        buf.setInt(0, buf.writerIndex() - 4);
        channel.writeAndFlush(buf);
        while (true) if (buf.refCnt() == 0) break;
    }


}
