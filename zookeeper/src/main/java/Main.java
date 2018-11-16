import server.ServiceFollowerSync;
import server.ServiceLeaderSync;
import server.fs.LocalFSInit;
import server.fs.LocalFSMonitor;
import server.fs.listener.DatFileL;
import server.fs.listener.JobFileL;
import server.fs.vistor.DatFileV;
import server.fs.vistor.JobFileV;
import server.node.RegisterNode;
import zk.ZkCache;
import zk.ZkClient;
import zk.ZkConfig;
import zk.ZkUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.curator.utils.CloseableUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 */
public class Main implements Runnable {

    private final static Logger logger = Logger.getLogger(Main.class);
    private boolean error = false;
    private static final String folder_separator = File.separator;

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
         /*
         *UI同步的是已部署的服务
         */
        final String breakPointDir = "data";
        final String pmJobsDir = "conf" + File.separator + "jobs";
        final String uiJobsDir = "tomcat" + File.separator + "conf" + File.separator + "etl";
        String folder_data_dir = System.getProperty("config_dir", "/etc/unimas/etl/");
        if (!folder_data_dir.endsWith(folder_separator)) {
            folder_data_dir += folder_separator;
        }
        try {
            logger.info("loading file to initialize environment......");
            Properties properties = new Properties();
            properties.load(Files.newInputStream(Paths.get(folder_data_dir,
                    "conf",
                    "server.properties")));
            Context context = Context.getInstance();
            context.init(folder_data_dir,
                    properties.getProperty(ZkConfig.node_id, "0"),
                    properties.getProperty(ZkConfig.listener),
                    properties.getProperty(ZkConfig.UiPort, 32010 + ""));
            context.setBreakPointDir(breakPointDir);
            context.setPmJobsDir(pmJobsDir);
            context.setUiJobsDir(uiJobsDir);
//            context.setZkRetryTimes(Integer.parseInt(properties.getOrDefault(ZkConfig.zookeeper_retry_times, 0)));
            context.setLeaderSyncTime(Integer.parseInt(
                    properties.getProperty(ZkConfig.service_leader_sync_time, 60 * 1000 + "")));
            context.setFollowerSyncTime(Integer.parseInt(
                    properties.getProperty(ZkConfig.service_follower_sync_time, 5 * 60 * 1000 + "")));

            logger.info("initialize zookeeper connection......");
            ZkClient zkClient = new ZkClient(properties);
            context.setZkClient(zkClient);
            ZkUtils.initZkRoot(zkClient);

            logger.info("register node " + context.getLocalNodeId());
            RegisterNode registerNode = new RegisterNode(zkClient);
            registerNode.register();
//            registerNode.addLostListener();


            logger.info("start inside socket server......");
//            TransServer transServer = new TransServer(context.getLocalPort() + "");
//            transServer.start();

            logger.info("start cache......");
            ZkCache zkCache = ZkCache.getInstance();
            zkCache.start(zkClient);

            logger.info("mapping local jobs file and dat file......");
            Map<String, FileVisitor<Path>> vm = new HashMap<>();
            vm.put(context.getPmJobsDir(), new JobFileV());
            vm.put(context.getBreakPointDir(), new DatFileV());
            LocalFSInit localFSInit = new LocalFSInit(vm);
            localFSInit.start();

            logger.info("start local files listener......");
            Map<String, FileAlterationListener> lm = new HashMap<>();
            lm.put(context.getPmJobsDir(), new JobFileL());
            lm.put(context.getBreakPointDir(), new DatFileL());
            LocalFSMonitor localFSMonitor = new LocalFSMonitor(lm);
            new Thread(localFSMonitor).start();

            logger.info("start service leader sync......");
            ServiceLeaderSync serviceLeaderSync = new ServiceLeaderSync();
            new Thread(serviceLeaderSync).start();

            logger.info("start service replica sync......");
            ServiceFollowerSync serviceFollowerSync = new ServiceFollowerSync();
            new Thread(serviceFollowerSync).start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                localFSMonitor.close();
                serviceLeaderSync.stop();
                serviceFollowerSync.stop();
//                CloseableUtils.closeQuietly(transServer);
                CloseableUtils.closeQuietly(zkCache);
                CloseableUtils.closeQuietly(zkClient);
            }));
            while (!error) {
                logger.debug(" all alive nodes " + Arrays.toString(zkCache.getAllNodesId().toArray()));
                if (!zkCache.getAllNodesId().contains(context.getLocalNodeId())) {
                    logger.info(" register node " + context.getLocalNodeId());
                    registerNode.register();
                }
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    logger.error(e); //ignore
                }
            }
        } catch (Exception e) {
            logger.error(e);
            error = true;
            System.exit(-1);
        }
    }
}
