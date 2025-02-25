package indi.yolo.sample.netty.tcpforward;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author yolo
 */
public class OutputBytes {

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");

    public static void output(String srcAddress, int srcPort, String tarAddress, int tarPort, StringBuilder content) {
        String timestamp = TIME_FORMAT.format(new Date());
        // 输出类似tcpdump格式的日志
        System.out.printf("input: %s %s.%d > %s.%d: %n", timestamp, srcAddress, srcPort, tarAddress, tarPort);
        int pos = 0;
        for (int i = 0; i < content.length(); i += 100) {
            System.out.printf("%s%n", content.substring(pos, i));
            pos = i;
        }
        System.out.printf("%n");
    }

}
