package indi.yolo.sample.sigar.gather.system;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SigarMain {

    public static void main(String[] args) {
        System.setProperty("java.library.path", new File("").getAbsolutePath() +
                "/java/functions/gatherData/lib/sigar");
        // Sigar信息集合
        List<SigarInfoEntity> sigarInfos = new ArrayList<>();
//        try {
            // 1.获取系统信息和jvm虚拟机信息
//            sigarInfos.addAll(SigarUtils.getJvmInfos());
            // 2.获取cpu信息
//            sigarInfos.addAll(SigarUtils.getCpuInfos());
            // 3.获取内存信息
//            sigarInfos.addAll(SigarUtils.getMemoryInfos());
            // 4.获取操作系统信息
            sigarInfos.addAll(SigarUtils.getOsInfos());
            // 5.获取文件信息
//            sigarInfos.addAll(SigarUtils.getFileInfos());
            // 6.获取网络信息
//            sigarInfos.addAll(SigarUtils.getNetInfos());

            StringBuilder sigarStringBuffer = new StringBuilder();
            for (SigarInfoEntity sigarInfo : sigarInfos) {
                sigarStringBuffer.append(sigarInfo.getName()).append(":").append(sigarInfo.getValue()).append("\r\n");
            }

            System.out.println(sigarStringBuffer.toString());


//        } catch (SigarException | UnknownHostException se) {
//            // TODO: handle exception
//            se.printStackTrace();
//        }

    }
}
