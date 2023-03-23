package indi.yolo.sample.zk;

import kamon.sigar.SigarProvisioner;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.zookeeper.CreateMode;
import org.hyperic.sigar.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.concurrent.*;

public class NodeImpl {

    private final Logger logger = LoggerFactory.getLogger(NodeImpl.class);

    private final String nodePath;
    private final String ip; //ipv4
    private final int port;
    private final Sigar sigar;
    private final long pid;
    private final DecimalFormat decimalFormat;

    public NodeImpl(String zkPath, String ip, int port) throws Exception {
        if (zkPath.lastIndexOf('/') > 0) this.nodePath = zkPath + ip(ip);
        else this.nodePath = zkPath + "/" + ip(ip);
        this.ip = ip;
        this.port = port;
        SigarProvisioner.provision();
        this.sigar = new Sigar();
        this.pid = sigar.getPid();
        this.decimalFormat = new DecimalFormat("#.0");
    }

    private long ip(String ip) {
        if (ip == null || ip.isEmpty()) return 0;
        String[] s = ip.split("\\.");
        if (s.length != 4) return 0;
        return (Long.parseLong(s[0]) << 24) + (Long.parseLong(s[1]) << 16) +
                (Long.parseLong(s[2]) << 8) + Long.parseLong(s[3]);
    }

    private long cpu_total = 0L;
    private long cpu_idle = 0L;
    private double mem_rate = 0D;
    private ScheduledExecutorService sysScheduler;

    private void refreshSys() {
        try {
            cpu_total = sigar.getCpu().getTotal() - cpu_total;
            cpu_idle = sigar.getCpu().getIdle() - cpu_idle;
            mem_rate = sigar.getMem().getFreePercent();
        } catch (Exception e) {
            logger.error("gather sys info fail,", e);
        }

    }

    String getNodePath() {
        return nodePath;
    }

    @Override
    public String toString() {
        float cpu_rate = 1f;
        if (cpu_total > 0) cpu_rate = (float) cpu_idle / cpu_total;
        return "{\"ip\":\"" + ip + "\",\"port\":" + port + ",\"cpu_rate\":" +
                decimalFormat.format((1 - cpu_rate) * 100) +
                ",\"mem_rate\":" + decimalFormat.format(mem_rate) + ",\"pid\":" + pid + "}";
    }

    private void createNode() throws Exception {
        ZKClient.getInstance().create(CreateMode.EPHEMERAL, nodePath, toString());
    }

    public void register() throws Exception {
        ZKClient.getInstance().getCurator().getConnectionStateListenable().addListener((client, state) -> {
            if (state == ConnectionState.LOST) {
                while (true) {
                    try {
                        logger.warn("retry node[" + ip + ":" + port + "]");
                        if (ZKClient.getInstance().getCurator().getZookeeperClient().blockUntilConnectedOrTimedOut()) {
                            createNode();
                            break;
                        }
                    } catch (Exception e) {
                        logger.error("retry node[" + ip + ":" + port + "] fail,", e);
                    }
                }
            }
        });
        createNode();
        sysScheduler = Executors.newScheduledThreadPool(1);
        sysScheduler.scheduleAtFixedRate(() -> {
            refreshSys();
            try {
                ZKClient.getInstance().update(nodePath, toString());
            } catch (Exception e) {
                logger.error("update node[" + ip + ":" + port + "] error,", e);
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    public void close() {
        if (sysScheduler != null) sysScheduler.shutdownNow();
        if (sigar != null) sigar.close();
    }

    void getLocalIP() throws SigarException {
        for (String iface : sigar.getNetInterfaceList()) {
            NetInterfaceConfig cfg = sigar.getNetInterfaceConfig(iface);
            String address = cfg.getAddress();
            if (NetFlags.LOOPBACK_ADDRESS.equals(address) ||
                    NetFlags.LOOPBACK_ADDRESS_V6.equals(address) ||
                    NetFlags.LOOPBACK_HOSTNAME.equals(address) ||
                    NetFlags.ANY_ADDR.equals(address) ||
                    NetFlags.ANY_ADDR_V6.equals(address) ||
                    NetFlags.NULL_HWADDR.equals(cfg.getHwaddr()) ||
                    (cfg.getFlags() & NetFlags.IFF_LOOPBACK) != 0
            ) continue;
            System.out.println(cfg.getName() + "IP地址:" + cfg.getAddress());
            System.out.println(cfg.getName() + "网关广播地址:" + cfg.getBroadcast());
            System.out.println(cfg.getName() + "网卡MAC地址:" + cfg.getHwaddr());
            System.out.println(cfg.getName() + "子网掩码:" + cfg.getNetmask());
            System.out.println(cfg.getName() + "网卡描述信息:" + cfg.getDescription());
            System.out.println(cfg.getName() + "网卡类型" + cfg.getType());
        }
    }

}
