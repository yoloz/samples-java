package assignor;

import beans.ServiceInfo;
import Context;
import zk.ZkCache;
import org.apache.log4j.Logger;

import java.util.List;

/**
 */
public class LostStopService implements Runnable {

    private final Logger logger = Logger.getLogger(this.getClass());

//    private Collection<ServiceInfo> serviceInfos;
//    public LostStopService(Collection<ServiceInfo> serviceInfos){
//        this.serviceInfos = serviceInfos;
//    }

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
        int lostNode = Context.getInstance().getLocalNodeId();
        List<ServiceInfo> services = ZkCache.getInstance().leaderNodeForSer(lostNode);
        services.forEach(service -> {
            /*
            *由于lost时cache清空,故在service清空前执行本leader全部的服务停止
            *
             */
//            AppMetaInfo appMetaInfo = JsonUtils.parse(
//                    ZkCache.getInstance().getPmConf(service.getId()).get(0),
//                    AppMetaInfo.class);
//            if (appMetaInfo != null &&
//                    appMetaInfo.getStatus().equals(AppStatus.START)) {
            logger.info(" stop service " + service.getId());
//            InteractionForZk.getInstance().stopAppCompletely(service.getId());
//            }
        });
    }
}
