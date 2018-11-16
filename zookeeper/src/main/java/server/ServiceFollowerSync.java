package server;


import beans.NodeInfo;
import beans.ServiceInfo;
import common.JsonUtils;
//import com.unimas.cn.controller.pm.inter.zk.InteractionForZk;
import Context;
import zk.ZkCache;
import zk.ZkConfig;
import zk.ZkUtils;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 */
public class ServiceFollowerSync implements Runnable {

    private final Logger logger = Logger.getLogger(this.getClass());

    private boolean running = true;

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(Context.getInstance().getFollowerSyncTime());
            } catch (InterruptedException e) {
                logger.error(e);  //ignore
            }
            ZkCache zkCache = ZkCache.getInstance();
            if (zkCache.getAllNodesId().isEmpty()) continue;
            Context context = Context.getInstance();
            int leaderId = context.getLocalNodeId();
            List<ServiceInfo> services = zkCache.leaderNodeForSer(leaderId);
//            TreeCache treeCache = new TreeCache(context.getZkClient().getCurator(), ZkConfig.zk_pmMp);
            TreeCache treeCache = zkCache.getPmFiles();
            try {
//                treeCache.start();
//                Thread.sleep(2000); //测试中发现如果没有sleep则取出的数据都为空
                for (ServiceInfo service : services) {
                    if (service.getInSyncReplicas().isEmpty()) {
                        logger.error("service offline " + service.getId());
//                        InteractionForZk.getInstance().setJobStatus(service.getId(), "offline");
                        continue;
                    }
                    if (service.getInSyncReplicas().contains(leaderId)) {
                        this.syncFollower(service, leaderId, treeCache);
                    }
                }
            } catch (Exception e) {
                logger.error(e);
//            } finally {
//                CloseableUtils.closeQuietly(treeCache);
            }
        }
    }

    public void stop() {
        running = false;
    }

    private void syncFollower(ServiceInfo serviceInfo, int leaderNodeId, TreeCache treeCache) throws Exception {
        Map<String, ChildData> m1 = treeCache.getCurrentChildren(
                ZkConfig.zk_pmMp + "/" + leaderNodeId + "/conf/jobs/" + serviceInfo.getId());
        Map<String, ChildData> m2 = treeCache.getCurrentChildren(
                ZkConfig.zk_pmMp + "/" + leaderNodeId + "/data/" + serviceInfo.getId());
        ChildData ued = treeCache.getCurrentData(
                ZkConfig.zk_pmMp + "/" + leaderNodeId + "/conf/jobs/" + serviceInfo.getId() + ".UED");

        logger.debug(" follower sync " + serviceInfo.getId() + " job file " + JsonUtils.toJson(m1));
        logger.debug(" follower sync " + serviceInfo.getId() + " dat file " + JsonUtils.toJson(m2));
        logger.debug(" follower sync " + serviceInfo.getId() + " ued file " + JsonUtils.toJson(ued));

        for (NodeInfo nodeInfo : ZkCache.getInstance().getAllNodes()) {
            if (Objects.equals(nodeInfo.getId(), leaderNodeId)) continue;
            boolean isSync = true;
            Map<String, ChildData> o1 = treeCache.getCurrentChildren(
                    ZkConfig.zk_pmMp + "/" + nodeInfo.getId() + "/conf/jobs/" + serviceInfo.getId());
            Map<String, ChildData> o2 = treeCache.getCurrentChildren(
                    ZkConfig.zk_pmMp + "/" + nodeInfo.getId() + "/data/" + serviceInfo.getId());
            ChildData oued = treeCache.getCurrentData(
                    ZkConfig.zk_pmMp + "/" + nodeInfo.getId() + "/conf/jobs/" + serviceInfo.getId() + ".UED");

            logger.debug(" follower unknown sync " + serviceInfo.getId() + " job file " + JsonUtils.toJson(o1));
            logger.debug(" follower unknown sync " + serviceInfo.getId() + " dat file " + JsonUtils.toJson(o2));
            logger.debug(" follower unknown sync " + serviceInfo.getId() + " ued file " + JsonUtils.toJson(oued));

            if (ued != null && (oued == null ||
                    !this.byteToString(ued.getData()).equals(this.byteToString(oued.getData())))) {
                isSync = false;
                logger.debug(" trans " + serviceInfo.getId() + " ued file from " + leaderNodeId + " to " + nodeInfo.getId()
                        + " " + nodeInfo.getHost() + nodeInfo.getPort() +
                        this.zkToLocalPath(ued.getPath(), leaderNodeId + ""));
//                TransUtil.transFile(nodeInfo.getHost(), nodeInfo.getPort() + "",
//                        this.zkToLocalPath(ued.getPath(), leaderNodeId + ""));
            }
            if (!this.compareSyncMap(m1, o1, nodeInfo, leaderNodeId + "")) isSync = false;
            if (!this.compareSyncMap(m2, o2, nodeInfo, leaderNodeId + "")) isSync = false;

            if (!isSync && serviceInfo.getInSyncReplicas().contains(nodeInfo.getId())) {
                serviceInfo.getInSyncReplicas().remove(nodeInfo.getId());
                logger.info("node " + nodeInfo.getHost() + " service " + serviceInfo.getId() + " remove sync");
            } else if (isSync && !serviceInfo.getInSyncReplicas().contains(nodeInfo.getId())) {
                logger.info("node " + nodeInfo.getHost() + " service " + serviceInfo.getId() + " add sync");
                serviceInfo.getInSyncReplicas().add(nodeInfo.getId());
            } else continue;

            ZkUtils.createOrUpdate(Context.getInstance().getZkClient(),
                    ZkConfig.zk_services + "/" + serviceInfo.getId(),
                    JsonUtils.toJson(serviceInfo),
                    CreateMode.PERSISTENT);
        }
    }


    private boolean compareSyncMap(Map<String, ChildData> source, Map<String, ChildData> other,
                                   NodeInfo nodeInfo, String syncNodeId) {
        boolean isSync = true;
        if (source != null) {
            for (Map.Entry<String, ChildData> entry : source.entrySet()) {
                String key = entry.getKey();
                ChildData value = entry.getValue();
                if (other != null && other.containsKey(key)) {
                    if (value.getData() != null) {
                        String v1 = byteToString(other.get(key).getData());
                        if (!byteToString(value.getData()).equals(v1)) {
                            isSync = false;
                            logger.debug(" trans file to " + nodeInfo.getHost() + nodeInfo.getPort()
                                    + zkToLocalPath(value.getPath(), syncNodeId));
//                            TransUtil.transFile(nodeInfo.getHost(), nodeInfo.getPort() + "",
//                                    zkToLocalPath(value.getPath(), syncNodeId));
                        }
                    }
                } else {
                    if (value.getData() != null) {
                        isSync = false;
                        logger.debug(" trans file to " + nodeInfo.getHost() + nodeInfo.getPort()
                                + zkToLocalPath(value.getPath(), syncNodeId));
//                        TransUtil.transFile(nodeInfo.getHost(), nodeInfo.getPort() + "",
//                                zkToLocalPath(value.getPath(), syncNodeId));
                    }
                }
            }
        }
        return isSync;
    }

    private String zkToLocalPath(String zkPath, String nodeId) {
//        return TransUtil.replacePrefixPath(zkPath,
//                ZkConfig.zk_pmMp + "/" + nodeId,
//                Context.getInstance().getCustomDir().toString());
        return null;
    }

    private String byteToString(byte[] data) {
        return new String(data, Context.charset);
    }
}