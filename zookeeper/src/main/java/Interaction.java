import beans.NodeInfo;
import beans.MetricInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface Interaction {
    /*
    *
    * 原始的UTL逻辑判断继续,ZK交互未涉及,如启动不可修改,未部署不可运行等...........
    *
    */

    /**
     * get serviceId
     *
     * @return service id
     */
    public String createServiceId() throws Exception;

    /**
     * add service and assignor leader
     *
     * @param serviceId         service id
     * @param assignorClassName assignor strategy
     */

    public void createService(String serviceId, String assignorClassName) throws Exception;

    /**
     * involving the designer,not impl
     * just to lock or to deter whether other is mod
     *
     * @param serviceId service id
     * @param start     if you finished false to set
     * @return true/false if other is using,you can not mod
     */
    public boolean modifyService(String serviceId, boolean start);

    /**
     * del zk path
     *
     * @param serviceId service id
     * @param dp        data properties to other node
     * @param start     if you finished false to set
     * @return true/false if other is using,you can not del
     */
//    public boolean deleteService(String serviceId, DataProperties dp, boolean start);

    /**
     * start or stop service by leader node
     *
     * @param serviceId service id
     * @return leader node ip address
     */
    public String getServiceLeaderHost(String serviceId);

    /**
     * update pm config.xml
     *
     * @param serviceId service id
     * @param json      json
     * @return true/false if other is using,you can not deploy
     */
    public void updatePmConf(String serviceId, String json);

    /**
     * deploy service
     *
     * @param serviceId service id
     * @param start     if you finished false to set
     * @return true/false if other is using,you can not del
     */
    public boolean deployService(String serviceId, boolean start);

    /**
     * delete pm conf
     *
     * @param serviceId service id
     * @return true/false if other is using,you can not deploy
     */
    public void deletePmConf(String serviceId);

    /**
     * @param serviceIds service ids or null
     * @return you set update json
     */
    public List<String> getPmConf(String... serviceIds);

    /**
     * @return you set update json
     */
    public Collection<String> getAllPmConf();

    /**
     * add or update schedule info
     *
     * @param serviceId service id
     * @param json      json
     * @return true/false if other is using,you can not set schedule
     */
    public void updateScheduleInfo(String serviceId, String json);

    /**
     * config service schedule
     *
     * @param serviceId service id
     * @param start     if you finished false to set
     * @return true/false if other is using,you can not del
     */
    public boolean configSchedule(String serviceId, boolean start);

    /**
     * delete service id
     *
     * @param serviceId service id
     * @return true/false if other is using,you can not deploy
     */
    public boolean deleteScheduleInfo(String serviceId);

    /**
     * get service schedule info
     *
     * @param serviceIds service id
     * @return you set update json
     */
    public List<String> getScheduleInfo(String... serviceIds);

    /**
     * set cpu,mem for that node
     *
     * @param metricInfo cpu.mem.etc
     */
    public void setMetricForNode(MetricInfo metricInfo);

    /**
     * set cpu,mem for service who's leader is that node
     *
     * @param serviceId  service id
     * @param metricInfo cpu.mem.etc
     */
    public void setMetricForService(String serviceId, MetricInfo metricInfo);

    /**
     * get all nodes metric
     *
     * @return all metric
     */
    public Map<String,MetricInfo> getAllNodeMetric();

    /**
     * get one nodes metric
     *
     * @return all metric
     */
    public MetricInfo getNodeMetric(String nodeId);

    /**
     * get all service metric
     *
     * @return all metric
     */
    public Collection<MetricInfo> getAllServiceMetric();

    /**
     * get service metric
     *
     * @param serviceId services
     * @return service metric
     */
    public List<MetricInfo> getServiceMetric(String... serviceId);

    /**
     * deter whether the service can run
     *
     * @param serviceId service id
     * @return true/false
     */
    public boolean possibleToRun(String serviceId);

    /**
     * get all alive nodes
     * @return nodes
     */
    public Collection<NodeInfo> getAllAliveNodes();
}
