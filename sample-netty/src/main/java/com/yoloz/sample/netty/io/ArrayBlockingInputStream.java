package com.yoloz.sample.netty.io;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ArrayBlockingInputStream extends InputStream {
    
    private final ArrayBlockingQueue<Byte> buf;
    private final AtomicBoolean closed;

    public ArrayBlockingInputStream() {
        this.buf = new ArrayBlockingQueue<>(8192);
        this.closed = new AtomicBoolean(false);
    }

    public void appendBuf(ByteBuf byteBuf) throws IOException {
        ensureOpen();
        try {
            byte b = byteBuf.readByte();
            String fail = null;
            if (b == 0) while (byteBuf.isReadable()) buf.put(byteBuf.readByte());
            else if (b == -1) close();
            else fail = IOUtils.readString(byteBuf, byteBuf.readShort()); //b==1
            byteBuf.release();
            if (fail != null) throw new IOException(fail);
        } catch (InterruptedException | IOException e) {
            close();
            throw new IOException(e);
        }
    }

    private void ensureOpen() throws IOException {
        if (closed.get()) throw new IOException("Stream closed");
    }

    @Override
    public int read() {
        while (!closed.get() || buf.size() > 0) {
            Byte byt = buf.poll();
            if (byt != null) return (byt & 0xff);
        }
        return -1;
    }

    public boolean isClosed() {
        return closed.get();
    }

    @Override
    public void close() {
        closed.set(true);
    }

}
