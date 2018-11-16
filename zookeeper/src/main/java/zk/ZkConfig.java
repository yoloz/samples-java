package zk;


public class ZkConfig {

    /**
     * configurations
     */
    public static final String node_id = "node.id";
    public static final String UiPort = "UiPort";
    public static final String listener = "listener";
    public static final String service_leader_sync_time = "service.leader.sync.time.ms";
    public static final String service_follower_sync_time = "service.follower.sync.time.ms";

    static final String zookeeper_address = "zookeeper.connect";
    static final String zookeeper_connect_timeout = "zookeeper.connection.timeout.ms";
    static final String zookeeper_session_timeout = "zookeeper.session.timeout.ms";
    public static final String zookeeper_retry_times = "zookeeper.retry.times";
    static final String zookeeper_retry_sleep = "zookeeper.retry.sleep.ms";


    /**
     * zk path config
     */
    private static final String zk_root = "/uni_utl";
    public static final String zk_controller = zk_root + "/controller";//暂时无主CN
    private static final String zk_config = zk_root + "/config";
    public static final String zk_schedule = zk_config + "/schedule";
    public static final String zk_pmConfig = zk_config + "/pmConfig";
    public static final String zk_services = zk_root + "/services";
    public static final String zk_nodes = zk_root + "/nodes";
    private static final String zk_mapfiles = zk_root + "/mapping";
    public static final String zk_pmMp = zk_mapfiles + "/pm";
    public static final String zk_uiMp = zk_mapfiles + "/ui";
    private static final String zk_metric = zk_root + "/metric";
    public static final String zk_nodeMetric = zk_metric + "/nodes";
    public static final String zk_serMetric = zk_metric + "/services";
    public static final String zk_lock = zk_root + "/lock";
    public static final String zk_serviceId = zk_root + "/serviceId";


}