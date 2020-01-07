package ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
        boolean isConnect = SSLClient.isConnect();
        while (!isConnect) {
            Thread.sleep(500);
            isConnect = SSLClient.isConnect();
        }
        Channel channel = SSLClient.getChannel();
        System.out.println("发送test1");
        byte[] bytes = "test1".getBytes(StandardCharsets.UTF_8);
        ByteBuf buf = Unpooled.buffer(512);
        buf.writeInt(bytes.length + 5);
        buf.writeByte(1);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        channel.writeAndFlush(buf);

        System.out.println("发送test0");
        bytes = "test0".getBytes(StandardCharsets.UTF_8);
        buf = Unpooled.buffer(512);
        buf.writeInt(bytes.length + 5);
        buf.writeByte(0);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        channel.writeAndFlush(buf);

        System.out.println("发送finish");
        Thread.sleep(30 * 1000);
    }
}