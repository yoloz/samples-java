package zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.util.Properties;

/**
 *
 */
public class ZkClient implements Closeable {
    private final Logger logger = Logger.getLogger(this.getClass());

    private CuratorFramework _curator;

    private int ZK_SESSION_TIMEOUT = 60000;
    private int ZK_RETRY_TIMES = 5;
    private int ZK_RETRY_SLEEP = 1000;

    private CuratorFramework newCurator(Properties conf) {
        if (conf.getProperty(ZkConfig.zookeeper_address) == null ||
                conf.getProperty(ZkConfig.zookeeper_address).isEmpty()) {
            logger.error("zk hosts is empty......");
            return null;
        }
        return CuratorFrameworkFactory.newClient(
                conf.getProperty(ZkConfig.zookeeper_address),
                Integer.parseInt(conf.getProperty(ZkConfig.zookeeper_session_timeout, ZK_SESSION_TIMEOUT + "")),
                Integer.parseInt(conf.getProperty(ZkConfig.zookeeper_connect_timeout, ZK_SESSION_TIMEOUT + "")),
                new RetryNTimes(Integer.parseInt(conf.getProperty(ZkConfig.zookeeper_retry_times, ZK_RETRY_TIMES + "")),
                        Integer.parseInt(conf.getProperty(ZkConfig.zookeeper_retry_sleep, ZK_RETRY_SLEEP + ""))));
    }

    private CuratorFramework newCurator(String connectionStr) {
        return CuratorFrameworkFactory.newClient(
                connectionStr,
                ZK_SESSION_TIMEOUT,
                ZK_SESSION_TIMEOUT,
                new RetryNTimes(ZK_RETRY_TIMES, ZK_RETRY_SLEEP));
    }

    public CuratorFramework getCurator() {
        assert _curator != null;
        return _curator;
    }

    public ZkClient(Properties config) {
        _curator = newCurator(config);
        assert _curator != null;
        _curator.start();
        logger.info("Starting curator service");
    }

//    public ZkClient() {
//        this(Context.getInstance().getZkConnect());
//    }

    public ZkClient(String connectionStr) {
        _curator = newCurator(connectionStr);
        _curator.start();
        logger.info("Starting curator service");
    }


    @Override
    public void close() {
        _curator.close();
        _curator = null;
    }
}
