import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;

/**
 */
public class Syslog4jDemo {

    public static void main(String[] args) {
        SyslogIF syslog = Syslog.getInstance("udp");
        syslog.getConfig().setHost("localhost");
        syslog.getConfig().setPort(8088);
        while (true) {
            syslog.info("syslog4jDemo client");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
