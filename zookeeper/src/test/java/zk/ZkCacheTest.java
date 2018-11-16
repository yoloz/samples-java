package zk;


import Context;
import beans.ServiceInfo;
import common.JsonUtils;
import server.node.RegisterNode;
import org.apache.zookeeper.CreateMode;

/**
 */
public class ZkCacheTest {

//    private ZkClient zkClient = null;
//
//    @Before
//    public void setUp() throws Exception {
//        zkClient = new ZkClient("UTL-163:2181");
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        zkClient.close();
//    }

//    @Test
//    public void initService() throws Exception {
//        ZkClient zkClient = new ZkClient("UTL-164:2181");
//        ZkUtils.delete(zkClient,ZkConfig.zk_services);
//        ZkUtils.create(zkClient,ZkConfig.zk_services,"",CreateMode.PERSISTENT);
//    }

    public void pathCache(ZkClient zkClient) {
        try {
            new Thread(() -> {
                try {
                    ZkCache zkCache = ZkCache.getInstance();
                    zkCache.start(zkClient);
                    while (true) {
                        Thread.sleep(2000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerNode() {
        new Thread(() -> {
            ZkClient zkClient = new ZkClient("UTL-164:2181");
            Context context = Context.getInstance();
            context.init("", "0", "10.68.23.14:123","");
            RegisterNode registerNode = new RegisterNode(zkClient);
            try {
                registerNode.register();
                Thread.sleep(6000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            zkClient.close();
        }).start();
        new Thread(() -> {
            ZkClient zkClient = new ZkClient("UTL-165:2181");
            Context context = Context.getInstance();
            context.init("", "1", "10.68.23.15:123","");
            RegisterNode registerNode = new RegisterNode(zkClient);
            try {
                registerNode.register();
                Thread.sleep(6000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            zkClient.close();
        }).start();
        new Thread(() -> {
            ZkClient zkClient = new ZkClient("UTL-163:2181");
            Context context = Context.getInstance();
            context.init("", "2", "10.68.23.16:123","");
            RegisterNode registerNode = new RegisterNode(zkClient);
            try {
                registerNode.register();
                Thread.sleep(6000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            zkClient.close();
        }).start();
    }

    public void registerService(ZkClient zkClient) {
//        ZkClient zkClient = new ZkClient("UTL-165:2181");
        for (int i = 0; i < 5; i++) {
            String id = "123" + i;
            ServiceInfo seviceInfo = new ServiceInfo(id);
            try {
                ZkUtils.create(zkClient, ZkConfig.zk_services + "/" + id, JsonUtils.toJson(seviceInfo), CreateMode.PERSISTENT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("UTL-163:2181");
        ZkCacheTest zkCacheTest = new ZkCacheTest();
        System.out.println("============path cache==============");
        zkCacheTest.pathCache(zkClient);
//        System.out.println("============path cache==============");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("============register node==============");
        zkCacheTest.registerNode();
//        System.out.println("============register node==============");
        System.out.println("============register service==============");
        zkCacheTest.registerService(zkClient);
//        System.out.println("============register service==============");
        try {
            Thread.sleep(6000);
            ZkUtils.delete(zkClient,ZkConfig.zk_services);
            ZkUtils.create(zkClient,ZkConfig.zk_services,"",CreateMode.PERSISTENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}