package indi.yolo.sample.netty.io;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ArrayBlockingReader extends Reader {

    private final ArrayBlockingQueue<Character> buf;
    private final AtomicBoolean closed;

    public ArrayBlockingReader() {
        this.buf = new ArrayBlockingQueue<>(4096);
        this.closed = new AtomicBoolean(false);
    }

    public void appendBuf(ByteBuf byteBuf) throws IOException {
        ensureOpen();
        try {
            byte b = byteBuf.readByte();
            String fail = null;
            if (b == 0) while (byteBuf.isReadable()) buf.put(byteBuf.readChar());
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
    public int read(char[] b, int off, int len) {
        if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) return 0;
        int counter = 0;
        while (!closed.get() || buf.size() > 0) {
            if (counter == len) return counter;
            Character c = buf.poll();
            if (c != null) {
                b[off + counter] = c;
                counter++;
            }
        }
        if (counter > 0) return counter;
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
