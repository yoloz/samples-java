package indi.yolo.sample.netty.multi_port;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MultiClientTest {

    MultiClient multiClient;

    @BeforeEach
    public void setUp() {
        multiClient = new MultiClient(8006, 8007);
    }

    @AfterEach
    public void tearDown() throws Exception {
        multiClient.stop();
    }

    @Test
    public void start() throws Exception {
        multiClient.start();
        Thread.sleep(30 * 1000);
    }

    @Test
    public void closeChannel() {
    }
}