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
import java.util.Random;

/**
 *
 */
public class ServiceLeaderSync implements Runnable {

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
            ZkCache zkCache = ZkCache.getInstance();
            if (zkCache.getAllNodesId().isEmpty()) continue;
            Context context = Context.getInstance();
            int localNode = context.getLocalNodeId();
            List<ServiceInfo> services = zkCache.leaderNodeForSer(localNode);
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
                    if (!service.getInSyncReplicas().contains(localNode)) {
                        List<Integer> syncNodes = service.getInSyncReplicas();
                        int syncNodeId = syncNodes.get(new Random().nextInt(syncNodes.size()));
                        this.syncLeader(service, syncNodeId, treeCache);
                    }
                }
            } catch (Exception e) {
                logger.error(e);
//            } finally {
//                CloseableUtils.closeQuietly(treeCache);
            }
            try {
                Thread.sleep(Context.getInstance().getLeaderSyncTime());
            } catch (InterruptedException e) {
                logger.error(e); //ignore
            }
        }
    }

    public void stop() {
        running = false;
    }

    private void syncLeader(ServiceInfo serviceInfo, int syncNodeId, TreeCache treeCache) throws Exception {
        Map<String, ChildData> m1 = treeCache.getCurrentChildren(
                ZkConfig.zk_pmMp + "/" + syncNodeId + "/conf/jobs/" + serviceInfo.getId());
        Map<String, ChildData> m2 = treeCache.getCurrentChildren(
                ZkConfig.zk_pmMp + "/" + syncNodeId + "/data/" + serviceInfo.getId());
        ChildData ued = treeCache.getCurrentData(
                ZkConfig.zk_pmMp + "/" + syncNodeId + "/conf/jobs/" + serviceInfo.getId() + ".UED");
        logger.debug(" leader sync " + serviceInfo.getId() + " job file " + JsonUtils.toJson(m1));
        logger.debug(" leader sync " + serviceInfo.getId() + " dat file " + JsonUtils.toJson(m2));
        logger.debug(" leader sync " + serviceInfo.getId() + " ued file " + JsonUtils.toJson(ued));
        NodeInfo syncNode = ZkCache.getInstance().getNodeInfo(syncNodeId);
        boolean isSync = true;
        Map<String, ChildData> o1 = treeCache.getCurrentChildren(
                ZkConfig.zk_pmMp + "/" + serviceInfo.getLeader() + "/conf/jobs/" + serviceInfo.getId());
        Map<String, ChildData> o2 = treeCache.getCurrentChildren(
                ZkConfig.zk_pmMp + "/" + serviceInfo.getLeader() + "/data/" + serviceInfo.getId());
        ChildData oued = treeCache.getCurrentData(
                ZkConfig.zk_pmMp + "/" + serviceInfo.getLeader() + "/conf/jobs/" + serviceInfo.getId() + ".UED");
        logger.debug(" leader unknown sync " + serviceInfo.getId() + " job file " + JsonUtils.toJson(o1));
        logger.debug(" leader unknown sync " + serviceInfo.getId() + " dat file " + JsonUtils.toJson(o2));
        logger.debug(" leader unknown sync " + serviceInfo.getId() + " ued file " + JsonUtils.toJson(oued));
        if (ued != null && (oued == null ||
                !this.byteToString(ued.getData()).equals(this.byteToString(oued.getData())))) {
            isSync = false;
            logger.debug(" request file from " + syncNode.getHost() + syncNode.getPort() + ued.getPath());
//            TransUtil.requestFile(syncNode.getHost(), syncNode.getPort() + "", ued.getPath());
        }
        if (!this.compareSyncMap(m1, o1, syncNode)) isSync = false;
        if (!this.compareSyncMap(m2, o2, syncNode)) isSync = false;
        if (isSync) {
            serviceInfo.getInSyncReplicas().add(serviceInfo.getLeader());
            ZkUtils.createOrUpdate(Context.getInstance().getZkClient(),
                    ZkConfig.zk_services + "/" + serviceInfo.getId(),
                    JsonUtils.toJson(serviceInfo),
                    CreateMode.PERSISTENT);
            logger.info(" service " + serviceInfo.getId() + " leader sync");
        }
    }


    private boolean compareSyncMap(Map<String, ChildData> source, Map<String, ChildData> other,
                                   NodeInfo syncNode) {
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
                            logger.debug(" request file from " + syncNode.getHost() + syncNode.getPort()
                                    + value.getPath());
//                            TransUtil.requestFileByZkPath(syncNode.getHost(), syncNode.getPort() + "",
//                                    value.getPath());
                        }
                    }
                } else {
                    if (value.getData() != null) {
                        isSync = false;
                        logger.debug(" request file from " + syncNode.getHost() + syncNode.getPort()
                                + value.getPath());
//                        TransUtil.requestFileByZkPath(syncNode.getHost(), syncNode.getPort() + "",
//                                value.getPath());
                    }
                }
            }
        }
        return isSync;
    }

    private String byteToString(byte[] data) {
        return new String(data, Context.charset);
    }
}
