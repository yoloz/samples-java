package server;

import Context;
import server.fs.LocalFSInit;
import server.fs.vistor.DatFileV;
import server.fs.vistor.JobFileV;
import zk.ZkClient;
import zk.ZkConfig;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.utils.CloseableUtils;

import java.nio.charset.Charset;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ServiceSyncTest {

    public static void main(String[] args) throws Exception {
        Context context = Context.getInstance();
        context.init("/home/ethan/testData1", "114", "","");
        context.setZkClient(new ZkClient("UTL-163:2181"));
        Map<String, FileVisitor<Path>> vistorM = new HashMap<>();
        vistorM.put("/home/ethan/testData1/conf/jobs", new JobFileV());
        vistorM.put("/home/ethan/testData1/data", new DatFileV());
        LocalFSInit localFSInit = new LocalFSInit(vistorM);
        localFSInit.start();
        TreeCache treeCache = new TreeCache(context.getZkClient().getCurator(),
                ZkConfig.zk_pmMp);
        treeCache.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ChildData ued = treeCache.getCurrentData(
                ZkConfig.zk_pmMp + "/" + context.getLocalNodeId() + "/conf/jobs/" + "20161026162511.UED");
        Map<String, ChildData> m1 = treeCache.getCurrentChildren(
                ZkConfig.zk_pmMp + "/" + context.getLocalNodeId() + "/conf/jobs/" + "20161026162511");
        Map<String, ChildData> m2 = treeCache.getCurrentChildren(
                ZkConfig.zk_pmMp + "/" + context.getLocalNodeId() + "/data/" + "20161026162511");
        assert ued.getData() != null;
        System.out.println(ued.getPath() + " : " +
                new String(ued.getData(), Charset.forName("UTF-8")));
        assert m1 != null;
        m1.forEach((k, v) -> {
            if (v.getData() == null) {
                System.out.println(k);
            } else {
                System.out.println(k + "\n" +
                        v.getPath() + " : " + new String(v.getData(), Charset.forName("UTF-8")));
            }
        });
        assert m2 != null;
        m2.forEach((k, v) -> {
            if (v.getData() == null) {
                System.out.println(k);
            } else {
                System.out.println(k + "\n" +
                        v.getPath() + " : " + new String(v.getData(), Charset.forName("UTF-8")));
            }
        });
        CloseableUtils.closeQuietly(treeCache);
//        CuratorFramework _curator = Context.getInstance().getZkClient().getCurator();
//        PathChildrenCache p1 = new PathChildrenCache(
//                _curator,
//                ZkConfig.zk_pmMp + "/" + context.getLocalNodeId() + "/conf/jobs/" + "20161215162734",
//                true
//        );
//        NodeCache nodeCache = new NodeCache(_curator,
//                ZkConfig.zk_pmMp + "/" + context.getLocalNodeId() + "/conf/jobs/" + "20161215162734.UED");
//        PathChildrenCache p2 = new PathChildrenCache(
//                _curator,
//                ZkConfig.zk_pmMp + "/" + context.getLocalNodeId() + "/data/" + "20161215162734",
//                true
//        );
//        p1.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
//        nodeCache.start();
//        p2.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
//        p1.getCurrentData().forEach(childData -> {
//            if (childData.getData() != null) {
//                System.out.println(childData.getPath() + " : " +
//                        new String(childData.getData(), Charset.forName("UTF-8")));
//            } else {
//                System.out.println(childData.getPath());
//            }
//        });
//        System.out.println(nodeCache.getCurrentData().getPath() + " : " +
//                new String(nodeCache.getCurrentData().getData(), Charset.forName("UTF-8")));
//        p2.getCurrentData().forEach(childData -> {
//            if (childData.getData() != null) {
//                System.out.println(childData.getPath() + " : " +
//                        new String(childData.getData(), Charset.forName("UTF-8")));
//            } else {
//                System.out.println(childData.getPath());
//            }
//        });
//        CloseableUtils.closeQuietly(p1);
//        CloseableUtils.closeQuietly(nodeCache);
//        CloseableUtils.closeQuietly(p2);
//        context.getZkClient().close();
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}