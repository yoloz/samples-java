package io.client;

import io.*;
import io.netty.buffer.ByteBuf;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClientMain {

    public static void main(String[] args) throws IOException {
        try (SslClient sslClient = new SslClient("127.0.0.1", 8007)) {
            clientWriteBytes(sslClient);
            clientReadBytes(sslClient);
            clientWriteChars(sslClient);
            clientReadChars(sslClient);
        }
    }

    public static void clientWriteBytes(SslClient sslClient) throws IOException {
        byte[] header = "clientWriteBytes".getBytes();
        try (InputStream inputStream = Files.newInputStream(IOUtils.path);
             ByteBufOutputStream outputStream = new ByteBufOutputStream(header, sslClient.getChannel())) {
            byte[] bytes = new byte[1024];
            int len;
            while ((len = inputStream.read(bytes)) > 0) outputStream.write(bytes, 0, len);
        }
        ByteBuf result = sslClient.getResponse();
        if (result.readByte() == 0) System.out.println("clientWriteBytes success");
    }

    public static void clientReadBytes(SslClient sslClient) throws IOException {
        try (ArrayBlockingInputStream inputStream = new ArrayBlockingInputStream()) {
            new ReadBinary(inputStream, sslClient).start();
            ByteBuf buffer = IOUtils.newBuf();
            byte[] header = "clientReadBytes".getBytes();
            buffer.writeShort(header.length);
            buffer.writeBytes(header);
            sslClient.sendRequest(buffer);
            try (OutputStream outputStream = Files.newOutputStream(Paths.get(System.getProperty("user.dir"),
                    "clientReadBytes"))) {
                byte[] bytes = new byte[4096];
                int len;
                while ((len = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, len);
                }
            }
        }
        ByteBuf result = sslClient.getResponse();
        if (result.readByte() == 0) System.out.println("clientReadBytes success");
    }

    public static void clientWriteChars(SslClient sslClient) throws IOException {
        byte[] header = "clientWriteChars".getBytes();
        try (BufferedReader reader = Files.newBufferedReader(IOUtils.path);
             ByteBufWriter writer = new ByteBufWriter(header, sslClient.getChannel())) {
            char[] chars = new char[4096];
            int len;
            while ((len = reader.read(chars)) > 0) writer.write(chars, 0, len);
        }
        ByteBuf result = sslClient.getResponse();
        if (result.readByte() == 0) System.out.println("clientWriteChars success");
    }

    public static void clientReadChars(SslClient sslClient) throws IOException {
        try (ArrayBlockingReader reader = new ArrayBlockingReader()) {
            new ReadChars(reader, sslClient).start();
            ByteBuf buffer = IOUtils.newBuf();
            byte[] header = "clientReadChars".getBytes();
            buffer.writeShort(header.length);
            buffer.writeBytes(header);
            sslClient.sendRequest(buffer);
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(System.getProperty("user.dir"),
                    "clientReadChars"), StandardCharsets.UTF_8)) {
                char[] chars = new char[4096];
                int len;
                while ((len = reader.read(chars)) != -1) {
                    writer.write(chars, 0, len);
                }
            }
        }
        ByteBuf result = sslClient.getResponse();
        if (result.readByte() == 0) System.out.println("clientReadChars success");
    }


    private static class ReadBinary extends Thread {

        private ArrayBlockingInputStream inputStream;
        private SslClient sslClient;

        ReadBinary(ArrayBlockingInputStream inputStream, SslClient sslClient) {
            this.inputStream = inputStream;
            this.sslClient = sslClient;
        }

        @Override
        public void run() {
            try {
                while (!inputStream.isClosed()) inputStream.appendBuf(sslClient.getResponse());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ReadChars extends Thread {

        private ArrayBlockingReader reader;
        private SslClient sslClient;

        ReadChars(ArrayBlockingReader reader, SslClient sslClient) {
            this.reader = reader;
            this.sslClient = sslClient;
        }

        @Override
        public void run() {
            try {
                while (!reader.isClosed()) reader.appendBuf(sslClient.getResponse());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
