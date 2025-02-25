package indi.yolo.sample.netty.tcpforward;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author yolo
 */
class TcpForwardServerTest {

    @Test
    void start() throws InterruptedException {

        int localPort = 10000; // 本地服务器监听的端口
        String remoteHost = "192.168.124.232"; // 远程服务器的IP地址
        int remotePort = 1433; // 远程服务器的端口

        new TcpForwardServer(localPort, remoteHost, remotePort).start();
    }
}