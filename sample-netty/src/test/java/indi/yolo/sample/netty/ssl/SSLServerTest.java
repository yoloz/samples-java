package indi.yolo.sample.netty.ssl;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SSLServerTest {

    SSLServer SSLServer;

    @BeforeEach
    public void setUp() throws Exception {
        System.setProperty("ssl","not empty");
        SSLServer = new SSLServer();
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void start() throws Exception {
        SSLServer.start();
    }
}