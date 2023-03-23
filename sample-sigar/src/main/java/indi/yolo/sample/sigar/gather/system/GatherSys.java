package indi.yolo.sample.sigar.gather.system;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import indi.yolo.sample.sigar.output.Output;
import org.hyperic.sigar.*;
import org.hyperic.sigar.cmd.Ps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GatherSys {

    private Logger logger = LoggerFactory.getLogger(GatherSys.class);

    /**
     * configuration definition
     */
    private enum CONFIG {
        INTERVAL("gather.system.interval.sec");
        private String value;

        CONFIG(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private final Gson gson = new Gson();
    private ScheduledExecutorService runExecutor;
    private ScheduledFuture runFuture;
    private ScheduledExecutorService sysExecutor;
    private ScheduledFuture sysFuture;

    private final int interval;
    private final Output output;


    public GatherSys(Properties config, Output output) {
        String _interval = config.getProperty(CONFIG.INTERVAL.getValue(), "5");
        this.interval = _interval.isEmpty() ? 5 : Integer.parseInt(_interval);
        this.output = output;
    }

    public void gather(Sigar sigar) {
        runExecutor = Executors.newScheduledThreadPool(1);
        runFuture = runExecutor.scheduleAtFixedRate(() -> {
            output.apply(null, runInfo(sigar).getBytes(Charset.forName("UTF-8")));
            output.apply(null, procList(sigar).getBytes(Charset.forName("UTF-8")));
        }, 2, interval, TimeUnit.SECONDS);
        sysExecutor = Executors.newScheduledThreadPool(1);
        sysFuture = sysExecutor.scheduleAtFixedRate(() -> {
            output.apply(null, netInfo(sigar).getBytes(Charset.forName("UTF-8")));
            output.apply(null, sysInfo().getBytes(Charset.forName("UTF-8")));
        }, 2, 12 * 3600, TimeUnit.SECONDS);
    }

    public void close() {
        if (runFuture != null) runFuture.cancel(true);
        if (runExecutor != null) {
            runExecutor.shutdown();
            try {
                runExecutor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {//ignore
            }
            runExecutor.shutdownNow();
        }
        if (sysFuture != null) sysFuture.cancel(true);
        if (sysExecutor != null) {
            sysExecutor.shutdown();
            try {
                sysExecutor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {//ignore
            }
            sysExecutor.shutdownNow();
        }
    }

    /**
     * 当前时间,系统运行时间,当前登录用户数,系统负载,分别为 1分钟、5分钟、15分钟前到现在的平均值
     * uptime: 11:49:52 up 2:34, 1 user, load average: 0.25, 0.39, 0.52
     * <p>
     * tasks: 204 total, 1 running, 203 sleeping, 0 stopped, 0 zombie
     * <p>
     * cpu: 16.3 us, 1.8 sy, 0.1 ni, 80.3 id, 1.6 wa, 0.0 hi, 0.0 si, 0.0 st
     * <p>
     * mem :  6001336 total, 273224 free, 3224728 used, 2503384 buff/cache
     *
     * @return json string {"runInfo":{"uptime":"","tasks":"","cpu":"","mem":""}}
     */
    private String runInfo(Sigar sigar) {
        Map<String, String> info = new HashMap<>(4);
        try {
            String ut = Uptime.getInfo(sigar);
            info.put("uptime", ut);
        } catch (SigarException e) {
            logger.error(e.getMessage(), e);
            info.put("uptime", e.getMessage());
        }
        try {
            ProcStat stat = sigar.getProcStat();
            String tasks = String.valueOf(stat.getTotal()) +
                    " total, " + stat.getRunning() + " running, " +
                    stat.getSleeping() + " sleeping, " + stat.getStopped() +
                    " stopped, " + stat.getZombie() + " zombie";
            info.put("tasks", tasks);
        } catch (SigarException e) {
            logger.error(e.getMessage(), e);
            info.put("tasks", e.getMessage());
        }
        try {
            CpuPerc cp = sigar.getCpuPerc();
            String cu = CpuPerc.format(cp.getUser()) + " us, " + CpuPerc.format(cp.getSys()) + " sy, "
                    + CpuPerc.format(cp.getNice()) + " ni, " + CpuPerc.format(cp.getIdle()) + " id, "
                    + CpuPerc.format(cp.getWait()) + " wa, " + CpuPerc.format(cp.getIrq()) + " hi";
            if (SigarLoader.IS_LINUX) {
                cu += ", " + CpuPerc.format(cp.getSoftIrq()) + " si, " + CpuPerc.format(cp.getStolen())
                        + " st";
            }
            info.put("cpu", cu);
        } catch (SigarException e) {
            logger.error(e.getMessage(), e);
            info.put("cpu", e.getMessage());
        }
        try {
            Mem mem = sigar.getMem();
            String mm = mem.getTotal() + " total, " + mem.getFree() + " free, " + mem.getActualUsed()
                    + " used, " + (mem.getUsed() - mem.getActualUsed()) + " buff/cache";
            info.put("mem", mm);
        } catch (SigarException e) {
            logger.error(e.getMessage(), e);
            info.put("mem", e.getMessage());
        }
        Map<String, Map<String, String>> map = new HashMap<>(1);
        map.put("runInfo", info);
        return gson.toJson(map, new TypeToken<Map<String, Map<String, String>>>() {
        }.getType());
    }

    /**
     * PID\tUSER\tSTIME\tSIZE\tRSS\tSHARE\tSTATE\tTIME\tCPU\tCOMMAND
     *
     * @return json string {"processes":["",""]}
     */
    @SuppressWarnings("unchecked")
    private String procList(Sigar sigar) {
        List<String> processes = new ArrayList<>();
        try {
            long[] pids = sigar.getProcList();
            for (long pid : pids) {
                List<String> info = Ps.getInfo(sigar, pid);
                ProcCpu cpu = sigar.getProcCpu(pid);
                String cpuPerc = CpuPerc.format(cpu.getPercent());
                info.add(info.size() - 1, cpuPerc);
                StringBuilder buf = new StringBuilder();
                info.forEach(s -> buf.append(s).append("\t"));
                processes.add(buf.substring(0, buf.length() - 1));
            }
        } catch (SigarException e) {
            logger.error(e.getMessage(), e);
            processes.add(e.getMessage());
        }
        Map<String, List<String>> info = new HashMap<>(1);
        info.put("processes", processes);
        return gson.toJson(info, new TypeToken<Map<String, List<String>>>() {
        }.getType());
    }

    /**
     * {"interface":"","ip":"","mac":"","netmask":""}
     *
     * @return json string {"net":[{},{}]}
     */
    private String netInfo(Sigar sigar) {
        Map<String, List<Map<String, String>>> info = new HashMap<>();
        try {
            String[] interfaces = sigar.getNetInterfaceList();
            List<Map<String, String>> list = new ArrayList<>(interfaces.length);
            for (String name : interfaces) {
                try {
                    NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
                    long flags = ifconfig.getFlags();
                    if ((flags & 1L) > 0L && (flags & 16L) <= 0L && (flags & 8L) <= 0L) {
                        Map<String, String> map = new HashMap<>();
                        map.put("interface", ifconfig.getName());
                        map.put("ip", ifconfig.getAddress());
                        map.put("mac", ifconfig.getHwaddr());
                        map.put("netmask", ifconfig.getNetmask());
                        list.add(map);
                    }
                } catch (SigarException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            info.put("net", list);
        } catch (SigarException e) {
            logger.error(e.getMessage(), e);
        }
        return gson.toJson(info, new TypeToken<Map<String, List<Map<String, String>>>>() {
        }.getType());
    }

    /**
     * {"arch":"","endian":"","data_model":"","desc":"","vendor":"","vendor_code":"",
     * "vendor_name":"","vendor_version":"","version":""}
     *
     * @return json string {"sys":{}}
     */
    private String sysInfo() {
        Map<String, String> map = new HashMap<>(9);
        OperatingSystem os = OperatingSystem.getInstance();
        map.put("arch", os.getArch());
        map.put("endian", os.getCpuEndian());
        map.put("data_model", os.getDataModel());
        map.put("desc", os.getDescription());
        map.put("vendor", os.getVendor());
        map.put("vendor_code", os.getVendorCodeName());
        map.put("vendor_name", os.getVendorName());
        map.put("vendor_version", os.getVendorVersion());
        map.put("version", os.getVersion());
        Map<String, Map<String, String>> info = new HashMap<>(1);
        info.put("sys", map);
        return gson.toJson(info, new TypeToken<Map<String, Map<String, String>>>() {
        }.getType());
    }

//    /**
//      {"vendor":"","model":"","mhz":"","total_cores":"","physical_cpu":"","core_per_cpu":""}
//
//      @return json string {"cpu":[{},{}]}
//     */
   /* private String physicalCpu() {
        Map<String, List<Map<String, String>>> info = new HashMap<>();
        try {
            CpuInfo[] cis = sigar.getCpuInfoList();
            List<Map<String, String>> list = new ArrayList<>(cis.length);
            for (CpuInfo ci : cis) {
                Map<String, String> map = new HashMap<>();
                long cacheSize = ci.getCacheSize();
                map.put("vendor", ci.getVendor());
                map.put("model", ci.getModel());
                map.put("mhz", String.valueOf(ci.getMhz()));
                map.put("total_cores", String.valueOf(ci.getTotalCores()));
                map.put("physical_cpu", String.valueOf(ci.getTotalSockets()));
                map.put("core_per_cpu", String.valueOf(ci.getCoresPerSocket()));
                if (cacheSize != Sigar.FIELD_NOTIMPL) map.put("cache", String.valueOf(cacheSize));
                list.add(map);
            }
            info.put("cpu", list);
        } catch (SigarException e) {
            logger.error(e.getMessage(), e);
        }
        return gson.toJson(info, new TypeToken<Map<String, List<Map<String, String>>>>() {
        }.getType());
    } */


}
