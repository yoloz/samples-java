import com.cloudbees.syslog.Facility;
import com.cloudbees.syslog.MessageFormat;
import com.cloudbees.syslog.Severity;
import com.cloudbees.syslog.sender.UdpSyslogMessageSender;

import java.io.IOException;

/**
 *
 */
public class CloudbeesDemo {
    public static void main(String[] args) throws IOException {
        // Initialise sender
        UdpSyslogMessageSender messageSender = new UdpSyslogMessageSender();
        messageSender.setDefaultAppName("test");
        messageSender.setDefaultFacility(Facility.USER);
        messageSender.setDefaultSeverity(Severity.ALERT);
        messageSender.setSyslogServerHostname("10.68.23.14");
        messageSender.setSyslogServerPort(8088);
//        messageSender.setMessageFormat(MessageFormat.RFC_5424); // optional, default is RFC 3164
        // send a Syslog message
        while (true) {
            messageSender.sendMessage("This is a test message");
            try {
                Thread.sleep(60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
