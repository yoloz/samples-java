package indi.yoloz.sample.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.RetryNTimes;

/**
 * 17-5-17
 */
public class CuratorWatcherTest {

    /**
     * Zookeeper info
     */
    private static final String ZK_ADDRESS = "UTL-163:2181,UTL-164:2181,UTL-165:2181";

    public static void main(String[] args) throws Exception {
        // 1.Connect to zk
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                ZK_ADDRESS,
                new RetryNTimes(10, 5000)
        );
        client.start();
        System.out.println("zk client start successfully!");

        // 2.Register watcher
        PathChildrenCache watcher = new PathChildrenCache(
                client,
                "/zktest",
                true    // if cache data
        );
        watcher.getListenable().addListener((client1, event) -> {
            ChildData data = event.getData();
            if (data == null) {
                System.out.println("No data in event[" + event + "]");
            } else {
                System.out.println("Receive event: "
                                + "type=[" + event.getType() + "]"
                                + ", path=[" + data.getPath() + "]"
                                + ", data=[" + new String(data.getData()) + "]"
//                        + ", stat=[" + data.getStat() + "]"
                );
            }
        });
        watcher.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        System.out.println("Register zk watcher successfully!");

        PathChildrenCache watcher1 = new PathChildrenCache(
                client,
                "/zktest1",
                true    // if cache data
        );
        watcher1.getListenable().addListener((client1, event) -> {
            ChildData data = event.getData();
            if (data == null) {
                System.out.println("No data in event[" + event + "]");
            } else {
                System.out.println("Receive event: "
                                + "type=[" + event.getType() + "]"
                                + ", path=[" + data.getPath() + "]"
                                + ", data=[" + new String(data.getData()) + "]"
//                        + ", stat=[" + data.getStat() + "]"
                );
            }
        });
        watcher1.start(PathChildrenCache.StartMode.NORMAL);
        System.out.println("Register zk watcher1 successfully!");

//        Thread.sleep(Integer.MAX_VALUE);
    }

}

