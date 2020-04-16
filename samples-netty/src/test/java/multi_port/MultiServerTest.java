package multi_port;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MultiServerTest {

    MultiServer multiServer;

    @Before
    public void setUp() throws Exception {
        multiServer = new MultiServer(8006, 8007);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void start() throws InterruptedException {
        multiServer.start();
    }

    @Test
    public void stopServerChannel() throws InterruptedException {
        new Thread(() -> {
            try {
                multiServer.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(1000 * 10);
        new Thread(() -> {
            multiServer.stopServerChannel(8006);
        }).start();
        System.out.println("查看8006端口是否存在");
        Thread.sleep(1000 * 20);
        multiServer.stop();
    }
}