package indi.yolo.sample.kafka.tools.topic;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created on 17-2-8.
 */
public class Command {

    static final String security_protocol = "security.protocol";

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
                    case "connect":
                        CommandImpl.testConnect(properties);
                        break;
                    case "performance":
                        CommandImpl.performance(properties);
                        break;
                    case "clear":
                        CommandImpl.deleteTopic(properties);
                        break;
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
