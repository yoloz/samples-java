package indi.yolo.sample.sigar.gather.system;

import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class DiskAndNetFlow {

    private Sigar sigar;

    public DiskAndNetFlow(Sigar sigar) {
        this.sigar = sigar;
    }

    private void diskUsage() throws SigarException {
        FileSystem[] fsList = sigar.getFileSystemList();
        for (FileSystem fs : fsList) {
            // 分区的盘符名称
            System.out.println("fs.getDevName() = " + fs.getDevName());
            // 分区的盘符名称
            System.out.println("fs.getDirName() = " + fs.getDirName());
            System.out.println("fs.getFlags() = " + fs.getFlags());//
            // 文件系统类型，比如 FAT32、NTFS
            System.out.println("fs.getSysTypeName() = " + fs.getSysTypeName());
            // 文件系统类型名，比如本地硬盘、光驱、网络文件系统等
            System.out.println("fs.getTypeName() = " + fs.getTypeName());
            // 文件系统类型
            System.out.println("fs.getType() = " + fs.getType());
            FileSystemUsage usage = null;
            try {
                usage = sigar.getFileSystemUsage(fs.getDirName());
            } catch (SigarException e) {
                if (fs.getType() == 2)
                    throw e;
                continue;
            }
            switch (fs.getType()) {
                case FileSystem.TYPE_LOCAL_DISK:
                    // 文件系统总大小
                    System.out.println(" Total = " + usage.getTotal() + "KB");
                    // 文件系统剩余大小
                    System.out.println(" Free = " + usage.getFree() + "KB");
                    // 文件系统可用大小
                    System.out.println(" Avail = " + usage.getAvail() + "KB");
                    // 文件系统已经使用量
                    System.out.println(" Used = " + usage.getUsed() + "KB");
                    double usePercent = usage.getUsePercent() * 100D;
                    // 文件系统资源的利用率
                    System.out.println(" Usage = " + usePercent + "%");
                    break;
                default:
                    break;
            }
            System.out.println(" DiskReads = " + usage.getDiskReadBytes());
            System.out.println(" DiskWrites = " + usage.getDiskWriteBytes());
        }
    }

    private void netFlow() throws SigarException {
        String ifNames[] = sigar.getNetInterfaceList();
        for (String name : ifNames) {
            NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
            System.out.println("\nname = " + name);// 网络设备名
            System.out.println("Address = " + ifconfig.getAddress());// IP地址
            System.out.println("Netmask = " + ifconfig.getNetmask());// 子网掩码
            if ((ifconfig.getFlags() & 1L) <= 0L) {
                System.out.println("!IFF_UP...skipping getNetInterfaceStat");
                continue;
            }
            try {
                NetInterfaceStat ifstat = sigar.getNetInterfaceStat(name);
                System.out.println("RxPackets = " + ifstat.getRxPackets());// 接收的总包裹数
                System.out.println("TxPackets = " + ifstat.getTxPackets());// 发送的总包裹数
                System.out.println("RxBytes = " + ifstat.getRxBytes());// 接收到的总字节数
                System.out.println("TxBytes = " + ifstat.getTxBytes());// 发送的总字节数
                System.out.println("RxErrors = " + ifstat.getRxErrors());// 接收到的错误包数
                System.out.println("TxErrors = " + ifstat.getTxErrors());// 发送数据包时的错误数
                System.out.println("RxDropped = " + ifstat.getRxDropped());// 接收时丢弃的包数
                System.out.println("TxDropped = " + ifstat.getTxDropped());// 发送时丢弃的包数
            } catch (SigarException e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
