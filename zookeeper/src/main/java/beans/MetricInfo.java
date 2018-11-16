package beans;

import java.util.LinkedList;
import java.util.List;

/**
 * 系统消耗资源或者服务消耗资源
 * <p>
 */
public class MetricInfo {


    private String serviceId;
    private Integer nodeId;
    private Queue<InnerData> innerData;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public List<InnerData> getInnerData() {
        return innerData.getAll();
    }

    public void setInnerData(List<InnerData> innerData) {
        innerData.forEach(metric -> this.innerData.offer(metric));
    }

    public void addInnerData(InnerData... innerData) {
        for (InnerData metric : innerData) {
            this.innerData.offer(metric);
        }
    }

    public InnerData getLastInnerData() {
        return this.innerData.getLast();
    }

    public class InnerData {
        private float cpu;
        private float mem;
        private float disk;
        private long millis;

        public float getCpu() {
            return cpu;
        }

        public void setCpu(float cpu) {
            this.cpu = cpu;
        }

        public float getMem() {
            return mem;
        }

        public void setMem(float mem) {
            this.mem = mem;
        }

        public float getDisk() {
            return disk;
        }

        public void setDisk(float disk) {
            this.disk = disk;
        }

        public long getMillis() {
            return millis;
        }

        public void setMillis(long millis) {
            this.millis = millis;
        }
    }

    public MetricInfo() {
        this.innerData = new Queue<>(60);
    }

    private class Queue<E> {

        private int limit;
        private LinkedList<E> queue = new LinkedList<>();

        Queue(int limit) {
            this.limit = limit;
        }


        void offer(E e) {
            if (queue.size() >= limit) {
                queue.poll();
            }
            queue.offer(e);
        }

        E get(int position) {
            return queue.get(position);
        }

        E getLast() {
            return queue.getLast();
        }

        E getFirst() {
            return queue.getFirst();
        }

        int getLimit() {
            return limit;
        }

        int size() {
            return queue.size();
        }

        List<E> getAll() {
            return queue;
        }

    }
}
