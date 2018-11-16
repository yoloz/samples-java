package server.fs;

import Context;
import zk.ZkCache;
import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 */
public class SyncFileUtils {

    private static final Logger logger = Logger.getLogger(SyncFileUtils.class);

    /*
     *暂时发现,部署的时候设计器生成UED(.zip)压缩文件,
     * 会直接服务id然后jobid.ujx,transid.utx,
     * 即会和ui的文件结构有出入的.
     * 这么说来服务文件的jobfile同步只需同步UED文件即可(后续可以改成这样),
     * 然后解压(最好先了解设计器,pm原始设计逻辑,这里暂时也不清楚).
     */

    /**
     * 部署完成后同步ui的job file到其他节点
     * 这样其他节点也可以修改了
     * 暂时没有保证这次失败会在此同步
     * 后续如果需要可以仿照pm的follower机制操作
     *
     * @param serviceId service id
     */
    public static void syncUiDeployJobFile(String serviceId) {
        logger.info("sync ui deploy job file to other node......");
        Path ued = Paths.get(Context.getInstance().getUiJobsDir(), serviceId + ".UED");
        Path pathDir = Paths.get(Context.getInstance().getUiJobsDir(), serviceId);
        transSyncFile(ued, pathDir);
        syncPmDeployJobFile(serviceId);
    }

    /**
     * 部署完成的时候同步pm的job file到其他节点
     * 有service follower保证会同步(即这次如果失败,follower的定时调度中会在此抓取文件)
     *
     * @param serviceId service id
     */
    private static void syncPmDeployJobFile(String serviceId) {
        logger.info("sync pm deploy job file to other node......");
        Path ued = Paths.get(Context.getInstance().getPmJobsDir(), serviceId + ".UED");
        Path pathDir = Paths.get(Context.getInstance().getPmJobsDir(), serviceId);
        transSyncFile(ued, pathDir);
    }

    private static void transSyncFile(Path ued, Path dir) {
        int localNodeId = Context.getInstance().getLocalNodeId();
        ZkCache.getInstance().getAllNodes().forEach(node -> {
            if (!Objects.equals(localNodeId, node.getId())) {
//                TransUtil.transFile(node.getHost(), node.getPort() + "", ued.toString());
//                TransUtil.transFile(node.getHost(), node.getPort() + "", dir.toString());
            }
        });
    }
}
