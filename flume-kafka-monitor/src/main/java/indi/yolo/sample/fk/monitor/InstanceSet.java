package indi.yolo.sample.fk.monitor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 17-2-21.
 */
class InstanceSet {

    final static Map<String, String> objectNames = new HashMap<>();

    static {
        objectNames.put("messageInCount", "kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec");
        objectNames.put("byteInCount", "kafka.server:type=BrokerTopicMetrics,name=BytesInPerSec");
        objectNames.put("byteOutCount", "kafka.server:type=BrokerTopicMetrics,name=BytesOutPerSec");
        objectNames.put("failedFetchRequestsCount", "kafka.server:type=BrokerTopicMetrics,name=FailedFetchRequestsPerSec");
        objectNames.put("failedProduceRequestsCount", "kafka.server:type=BrokerTopicMetrics,name=FailedProduceRequestsPerSec");
        objectNames.put("totalFetchRequestsCount", "kafka.server:type=BrokerTopicMetrics,name=TotalFetchRequestsPerSec");
        objectNames.put("totalProduceRequestsCount", "kafka.server:type=BrokerTopicMetrics,name=TotalProduceRequestsPerSec");
    }

//    /**
//     * replicaManager
//     */
//    public static final String partitionCount = "kafka.server:type=ReplicaManager,name=PartitionCount";
//    public static final String leaderCount = "kafka.server:type=ReplicaManager,name=LeaderCount";

}
