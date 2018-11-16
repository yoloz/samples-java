package assignor;

import beans.ServiceInfo;
import common.JsonUtils;
import Context;
import zk.ZkCache;
import zk.ZkConfig;
import zk.ZkUtils;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 */
public class LostBalance implements Runnable {

    private final Logger logger = Logger.getLogger(this.getClass());
    private int lostNode;

    public LostBalance(int lostNode) {
        this.lostNode = lostNode;
    }

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
        List<ServiceInfo> services = ZkCache.getInstance().leaderNodeForSer(lostNode);
        logger.info(" node " + lostNode + " has lost...... balance start...service size " +
                services.size());
        NodeResourceAssignor nodeResourceAssignor = new NodeResourceAssignor();
        services.forEach(service -> {
            logger.debug(" service " + service.getId() + " balance......");
            boolean remove = service.getInSyncReplicas().remove((Integer) lostNode);
            logger.debug(" remove==========" + remove);
            if (service.getInSyncReplicas().isEmpty()) {
                logger.error("service offline " + service.getId());
//                try {
//                    InteractionForZk.getInstance().setJobStatus(service.getId(), "offline");
//                } catch (X x) {
//                    logger.error(x);
//                }
            } else {
                List<Integer> syncBack = service.getInSyncReplicas();
                int localNode = Context.getInstance().getLocalNodeId();
                logger.debug(" balance service info: " + JsonUtils.toJson(service) +
                        " localNode " + localNode + " min " + Collections.min(syncBack));
                if (localNode == Collections.min(syncBack)) {
                    logger.debug(" get new leader start ");
                    int leaderId = nodeResourceAssignor.assign(syncBack);
                    logger.debug(" new leader is " + leaderId);
                    service.setLeader(leaderId);
                    String serviceInfo = JsonUtils.toJson(service);
                    logger.debug(" new service info " + serviceInfo);
                    try {
                        ZkUtils.createOrUpdate(Context.getInstance().getZkClient(),
                                ZkConfig.zk_services + "/" + service.getId(),
                                serviceInfo,
                                CreateMode.PERSISTENT);
                        logger.info(" service " + service.getId() +
                                " leader lost and new leader is " + service.getLeader());
                    } catch (Exception e) {
                        logger.error(e);
                    }
//                    AppMetaInfo appMetaInfo = JsonUtils.parse(
//                            ZkCache.getInstance().getPmConf(service.getId()).get(0),
//                            AppMetaInfo.class);
//                    if (appMetaInfo != null &&
//                            appMetaInfo.getStatus().equals(AppStatus.START)) {
//                        if (Objects.equals(localNode, leaderId)) {
//                            InteractionForZk.getInstance().startAppImmediately(service.getId());
//                        } else new Thread(() -> {
//                            String host = ZkCache.getInstance().getNodeInfo(leaderId).getHost();
//                            DataProperties dp = new DataProperties();
//                            dp.putValue(Constant.key_jobcode, service.getId());
//                            try {
//                                TransUtil.oldTrans(host, Context.getInstance().getUiPort(), dp);
//                            } catch (Exception e) {
//                                logger.error(e);
//                            }
//                        }).start();
//                    }
                }
            }
        });
        logger.info(" node " + lostNode + " has lost...... balance end......");
    }
}
