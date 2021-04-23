package com.yoloz.sample.netty.ssl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SSLServerTest {

    SSLServer SSLServer;

    @Before
    public void setUp() throws Exception {
        System.setProperty("ssl","not empty");
        SSLServer = new SSLServer();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void start() throws Exception {
        SSLServer.start();
    }
}