package com.yoloz.sample.netty.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class SSLClientTest {
    SSLClient SSLClient;

    @Before
    public void setUp() throws Exception {
        System.setProperty("ssl", "not empty");
        SSLClient = new SSLClient();
        SSLClient.start();
    }

    @After
    public void tearDown() throws Exception {
        SSLClient.close();
    }

    @Test
    public void getChannel() throws InterruptedException {
        Channel channel = SSLClient.getChannel();
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
        System.out.println("发送test1");
        buf.writerIndex(4);
        byte[] bytes = "test1".getBytes(StandardCharsets.UTF_8);
        buf.writeByte(1);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        buf.setInt(0,bytes.length + 5);
        channel.writeAndFlush(buf);

        System.out.println("发送test0");
        buf = PooledByteBufAllocator.DEFAULT.buffer();
        buf.writerIndex(4);
        bytes = "test0".getBytes(StandardCharsets.UTF_8);
        buf.writeByte(0);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        buf.setInt(0,bytes.length + 5);
        channel.writeAndFlush(buf);

        System.out.println("发送finish");
        Thread.sleep(30 * 1000);
    }
}