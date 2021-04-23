package com.yoloz.sample.netty.io;

import java.io.*;
import java.util.concurrent.Callable;

public class In2OutChars implements Callable<Boolean> {

    private final Reader reader;
    private final Writer writer;

    public In2OutChars(Reader reader, Writer writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public Boolean call() throws IOException {
        char[] chars = new char[2048];
        int len;
        while ((len = reader.read(chars)) != -1) {
            writer.write(chars, 0, len);
        }
        writer.flush();
        return true;
    }
}
