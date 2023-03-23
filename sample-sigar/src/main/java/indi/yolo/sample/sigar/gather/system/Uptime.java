package indi.yolo.sample.sigar.gather.system;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarNotImplementedException;
import org.hyperic.sigar.util.PrintfFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Uptime {

    private static Object[] loadAvg = new Object[3];
    private static PrintfFormat formatter = new PrintfFormat("%.2f, %.2f, %.2f");

    public static String getInfo(Sigar sigar) throws SigarException {
        int whosNum = sigar.getWhoList().length;
        double uptime = sigar.getUptime().getUptime();

        String loadAverage;
        try {
            double[] avg = sigar.getLoadAverage();
            loadAvg[0] = avg[0];
            loadAvg[1] = avg[1];
            loadAvg[2] = avg[2];
            loadAverage = "load average: " + formatter.sprintf(loadAvg);
        } catch (SigarNotImplementedException var5) {
            loadAverage = "(load average unknown)";
        }

        return getCurrentTime() + " up " + formatUptime(uptime) + ", " + whosNum + " user, "
                + loadAverage;
    }

    private static String formatUptime(double uptime) {
        String retval = "";
        int days = (int) uptime / 86400;
        if (days != 0) {
            retval = retval + days + " " + (days > 1 ? "days" : "day") + ", ";
        }

        int minutes = (int) uptime / 60;
        int hours = minutes / 60;
        hours %= 24;
        minutes %= 60;
        if (hours != 0) {
            retval = retval + hours + ":" + minutes;
        } else {
            retval = retval + minutes + " min";
        }
        return retval;
    }

    private static String getCurrentTime() {
        return (new SimpleDateFormat("HH:mm:ss")).format(new Date());
    }

}
