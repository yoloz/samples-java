import zk.ZkClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 */
public class Context {

    public final static Charset charset = Charset.forName("UTF-8");

    //用户设置的数据文件路径
    private String uiPort;                    //ui交互端口
    private String customDir;                 //etc/unimas/etl
    private int nodeId;                       //配置的本台机器id
    private String listener;                  //配置的本台机器监听

    private ZkClient zk_Client;

    private String breakPointDir;
    private String pmJobsDir;
    private String uiJobsDir;
    //    private int zkRetryTimes;
    private int leaderSyncTime;
    private int followerSyncTime;

    private Context() {

    }

    private static volatile Context instance;

    public static Context getInstance() {
        if (instance == null) {
            synchronized (Context.class) {
                if (instance == null) {
                    instance = new Context();
                }
            }
        }
        return instance;
    }

    public void init(String customDir, String nodeId, String listener, String uiPort) {
        this.customDir = customDir;
        try {
            this.nodeId = Integer.parseInt(nodeId);
        } catch (Exception e) {
            this.nodeId = 0;
        }
        this.listener = listener;
        this.uiPort = uiPort;
    }


    public Path getCustomDir() {
        return Paths.get(customDir);
    }

    public Integer getLocalNodeId() {
        return nodeId;
    }

    public String getLocalHost() {
        if (listener != null && !listener.isEmpty()) {
            try {
                if (listener.contains(":")) return listener.substring(0, listener.indexOf(":"));
                else return InetAddress.getLocalHost().getCanonicalHostName();
            } catch (Exception e) {
                return "127.0.0.1";
            }
        } else {
            try {
                return InetAddress.getLocalHost().getCanonicalHostName();
            } catch (UnknownHostException e) {
                return "127.0.0.1";
            }
        }
    }

    public int getLocalPort() {
        if (listener != null && !listener.isEmpty()) {
            try {
                if (listener.contains(":")) {
                    return Integer.parseInt(listener.substring(listener.indexOf(":") + 1));
                } else return Integer.parseInt(listener);
            } catch (NumberFormatException e) {
                return 32321;
            }
        } else {
            return 32321;
        }
    }

    public String getUiPort() {
        return uiPort;
    }

    public void setZkClient(ZkClient zkClient) {
        zk_Client = zkClient;
    }

    public ZkClient getZkClient() {
        assert zk_Client != null;
        return zk_Client;
    }

    String getBreakPointDir() {
//        return Paths.get(customDir, TransUtil.relativePath(null, breakPointDir)).toString();
        return null;
    }

    void setBreakPointDir(String breakPointDir) {
        this.breakPointDir = breakPointDir;
    }

    public String getPmJobsDir() {
//        return Paths.get(customDir, TransUtil.relativePath(null, pmJobsDir)).toString();
        return null;
    }

    void setPmJobsDir(String pmJobsDir) {
        this.pmJobsDir = pmJobsDir;
    }

    public String getUiJobsDir() {
//        return Paths.get(customDir, TransUtil.relativePath(null, uiJobsDir)).toString();
        return null;
    }

    void setUiJobsDir(String uiJobsDir) {
        this.uiJobsDir = uiJobsDir;
    }

//    public int getZkRetryTimes() {
//        return zkRetryTimes;
//    }
//
//    public void setZkRetryTimes(int zkRetryTimes) {
//        this.zkRetryTimes = zkRetryTimes;
//    }

    public long getLeaderSyncTime() {
        return leaderSyncTime;
    }

    public void setLeaderSyncTime(int leaderSyncTime) {
        this.leaderSyncTime = leaderSyncTime;
    }

    public long getFollowerSyncTime() {
        return followerSyncTime;
    }

    public void setFollowerSyncTime(int followerSyncTime) {
        this.followerSyncTime = followerSyncTime;
    }
}
