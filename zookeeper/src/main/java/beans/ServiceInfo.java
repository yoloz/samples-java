package beans;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class ServiceInfo {

    private String id;
    private Integer leader;

    private List<Integer> replicas;
    private List<Integer> inSyncReplicas;

    public ServiceInfo(String id) {
        this(id, 0);
    }

    public ServiceInfo(String id, Integer leader) {
        this(id, leader, Collections.emptyList(), Collections.emptyList());
    }

    public ServiceInfo(String id, Integer leader, List<Integer> replicas, List<Integer> inSyncReplicas) {
        this.id = id;
        this.leader = leader;
        this.replicas = replicas;
        this.inSyncReplicas = inSyncReplicas;
    }


    public void setLeader(Integer leader) {
        this.leader = leader;
    }

    public void addReplicas(Integer nodeInfo) {
        if (replicas.isEmpty()) {
            replicas = new ArrayList<>();
        }
        replicas.add(nodeInfo);
    }

    public void addInsyncReplicas(Integer nodeInfo) {
        if (inSyncReplicas.isEmpty()) {
            inSyncReplicas = new ArrayList<>();
        }
        inSyncReplicas.add(nodeInfo);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getLeader() {
        return leader;
    }

    public List<Integer> getReplicas() {
        return replicas;
    }

    public void setReplicas(List<Integer> replicas) {
        this.replicas = replicas;
    }

    public List<Integer> getInSyncReplicas() {
        return inSyncReplicas;
    }

    public void setInSyncReplicas(List<Integer> inSyncReplicas) {
        this.inSyncReplicas = inSyncReplicas;
    }

    @Override
    public String toString() {
        return String.format("Service(id = %s, leader = %s, replicas = %s, isr = %s)",
                id,
                leader == null ? "" : leader,
                formatNodeIds(replicas),
                formatNodeIds(inSyncReplicas));
    }

    private String formatNodeIds(List<Integer> nodes) {
        StringBuilder b = new StringBuilder("[");
        for (int i = 0; i < nodes.size(); i++) {
            b.append(nodes.get(i));
            if (i < nodes.size() - 1)
                b.append(',');
        }
        b.append("]");
        return b.toString();
    }

}
