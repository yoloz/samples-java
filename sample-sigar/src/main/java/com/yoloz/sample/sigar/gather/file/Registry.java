package com.yoloz.sample.sigar.gather.file;

import com.yoloz.sample.sigar.Gather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import LocalLog;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
//import java.util.logging.Logger;

public class Registry {

    //    private static final Logger logger = LocalLog.getLogger();
    private static final Logger logger = LoggerFactory.getLogger(Registry.class);
    private static final Path path = Paths.get(Gather.APP_DIR, "data", "registry");

    public static Map<String, String> get() {
        logger.debug(Thread.currentThread().getName() + "-registry: get-" + System.currentTimeMillis());
        Map<String, String> result = new HashMap<>();
        File file = path.toFile();
        if (file.exists()) {
            Properties properties = new Properties();
            try (InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(file), Charset.forName("UTF-8"))) {
                properties.load(reader);
                Map<String, String> tmp = toMap(properties);
                tmp.forEach((k, v) -> {
                    if (Paths.get(k).toFile().exists()) result.put(k, v);
                });
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return result;
    }

    public static void write(Map<String, String> content) {
        logger.debug(Thread.currentThread().getName() + "-registry: write-" + System.currentTimeMillis());
        Properties properties = mapTo(content);
        if (!properties.isEmpty()) {
            File file = path.toFile();
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), Charset.forName("UTF-8")))) {
                properties.store(writer, "");
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private static Properties mapTo(Map<String, String> map) {
        Properties properties = new Properties();
        if (map != null) map.forEach(properties::setProperty);
        return properties;
    }

    private static Map<String, String> toMap(Properties properties) {
        Map<String, String> map = new HashMap<>();
        if (properties != null) properties.forEach((k, v) -> map.put((String) k, (String) v));
        return map;
    }
}
