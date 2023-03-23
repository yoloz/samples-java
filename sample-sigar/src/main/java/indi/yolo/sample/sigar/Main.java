package indi.yolo.sample.sigar;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Properties;


public class Main {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class);
        String app_dir = System.getProperty("ga.base.dir");
        try {
            Properties config = new Properties();
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(
                    Paths.get(app_dir, "config", "gather.properties").toFile()),
                    Charset.forName("UTF-8"))) {
                config.load(reader);
            }
            Gather gather = new Gather(config);
            Runtime.getRuntime().addShutdownHook(new Thread(gather::close));
            gather.gather();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            System.exit(1);
        }
    }


}
