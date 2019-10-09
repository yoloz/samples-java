
package zk;

import common.JsonUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class ZkUtils {

    private static final Logger logger = Logger.getLogger(ZkUtils.class);


    public static void initZkRoot(ZkClient zkClient) throws Exception {
        CreateMode mode = CreateMode.PERSISTENT;
//        create(zkClient, ZkConfig.zk_controller, "", mode);
//        create(zkClient, ZkConfig.zk_schedule, "", mode);
//        create(zkClient, ZkConfig.zk_pmConfig, "", mode);
//        create(zkClient, ZkConfig.zk_services, "", mode);
//        create(zkClient, ZkConfig.zk_nodes, "", mode);
//        create(zkClient, ZkConfig.zk_mapfiles, "", mode);
//        create(zkClient, ZkConfig.zk_nodeMetric, "", mode);
//        create(zkClient, ZkConfig.zk_serMetric, "", mode);
//        create(zkClient, ZkConfig.zk_lock, "", mode);
        create(zkClient, ZkConfig.zk_serviceId,
                new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                , mode);
    }


    public static void deletePath(String path) {
        try {
            Context.getI .getInstance().getCurator().delete().deletingChildrenIfNeeded().forPath(path);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public static <T> T readJSON(ZkClient zkClient, String path, Class<T> clazz) throws Exception {
        byte[] b = readBytes(zkClient, path);
        if (b == null) {
            return null;
        }
        return JsonUtils.parse(new String(b, Context.charset), clazz);
    }

    public static String read(ZkClient zkClient, String path) throws Exception {
        byte[] b = readBytes(zkClient, path);
        if (b == null) {
            return null;
        }
        return new String(b, Context.charset);
    }

    private static void createOrUpdateBytes(ZkClient zkClient, String path, byte[] bytes, CreateMode mode) throws Exception {
        CuratorFramework _curator = zkClient.getCurator();
        if (_curator.checkExists().forPath(path) == null) {
            if (mode == null) mode = CreateMode.EPHEMERAL;
            _curator.create().creatingParentsIfNeeded()
                    .withMode(mode).forPath(path, bytes);
        } else {
            _curator.setData().forPath(path, bytes);
        }
    }

    private static void createBytes(ZkClient zkClient, String path, byte[] bytes, CreateMode mode) throws Exception {
        CuratorFramework _curator = zkClient.getCurator();
        if (_curator.checkExists().forPath(path) == null) {
            if (mode == null) mode = CreateMode.EPHEMERAL;
            _curator.create().creatingParentsIfNeeded()
                    .withMode(mode).forPath(path, bytes);
        }
    }

    private static void writeBytesExist(ZkClient zkClient, String path, byte[] bytes) throws Exception {
        zkClient.getCurator().setData().forPath(path, bytes);
    }

    private static byte[] readBytes(ZkClient zkClient, String path) throws Exception {
        CuratorFramework _curator = zkClient.getCurator();
        if (_curator.checkExists().forPath(path) != null) {
            return _curator.getData().forPath(path);
        } else {
            return null;
        }
    }


}
