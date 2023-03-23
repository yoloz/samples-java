package indi.yolo.sample.sigar;

import indi.yolo.sample.sigar.gather.file.GatherFile;
import indi.yolo.sample.sigar.gather.system.GatherSys;
import indi.yolo.sample.sigar.output.Output;
import org.hyperic.sigar.Sigar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Properties;

public class Gather {

    private final Logger logger = LoggerFactory.getLogger(Gather.class);

    public static final String APP_DIR = System.getProperty("ga.base.dir");

    /**
     * configuration definition
     */
    private enum CONFIG {
        GATHER_FILE("gather.file.enable"), GATHER_SYS("gather.system.enable"),
        OUTPUT_TYPE("gather.output.type"), KAFKA_ADDR("gather.kafka.address"),
        KAFKA_TOPIC("gather.kafka.topic");
        private String value;

        CONFIG(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private final boolean gatherFile;
    private final boolean gatherSys;
    private final Properties config;
    private final Output output;

    private Sigar sigar = null;
    private GatherFile gf = null;
    private GatherSys gs = null;

    Gather(Properties pro) {
        this.config = pro;
        String gf = pro.getProperty(CONFIG.GATHER_FILE.getValue(), "false");
        this.gatherFile = gf.isEmpty() ? false : Boolean.valueOf(gf);
        String gs = pro.getProperty(CONFIG.GATHER_SYS.getValue(), "false");
        this.gatherSys = gs.isEmpty() ? false : Boolean.valueOf(gs);
        String outType = pro.getProperty(CONFIG.OUTPUT_TYPE.getValue(), "console");
        outType = outType.isEmpty() ? "console" : outType;
        String kafkaAddress = null, kafkaTopic = null;
        if ("kafka".equals(outType)) {
            kafkaAddress = pro.getProperty(CONFIG.KAFKA_ADDR.getValue());
            kafkaTopic = pro.getProperty(CONFIG.KAFKA_TOPIC.getValue());
            if (kafkaAddress == null || kafkaAddress.isEmpty() ||
                    kafkaTopic == null || kafkaTopic.isEmpty())
                throw new ConfigException("gather.kafka.* undefined or empty...");
        }
        this.output = Output.getOutput(outType, kafkaAddress, kafkaTopic);

    }

    void gather() {
        if (gatherFile) {
            logger.info("#################### gatherFile ####################");
            gf = new GatherFile(config, output);
            gf.gather();
        }
        if (gatherSys) {
            logger.info("#################### gatherSys ####################");
            String os = System.getProperty("os.name").toLowerCase();
            String java_libPath = System.getProperty("java.library.path");
            String sigar_libPath = Paths.get(APP_DIR, "lib", "sigar").toString();
            if (!java_libPath.contains(sigar_libPath)) {
                if (os.contains("win")) {
                    java_libPath += ";" + sigar_libPath;
                } else {
                    java_libPath += ":" + sigar_libPath;
                }
                System.setProperty("java.library.path", java_libPath);
            }
            sigar = new Sigar();
            gs = new GatherSys(config, output);
            gs.gather(sigar);
        }
    }

    void close() {
        logger.info("#################### gatherClose ####################");
        if (gatherFile) gf.close();
        if (gatherSys) {
            gs.close();
            sigar.close();
        }
        if (output != null) output.close();
    }
}
