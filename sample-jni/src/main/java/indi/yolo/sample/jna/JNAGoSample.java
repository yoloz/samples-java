package indi.yolo.sample.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * @author yoloz
 */
public class JNAGoSample {

    public interface CLibrary extends Library {

        CLibrary INSTANCE = Native.load("C:\\java\\libtest.dll", CLibrary.class);

        String CHostInfo();

        String CMemInfo();

        String CCpuInfo();

        String CCpuStat();

        String CNCList();

        String CProcessList();

        String CProcessInfo(int pid);
    }

    public static void main(String[] args) {
        System.setProperty("jna.encoding", "UTF-8");
        CLibrary cLibrary = CLibrary.INSTANCE;
        System.out.println("hostInfo: " + cLibrary.CHostInfo());
        System.out.println("memInfo: " + cLibrary.CMemInfo());
        System.out.println("cpuInfo: " + cLibrary.CCpuInfo());
        System.out.println("cpuStat: " + cLibrary.CCpuStat());
        System.out.println("NCList: " + cLibrary.CNCList());
        System.out.println("processList: " + cLibrary.CProcessList());
        System.out.println("processInfo: " + cLibrary.CProcessInfo(3244));
    }

}
