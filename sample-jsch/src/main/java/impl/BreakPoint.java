package impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

class BreakPoint {

    private final Properties properties = new Properties();
    private final Path breakpoint;
    private ConcurrentHashMap<String, String> caches = new ConcurrentHashMap<>();

    BreakPoint(String serviceId) throws IOException {
        Path dir = Paths.get(System.getProperty("cii.root.dir"), "data", serviceId);
        if (!dir.toFile().exists()) Files.createDirectories(dir);
        breakpoint = dir.resolve(serviceId + ".bp");
        if (breakpoint.toFile().exists()) {
            try (InputStream inputStream = Files.newInputStream(breakpoint)) {
                properties.load(inputStream);
                properties.forEach((k, v) -> caches.put((String) k, (String) v));
            }
        }
    }

    private boolean isGreater(String src, String dst) {
        if (src == null || dst == null) return false;
        src = src.trim();
        dst = dst.trim();
        if (src.length() > dst.length()) return true;
        if (src.length() == dst.length()) return src.compareToIgnoreCase(dst) > 0;
        return false;
    }

    private void put(String key, String value) {
        if (!caches.containsKey(key) || isGreater(value, String.valueOf(caches.get(key)))) {
            caches.put(key, value);
        }
    }

    private void put(String key, long value) {
        if (!caches.containsKey(key) || value > Long.parseUnsignedLong(caches.get(key))) {
            caches.put(key, value + "");
        }
    }

    boolean checkDown(String key, String value) {
        if (!properties.containsKey(key) || isGreater(value, String.valueOf(properties.get(key)))) {
            put(key, value);
            return true;
        }
        return false;
    }

    boolean checkDown(String key, long value) {
        if (!properties.containsKey(key) || value > Long.parseUnsignedLong(properties.getProperty(key))) {
            put(key, value);
            return true;
        }
        return false;
    }

    void store() throws IOException {
        if (breakpoint != null && !caches.isEmpty()) {
            try (OutputStream out = Files.newOutputStream(breakpoint)) {
                properties.clear();
                properties.putAll(caches);
                properties.store(out, "");
            }
        }
    }
}
