package assignor;

import beans.NodeInfo;
import beans.ServiceInfo;
import zk.ZkCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 */
public class RangeAssignor implements ServiceAssignor {

//    /**
//     * 返回拥有服务最少的节点
//     *
//     * @param metadata  clusterInfo
//     * @param serviceId new service
//     * @return nodeId
//     */
//    @Override
//    public Integer assign(ClusterInfo metadata, String serviceId) {
//        return -1;
//    }

    @Override
    public Integer assign(String serviceId) {
        List<Integer> nodeIds = new ArrayList<>(1);
        List<NodeInfo> nodes = new ArrayList<>(ZkCache.getInstance().getAllNodes());
        List<ServiceInfo> services = new ArrayList<>(ZkCache.getInstance().getAllServices());
        Map<Integer, Integer> nodesCount = new HashMap<>(nodes.size());
        Map<Integer, Integer> leaderCountForNode = new HashMap<>();
        services.forEach(serviceInfo -> {
            int nodeId = serviceInfo.getLeader();
            if (leaderCountForNode.containsKey(nodeId)) {
                leaderCountForNode.put(nodeId, leaderCountForNode.get(nodeId) + 1);
            } else {
                leaderCountForNode.put(nodeId, 1);
            }
        });
        if (leaderCountForNode.isEmpty()) {
            return nodes.get(new Random().nextInt(nodes.size())).getId();
        } else {
            nodes.forEach(node -> nodesCount.put(node.getId(), leaderCountForNode.get(node.getId())));
            int num = Collections.min(nodesCount.values());
            nodesCount.forEach((k, v) -> {
                if (v == num) nodeIds.add(k);
            });
            return nodeIds.get(new Random().nextInt(nodeIds.size()));
        }
    }

    @Override
    public String name() {
        return "range";
    }

}