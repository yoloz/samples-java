package io;

import io.netty.buffer.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IOUtils {

    public static final Path path = Paths.get("");

    public static ByteBuf newBuf(int size) {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(size);
        buf.writerIndex(4);
        return buf;
    }

    public static ByteBuf newBuf() {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
        buf.writerIndex(4);
        return buf;
    }

    public static byte[] readBytes(ByteBuf buf, int length) {
        if (length == ~0) return null;
        byte[] bytes = ByteBufUtil.getBytes(buf, buf.readerIndex(), length);
        buf.readerIndex(buf.readerIndex() + length);
        return bytes;
    }

    public static String readString(ByteBuf buf, int length) {
        byte[] bytes = readBytes(buf, length);
        return bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
    }

}
