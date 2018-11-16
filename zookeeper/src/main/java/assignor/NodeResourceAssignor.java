package assignor;

import beans.MetricInfo;
import beans.NodeInfo;
import zk.ZkCache;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 */
public class NodeResourceAssignor implements ServiceAssignor {
    private final Logger logger = Logger.getLogger(this.getClass());

//    @Override
//    public Integer assign(ClusterInfo metadata, String serviceId) {
//        return -1;
//    }

    @Override
    public Integer assign(String serviceId) {
//        List<String> insync = ZkCache.getInstance().getService(serviceId).getInSyncReplicas();
        Set<Integer> insync = ZkCache.getInstance().getAllNodesId();
        List<NodeInfo> allNodes = new ArrayList<>(ZkCache.getInstance().getAllNodes());
        Map<Integer, Float> resource = new HashMap<>();
        try {
            insync.forEach(nodeId -> {
                MetricInfo metricInfo = ZkCache.getInstance().getNodeMetric(nodeId + "");
                if (metricInfo != null) {
                    MetricInfo.InnerData innerData = metricInfo.getLastInnerData();
                    resource.put(nodeId,
                            innerData.getCpu() + innerData.getMem());
                }
            });
            if (resource.isEmpty()) {
                return allNodes.get(new Random().nextInt(allNodes.size())).getId();
            } else {
                List<Integer> assignorNode = new ArrayList<>(resource.size());
                float min = Collections.min(resource.values());
                resource.forEach((k, v) -> {
                    if (v == min) assignorNode.add(k);
                });
                return assignorNode.get(new Random().nextInt(assignorNode.size()));
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return allNodes.get(new Random().nextInt(allNodes.size())).getId();
    }

    public Integer assign(List<Integer> insync) {
        List<NodeInfo> allNodes = new ArrayList<>(ZkCache.getInstance().getAllNodes());
        Map<Integer, Float> resource = new HashMap<>();
        insync.forEach(nodeId -> {
            MetricInfo metricInfo = ZkCache.getInstance().getNodeMetric(nodeId + "");
            if (metricInfo != null) {
                MetricInfo.InnerData innerData = metricInfo.getLastInnerData();
                resource.put(nodeId,
                        innerData.getCpu() + innerData.getMem());
            }
        });
        if (resource.isEmpty()) {
            return allNodes.get(new Random().nextInt(allNodes.size())).getId();
        } else {
            List<Integer> assignorNode = new ArrayList<>(resource.size());
            float min = Collections.min(resource.values());
            resource.forEach((k, v) -> {
                if (v == min) assignorNode.add(k);
            });
            return assignorNode.get(new Random().nextInt(assignorNode.size()));
        }
    }

    @Override
    public String name() {
        return "simpleNodeResourceAssignor";
    }
}
