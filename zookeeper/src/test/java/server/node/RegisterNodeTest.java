package server.node;

import Context;
import zk.ZkClient;
import org.junit.Test;


/**
 *
 */
public class RegisterNodeTest {

//     服务器端可以用此关闭端口测试连接断开
//    iptables -A OUTPUT -d 10.68.11.114  -p tcp --sport 2181 -j DROP
//    iptables -F

    @Test
    public void run() throws Exception {

    }

    public static void main(String[] args) {
        ZkClient zkUtils = new ZkClient("UTL-164:2181");
        Context.getInstance().init("", "10", "","");
        RegisterNode registerNode = new RegisterNode(zkUtils);
//            new Thread(registerNode).start();
        try {
            registerNode.register();
            registerNode.addLostListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}