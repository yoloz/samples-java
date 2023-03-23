package indi.yolo.sample.sigar.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class Console extends Output {
    private final Logger logger = LoggerFactory.getLogger(Console.class);

    @Override
    public void apply(String key, byte[] value) {
        logger.info("key: " + key + "\n");
        logger.info("value: " + new String(value, Charset.forName("UTF-8")) + "\n");
    }
}
