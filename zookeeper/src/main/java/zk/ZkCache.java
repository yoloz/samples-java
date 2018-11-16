package zk;


import assignor.LostBalance;
import beans.NodeInfo;
import beans.ServiceInfo;
import Context;
import assignor.LostStopService;
import beans.MetricInfo;
import common.JsonUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.utils.CloseableUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 */
public class ZkCache implements Closeable {


    private Map<Integer, NodeInfo> nodeMap = new ConcurrentHashMap<>();
    private Map<String, ServiceInfo> serMap = new ConcurrentHashMap<>();
    private Map<String, String> serScheduleMap = new ConcurrentHashMap<>();
    private Map<String, String> pmConfMap = new ConcurrentHashMap<>();
    private Map<String, MetricInfo> nodeMetMap = new ConcurrentHashMap<>();
    private Map<String, MetricInfo> serMetMap = new ConcurrentHashMap<>();
//    private Map<String,String> pmFileMp = new ConcurrentHashMap<>();

    //    private ZkCache(ZkClient zkClient) throws Exception {
//        start(zkClient);
//    }
    private List<PathChildrenCache> pathChildrenCaches = new ArrayList<>(6);
    private TreeCache pmFiles;

    private ZkCache() {
    }

    private static volatile ZkCache instance;

    public static ZkCache getInstance() {
        if (instance == null) {
            synchronized (ZkCache.class) {
                if (instance == null) {
                    instance = new ZkCache();
                }
            }
        }
        return instance;
    }

    public void start(ZkClient zkClient) throws Exception {
        CuratorFramework _curator = zkClient.getCurator();
        nodeCache(_curator);
        serviceCache(_curator);
        pmConfigCache(_curator);
        scheduleCache(_curator);
        nodeMetCache(_curator);
        serMetCache(_curator);
        pmFMpCache(_curator);
    }

    public TreeCache getPmFiles() {
        assert pmFiles != null;
        return pmFiles;
    }

    public Collection<NodeInfo> getAllNodes() {
        return this.nodeMap.values();
    }

    public Set<Integer> getAllNodesId() {
        return this.nodeMap.keySet();
    }

    public NodeInfo getNodeInfo(Integer nodeId) {
        return this.nodeMap.get(nodeId);
    }

    public Collection<ServiceInfo> getAllServices() {
        return this.serMap.values();
    }

//    public NodeInfo nodeById(Integer id) {
//        return this.nodeMap.get(id);
//    }

    public List<ServiceInfo> leaderNodeForSer(Integer nodeId) {
        List<ServiceInfo> services = new ArrayList<>();
        this.serMap.values().forEach(service -> {
            if (service.getLeader().equals(nodeId)) {
                services.add(service);
            }
        });
        return services;
    }

    public Integer getServiceLeader(String serviceId) {
        ServiceInfo info = this.serMap.get(serviceId);
        if (info == null)
            return null;
        else
            return info.getLeader();
    }

    public ServiceInfo getService(String serviceId) {
        return this.serMap.get(serviceId);
    }

    public List<String> getPmConf(String... serviceId) {
        List<String> list = new ArrayList<>(serviceId.length);
        for (String key : serviceId) { //list 可以添加null
            if (this.pmConfMap.containsKey(key)) list.add(this.pmConfMap.get(key));
        }
        return list;
    }

    public Collection<String> getAllPmConf() {
        return this.pmConfMap.values();
    }

    public List<String> getSerSchedule(String... serviceId) {
        List<String> list = new ArrayList<>(serviceId.length);
        for (String key : serviceId) {
            if (this.serScheduleMap.containsKey(key)) list.add(this.serScheduleMap.get(key));
        }
        return list;
    }

    public Collection<MetricInfo> getNodesMetric() {
        return this.nodeMetMap.values();
    }

    public Map<String, MetricInfo> getNodesMetrics() {
        return this.nodeMetMap;
    }

    public MetricInfo getNodeMetric(String nodeId) {
        return this.nodeMetMap.get(nodeId);
    }

    public Collection<MetricInfo> getAllSerMetric() {
        return this.serMetMap.values();
    }

    public List<MetricInfo> getServiceMetric(String... serviceId) {
        List<MetricInfo> list = new ArrayList<>(serviceId.length);
        for (String key : serviceId) {
            if (this.serMetMap.containsKey(key)) list.add(this.serMetMap.get(key));
        }
        return list;
    }

    private void pmFMpCache(CuratorFramework client) throws Exception {
        pmFiles = new TreeCache(client, ZkConfig.zk_pmMp);
        pmFiles.start();
    }

    private void nodeMetCache(CuratorFramework client) throws Exception {
        PathChildrenCache nodeMetCache = new PathChildrenCache(client, ZkConfig.zk_nodeMetric, true);
        nodeMetCache.getListenable().addListener((client1, event) -> handleCache(event, nodeMetCache,
                ZkConfig.zk_nodeMetric, MetricInfo.class, this.nodeMetMap));
        nodeMetCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        pathChildrenCaches.add(nodeMetCache);
    }

    private void serMetCache(CuratorFramework client) throws Exception {
        PathChildrenCache serMetCache = new PathChildrenCache(client, ZkConfig.zk_serMetric, true);
        serMetCache.getListenable().addListener((client1, event) -> handleCache(event, serMetCache,
                ZkConfig.zk_serMetric, MetricInfo.class, this.serMetMap));
        serMetCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        pathChildrenCaches.add(serMetCache);
    }


    private void pmConfigCache(CuratorFramework client) throws Exception {
        PathChildrenCache pmConfigCache = new PathChildrenCache(client, ZkConfig.zk_pmConfig, true);
        pmConfigCache.getListenable().addListener((client1, event) -> handleCache(event, pmConfigCache,
                ZkConfig.zk_pmConfig, String.class, this.pmConfMap));
        pmConfigCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        pathChildrenCaches.add(pmConfigCache);
    }

    private void scheduleCache(CuratorFramework client) throws Exception {
        PathChildrenCache scheduleCache = new PathChildrenCache(client, ZkConfig.zk_schedule, true);
        scheduleCache.getListenable().addListener((client1, event) -> handleCache(event, scheduleCache,
                ZkConfig.zk_schedule, String.class, this.serScheduleMap));
        scheduleCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        pathChildrenCaches.add(scheduleCache);
    }

    private void nodeCache(CuratorFramework client) throws Exception {
        PathChildrenCache nodeCache = new PathChildrenCache(client, ZkConfig.zk_nodes, true);
        nodeCache.getListenable().addListener((client1, event) -> handleNodeCache(event, nodeCache));
        nodeCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        pathChildrenCaches.add(nodeCache);
    }


    private void serviceCache(CuratorFramework client) throws Exception {
        PathChildrenCache serviceCache = new PathChildrenCache(client, ZkConfig.zk_services, true);
        serviceCache.getListenable().addListener((client1, event) -> handleCache(event, serviceCache,
                ZkConfig.zk_services, ServiceInfo.class, this.serMap));
        serviceCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        pathChildrenCaches.add(serviceCache);
    }

    @SuppressWarnings("unchecked")
    private <T> void handleCache(PathChildrenCacheEvent event,
                                 PathChildrenCache cache,
                                 String cachePath,
                                 Class<T> clazz,
                                 Map<String, T> map) throws Exception {
        switch (event.getType()) {
            case CHILD_ADDED:
                String ak = event.getData().getPath().replaceFirst(cachePath, "")
                        .substring(1).trim();
                T ai;
                if (clazz.getName().equals("java.lang.String")) {
                    ai = (T) new String(event.getData().getData(), Context.charset);
                } else {
                    ai = JsonUtils
                            .parse(new String(event.getData().getData(), Context.charset), clazz);
                }
                map.put(ak, ai);
                break;
            case CHILD_REMOVED:
                String dk = event.getData().getPath().replaceFirst(cachePath, "")
                        .substring(1).trim();
                map.remove(dk);
                break;
            case CHILD_UPDATED:
                String uk = event.getData().getPath().replaceFirst(cachePath, "")
                        .substring(1).trim();
                T ui;
                if (clazz.getName().equals("java.lang.String")) {
                    ui = (T) new String(event.getData().getData(), Context.charset);
                } else {
                    ui = JsonUtils
                            .parse(new String(event.getData().getData(), Context.charset), clazz);
                }
                map.put(uk, ui);
                break;
            case CONNECTION_LOST:
                if (clazz.isAssignableFrom(ServiceInfo.class)) {
                    /*由于lost时cache清空,故在service清空前执行本leader全部的服务停止*/
                    LostStopService lostStopService = new LostStopService();
                    lostStopService.run();
                }
                map.clear();
                break;
            case CONNECTION_RECONNECTED:
                map.clear();
                cache.rebuild();
            case INITIALIZED:
                cache.getCurrentData().forEach(childData -> {
                    String key = childData.getPath().replaceFirst(cachePath, "")
                            .substring(1).trim();
                    T t;
                    if (clazz.getName().equals("java.lang.String")) {
                        t = (T) new String(childData.getData(), Context.charset);
                    } else {
                        t = JsonUtils
                                .parse(new String(childData.getData(), Context.charset), clazz);
                    }
                    map.put(key, t);
                });
                break;
            case CONNECTION_SUSPENDED:
                break;
        }
    }

    private void handleNodeCache(PathChildrenCacheEvent event,
                                 PathChildrenCache cache) throws Exception {
        String cachePath = ZkConfig.zk_nodes;
        switch (event.getType()) {
            case CHILD_ADDED:
                String ak = event.getData().getPath().replaceFirst(cachePath, "")
                        .substring(1).trim();
                NodeInfo ai = JsonUtils
                        .parse(new String(event.getData().getData(), Context.charset), NodeInfo.class);
                this.nodeMap.put(Integer.parseInt(ak), ai);
                break;
            case CHILD_REMOVED:
                String dk = event.getData().getPath().replaceFirst(cachePath, "")
                        .substring(1).trim();
                this.nodeMap.remove(Integer.parseInt(dk));
                new Thread(new LostBalance(Integer.parseInt(dk))).start();
                break;
            case CHILD_UPDATED:
                String uk = event.getData().getPath().replaceFirst(cachePath, "")
                        .substring(1).trim();
                NodeInfo ui = JsonUtils
                        .parse(new String(event.getData().getData(), Context.charset), NodeInfo.class);
                this.nodeMap.put(Integer.parseInt(uk), ui);
                break;
            case CONNECTION_LOST:
                this.nodeMap.clear();
                break;
            case CONNECTION_RECONNECTED:
                this.nodeMap.clear();
                cache.rebuild();
            case INITIALIZED:
                cache.getCurrentData().forEach(childData -> {
                    String key = childData.getPath().replaceFirst(cachePath, "")
                            .substring(1).trim();
                    NodeInfo t = JsonUtils
                            .parse(new String(childData.getData(), Context.charset), NodeInfo.class);
                    this.nodeMap.put(Integer.parseInt(key), t);
                });
                break;
            case CONNECTION_SUSPENDED:
                break;
        }
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     * <p>
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        pathChildrenCaches.forEach(CloseableUtils::closeQuietly);
        CloseableUtils.closeQuietly(pmFiles);
    }

}
