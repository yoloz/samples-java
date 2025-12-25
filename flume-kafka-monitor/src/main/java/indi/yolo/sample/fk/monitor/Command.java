package indi.yolo.sample.fk.monitor;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * Created on 17-2-20.
 */
public class Command {
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String lineSeparator = System.getProperty("line.separator", "\n");

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("Missing configuration parameters...\nusage: config<path>");
            System.exit(0);
        }
        if (args.length > 1) {
            System.out.println("only support for a properties file path");
            System.exit(0);
        }
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(args[0]));
            if (properties.containsKey("operation")) {
                String action = properties.getProperty("operation");
                switch (action) {
                    case "flume":
                        HttpManage.manageByHttp(properties);
                        break;
                    case "kafka":
                        new JMXClient(properties).loadByJMX();
                        break;
//                    case "flume,kafka":
//                        indi.yolo.sample.fk.monitor.HttpManage.manageByHttp(properties);
//                        new indi.yolo.sample.fk.monitor.JMXClient(properties).loadByJMX();
//                        break;
                    default:
                        throw new Exception("can not identify " + action);

                }
            } else throw new Exception("operating attributes are not configured...");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
