package indi.yolo.sample.sigar.gather.system;

import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SysMonitor {

    private Logger logger = LoggerFactory.getLogger(SysMonitor.class);

    private final int period = 5; //seconds

    private ScheduledExecutorService excutor;
    private ScheduledFuture service;

    private Sigar sigar;
    private String devKafka = "";
    private String bindIP; //系统绑定的IP地址
    private String netName = ""; //bindIP对应的网卡

    private static DecimalFormat df;

    private static DiskInfo diskInfo;
    private static NetInfo netInfo;

    private static long disk_r = 0L;
    private static long disk_w = 0L;
    private static long net_i = 0L;
    private static long net_o = 0L;


    public SysMonitor(String bindIP,Sigar sigar) throws Exception {
        this.sigar = sigar;
        this.bindIP = bindIP;
        df = new DecimalFormat("#.00");
        diskInfo = new DiskInfo();
        netInfo = new NetInfo();
        init();
    }

    public void start() {
        excutor = Executors.newScheduledThreadPool(1);
        service = excutor.scheduleAtFixedRate(() -> {
            DiskInfo oldDisk = copy(diskInfo);
            NetInfo oldNet = copy(netInfo);
            diskUsage();
            netFlow();
            if (oldDisk.readBytes != 0 || oldDisk.writeBytes != 0) {
                disk_r = diskInfo.readBytes - oldDisk.readBytes;
                disk_w = diskInfo.writeBytes - oldDisk.writeBytes;
            }
            if (oldNet.rxBytes != 0 || oldNet.txBytes != 0) {
                net_i = netInfo.rxBytes - oldNet.rxBytes;
                net_o = netInfo.txBytes - oldNet.txBytes;
            }
        }, 1, period, TimeUnit.SECONDS);
    }

    private void init() throws Exception {
        Path pf = Paths.get(System.getProperty("cii.root.dir"), "bin", "params");
        String kafkaDir = "";
        try (InputStream in = Files.newInputStream(pf);
             InputStreamReader ir = new InputStreamReader(in, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(ir)) {
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.isEmpty()) continue;
                if (index == 2) {
                    kafkaDir = line;
                    break;
                } else index++;
            }
        } catch (IOException e) {
            throw new Exception("读取文件[" + pf + "]出错", e);
        }
        if (!kafkaDir.isEmpty()) {
            String[] cmds = {"/bin/sh", "-c", "df " + kafkaDir};
            try {
                Process p = Runtime.getRuntime().exec(cmds);
                try (InputStream in = p.getInputStream();
                     InputStreamReader ir = new InputStreamReader(in);
                     BufferedReader reader = new BufferedReader(ir)) {
                    reader.readLine();
                    String line = reader.readLine();
                    if (line != null) devKafka = line.split("\\s+")[0];
                }
            } catch (IOException e) {
                throw new Exception("exec " + Arrays.toString(cmds) + " error ", e);
            }
        }
        String[] ifNames = sigar.getNetInterfaceList();
        for (String name : ifNames) {
            NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
            if (bindIP.equals(ifconfig.getAddress())) {
                netName = name;
                break;
            }
        }
    }

    private void diskUsage() {
        if (!devKafka.isEmpty()) {
            try {
                FileSystem[] fsList = sigar.getFileSystemList();
                for (FileSystem fs : fsList) {
                    if (devKafka.equals(fs.getDevName())) {
                        FileSystemUsage fu = sigar.getFileSystemUsage(fs.getDirName());
                        diskInfo.setTotal(fu.getTotal());
                        diskInfo.setFree(fu.getFree());
                        diskInfo.setAvail(fu.getAvail());
                        diskInfo.setUsed(fu.getUsed());
                        diskInfo.setUsage(fu.getUsePercent());
                        diskInfo.setReadBytes(fu.getDiskReadBytes());
                        diskInfo.setWriteBytes(fu.getDiskWriteBytes());
                    }
                }
            } catch (SigarException e) {
                logger.error(e.getMessage(), e);
            }
        } else logger.warn("磁盘盘符为空......");
    }

    private void netFlow() {
        if (!netName.isEmpty()) {
            try {
                NetInterfaceStat ifstat = sigar.getNetInterfaceStat(netName);
                netInfo.setRxPAll(ifstat.getRxPackets());
                netInfo.setTxPAll(ifstat.getTxPackets());
                netInfo.setRxBytes(ifstat.getRxBytes());
                netInfo.setTxBytes(ifstat.getTxBytes());
                netInfo.setRxPErrors(ifstat.getRxErrors());
                netInfo.setTxPErrors(ifstat.getTxErrors());
                netInfo.setRxPDropped(ifstat.getRxDropped());
                netInfo.setTxPDropped(ifstat.getTxDropped());
            } catch (SigarException e) {
                logger.error(e.getMessage(), e);
            }
        } else logger.warn("网卡设备名为空......");
    }

    private DiskInfo copy(DiskInfo diskInfo) {
        DiskInfo info = new DiskInfo();
        info.setWriteBytes(diskInfo.writeBytes);
        info.setReadBytes(diskInfo.readBytes);
        info.setUsage(diskInfo.usage);
        info.setUsed(diskInfo.used);
        info.setAvail(diskInfo.avail);
        info.setFree(diskInfo.free);
        info.setTotal(diskInfo.total);
        return info;
    }

    private NetInfo copy(NetInfo netInfo) {
        NetInfo info = new NetInfo();
        info.setTxPDropped(netInfo.txPDropped);
        info.setRxPDropped(netInfo.rxPDropped);
        info.setTxPErrors(netInfo.txPErrors);
        info.setRxPErrors(netInfo.rxPErrors);
        info.setTxBytes(netInfo.txBytes);
        info.setRxBytes(netInfo.rxBytes);
        info.setTxPAll(netInfo.txPAll);
        info.setRxPAll(netInfo.rxPAll);
        return info;
    }

    public void close() {
        if (service != null) service.cancel(true);
        if (excutor != null) {
            excutor.shutdown();
            try {
                excutor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {//ignore
            }
            excutor.shutdownNow();
        }
    }

    public static DiskInfo getDiskInfo() {
        return diskInfo;
    }

    public static NetInfo getNetInfo() {
        return netInfo;
    }

    public static String getDisk_r() {
//        if (disk_r == 0L) return "计算中...";
        return formatSize(disk_r);
    }

    public static String getDisk_w() {
//        if (disk_w == 0L) return "计算中...";
        return formatSize(disk_w);
    }

    public static String getNet_i() {
//        if (net_i == 0L) return "计算中...";
        return formatSize(net_i);
    }

    public static String getNet_o() {
//        if (net_o == 0L) return "计算中...";
        return formatSize(net_o);
    }

    private static String formatSize(long size) {
        String rs;
        if (size < 1024) {
            rs = df.format((double) size) + "B";
        } else if (size < 1048576) {
            rs = df.format((double) size / 1024) + "K";
        } else if (size < 1073741824) {
            rs = df.format((double) size / 1048576) + "M";
        } else {
            rs = df.format((double) size / 1073741824) + "G";
        }
        if (".00B".equals(rs)) rs = "0B";
        return rs;
    }

    class DiskInfo {

        private long total = 0L;
        private long free = 0L;
        private long avail = 0L;
        private long used = 0L;
        private double usage = 0D;
        private long readBytes = 0L;
        private long writeBytes = 0L;

        private String toG(long size) {
            if (size == 0) return "0B";
            return df.format((double) size / 1048576) + "G";
        }

        void setTotal(long total) {
            this.total = total < 0 ? 0 : total;
        }

        void setFree(long free) {
            this.free = free < 0 ? 0 : free;
        }

        void setAvail(long avail) {
            this.avail = avail < 0 ? 0 : avail;
        }

        void setUsed(long used) {
            this.used = used < 0 ? 0 : used;
        }

        void setUsage(double usage) {
            this.usage = usage < 0 ? 0 : usage;
        }

        void setReadBytes(long readBytes) {
            this.readBytes = readBytes < 0 ? 0 : readBytes;
        }

        void setWriteBytes(long writeBytes) {
            this.writeBytes = writeBytes < 0 ? 0 : writeBytes;
        }

        public String toString() {
            return "{\"total\":\"" + toG(total) + "\",\"free\":\"" + toG(free)
                    + "\",\"avail\":\"" + toG(avail) + "\",\"used\":\"" + toG(used)
                    + "\",\"usage\":\"" + (usage * 100D + "%") + "\",\"read\":\"" + formatSize(readBytes)
                    + "\",\"write\":\"" + formatSize(writeBytes) + "\"}";
        }
    }

    class NetInfo {
        private long rxPAll = 0L;
        private long txPAll = 0L;
        private long rxPErrors = 0L;
        private long txPErrors = 0L;
        private long rxPDropped = 0L;
        private long txPDropped = 0L;
        private long rxBytes = 0L;
        private long txBytes = 0L;

        void setRxPAll(long rxPAll) {
            this.rxPAll = rxPAll < 0 ? 0 : rxPAll;
        }

        void setTxPAll(long txPAll) {
            this.txPAll = txPAll < 0 ? 0 : txPAll;
        }

        void setRxPErrors(long rxPErrors) {
            this.rxPErrors = rxPErrors < 0 ? 0 : rxPErrors;
        }

        void setTxPErrors(long txPErrors) {
            this.txPErrors = txPErrors < 0 ? 0 : txPErrors;
        }

        void setRxPDropped(long rxPDropped) {
            this.rxPDropped = rxPDropped < 0 ? 0 : rxPDropped;
        }

        void setTxPDropped(long txPDropped) {
            this.txPDropped = txPDropped < 0 ? 0 : txPDropped;
        }

        void setRxBytes(long rxBytes) {
            this.rxBytes = rxBytes < 0 ? 0 : rxBytes;
        }

        void setTxBytes(long txBytes) {
            this.txBytes = txBytes < 0 ? 0 : txBytes;
        }

        public String toString() {
            return "{\"rxpa\":" + rxPAll + ",\"txpa\":" + txPAll + ",\"rxpe\":" + rxPErrors
                    + ",\"txpe\":" + txPErrors + ",\"rxpd\":" + rxPDropped + ",\"txpd\":"
                    + txPDropped + ",\"in\":\"" + formatSize(rxBytes) + "\",\"out\":\""
                    + formatSize(txBytes) + "\"}";
        }
    }
}
