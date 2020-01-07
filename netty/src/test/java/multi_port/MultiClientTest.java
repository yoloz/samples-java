package multi_port;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MultiClientTest {

    MultiClient multiClient;

    @Before
    public void setUp() {
        multiClient = new MultiClient(8006, 8007);
    }

    @After
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