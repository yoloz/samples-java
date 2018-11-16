package zk;

import org.apache.zookeeper.CreateMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

/**
 *
 */
public class ZkUtilsTest {

    private ZkClient zkClient = null;

    @Before
    public void setUp() throws Exception {
        zkClient = new ZkClient("UTL-163:2181");
    }

    @After
    public void tearDown() throws Exception {
        zkClient.close();
    }

    @Test
    public void initZkRoot() throws Exception {
        ZkUtils.initZkRoot(zkClient);
    }

    @Test
    public void writeJSON() throws Exception {
    }

    @Test
    public void write() throws Exception {
    }

    @Test
    public void readJSON() throws Exception {
    }

    @Test
    public void read() throws Exception {
        System.out.println("=================================");
        System.out.println(ZkUtils.read(zkClient, ZkConfig.zk_serviceId));
        System.out.println("=================================");
    }

    public static void main(String[] args) throws Exception {
        ZkClient zkClient = new ZkClient("UTL-164:2181");
//        zkClient.getCurator().getChildren().forPath("/test").forEach(s -> System.out.println(s));
//        System.out.println(ZkUtils.read(zkClient,"/test/f1"));
        ZkUtils.createOrUpdate(zkClient,"/test/1","123", CreateMode.PERSISTENT);
        Thread.sleep(60000);
        ZkUtils.createOrUpdate(zkClient,"/test/1","456", CreateMode.PERSISTENT);
    }

}