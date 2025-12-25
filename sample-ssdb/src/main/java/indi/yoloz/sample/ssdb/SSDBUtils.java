package indi.yoloz.sample.ssdb;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
//import org.apache.log4j.Category;
//import org.apache.log4j.Level;
//import org.apache.log4j.LogManager;
//import org.apache.log4j.Logger;
//import org.apache.log4j.PatternLayout;
//import org.apache.log4j.PropertyConfigurator;
//import org.apache.log4j.RollingFileAppender;
import org.nutz.ssdb4j.SSDBs;
import org.nutz.ssdb4j.spi.Response;
import org.nutz.ssdb4j.spi.SSDB;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class SSDBUtils {

//    private static final Logger logger = Logger.getLogger(SSDBUtils.class);

    private enum Type {
        kv("kv"), list("list"), set("set"), hash("hash");

        private String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private SSDB ssdb;

    private final NewKey key = new NewKey();
    private final String value;

    private final int threads;

    private Path jarPath = Paths.get(System.getProperty("jarPath"));

    private SSDBUtils(int threads, String value) {
//        jarPath = Paths.get(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath())
//                .getParent();
//        PropertyConfigurator.configure(this.getClass().getResource("log4j.properties"));

        this.threads = threads;
        this.value = value;
    }

    private void connectSSDB(String ip, int port, int timeout) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(threads);
        config.setMaxIdle(threads);
        this.ssdb = SSDBs.pool(ip, port, timeout, config);
    }

    /**
     * 在数据较多时清除很慢,直接手动删除磁盘文件然后重启
     */
  /*  private void clear() {
        Response resp = ssdb.flushdb("");
        if (!resp.ok()) {
            System.out.println("数据删除失败");
            close();
            System.exit(1);
        }
    }*/

    private class WriteDb extends Thread {
//        private final Logger logger_inner = getLogger();

        private Type type;
        private int _total;
        private CountDownLatch downLatch;

        private WriteDb(Type type, int total, CountDownLatch latch) {
            this.type = type;
            this._total = total;
            this.downLatch = latch;
        }

        @Override
        public void run() {
            System.out.println(getName() + " start...");
            try {
                switch (type) {
                    case list:
                        list(getName());
                        break;
                    case kv:
                    case set:
                    case hash:
                        kv_set_hash(getName());
                        break;
                    default:
                        System.out.println(type.toString() + " is not support...");
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            downLatch.countDown();
        }

        private void kv_set_hash(String name) {
            long start = System.currentTimeMillis();
            long period = 10 * 60 * 1000;
            long counter = period;
            for (int i = 0; i < _total; i++) {
                String _key = key.create();
                if ((System.currentTimeMillis() - start) > counter) {
                    System.out.println(name + "[" + type.toString() + ",key=>]" + _key);
                    counter += period;
                }
                if (type == Type.kv) ssdb.set(_key, value);
                else if (type == Type.set) ssdb.zset(name, _key, i);
                else if (type == Type.hash) ssdb.hset(name, _key, value);
                else {
                    System.out.println(type.toString() + " is not support.....");
                    break;
                }
            }
            long use_time = (System.currentTimeMillis() - start) / 1000;
            System.out.println(name + "[" + type.toString() + ",耗时:=>]" + use_time + "[eps=>]" + _total / use_time);
        }

        private void list(String name) {
            long start = System.currentTimeMillis();
            long counter = _total / 100;
            int suffixIndex = (int) (_total - counter * 100);
            Object[] values = new Object[100];
            for (int i = 0; i < 100; i++) values[i] = value;
            for (int i = 0; i < counter; i++) ssdb.qpush_front(name, values);
            if (suffixIndex > 0) {
                values = new Object[suffixIndex];
                for (int i = 0; i < suffixIndex; i++) values[i] = value;
                ssdb.qpush_front(name, values);
            }
            long use_time = (System.currentTimeMillis() - start) / 1000;
            System.out.println(name + "[" + type.toString() + ",耗时:=>]" + use_time + "[eps=>]" + _total / use_time);
        }

//        private Logger getLogger() {
//            String categoryName = "ssdbUtils-" + getName();
//            Path logFile = jarPath.resolve("logs").resolve(getName() + ".log");
//            if (LogManager.exists(categoryName) != null) {
//                return Logger.getLogger(categoryName);
//            }
//            try {
//                String appendName = "A" + categoryName;
//                PatternLayout layout = new PatternLayout();
//                layout.setConversionPattern("%d{ISO8601} %p %t %c - %m%n");
//
//                RollingFileAppender rollingFileAppender = new RollingFileAppender(layout, logFile.toString(), true);
//                rollingFileAppender.setName(appendName);
//                rollingFileAppender.setThreshold(Level.INFO);
//                rollingFileAppender.setImmediateFlush(true);
//                rollingFileAppender.setMaxBackupIndex(3);
//                rollingFileAppender.setMaxFileSize("100MB");
//                rollingFileAppender.setEncoding("UTF-8");
//
//                Category category = Logger.getLogger(categoryName);
//                category.setAdditivity(false);
//                category.setLevel(Level.INFO);
//                category.addAppender(rollingFileAppender);
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.out.println(e);
//            }
//            return Logger.getLogger(categoryName);
//        }
    }

    private void clearLogs() {
        try {
            Files.walkFileTree(jarPath.resolve("logs"), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class QueryDb {
        private Type type;

        private QueryDb(Type type) {
            this.type = type;
        }

        private void query(Object... params) {
            switch (type) {
                case kv:
                    kv(params);
                    break;
                case list:
                    list(params);
                    break;
                case set:
                case hash:
                    set_hash(params);
                    break;
                default:
                    System.out.println(type.toString() + " is not support...");
            }
        }

        private void kv(Object... params) {
            long start = System.currentTimeMillis();
            Response response;
            if (params.length == 1) response = ssdb.get(params[0]);//get获取的response中[value]
            else response = ssdb.multi_get(params); //multi_get获取的response中[k1,v1,k2,v2...]
            long use_time = (System.currentTimeMillis() - start) / 1000;
            System.out.println("[" + type.toString() + " get/multi_get 耗时=>]" + use_time);
            outputResp(type, response, params);
        }


        private void list(Object... params) {
            long start = System.currentTimeMillis();
            Response response = ssdb.qget(params[0], (int) params[1]); //qget获取的response中[value]
            long use_time = (System.currentTimeMillis() - start) / 1000;
            System.out.println("[" + type.toString() + " qget 耗时=>]" + use_time);
            outputResp(type, response, params);
        }

        private void set_hash(Object... params) {
            long start = System.currentTimeMillis();
            Object name = params[0];
            Object[] keys = new Object[params.length - 1];
            System.arraycopy(params, 1, keys, 0, params.length - 1);
            Response response;
            if (type == Type.set) {
                if (params.length == 2) response = ssdb.zget(name, keys[0]);//zget获取的response中[value]
                else response = ssdb.multi_zget(name, keys); //multi_zget获取的response中[k1,v1,k2,v2...]
            } else {
                if (params.length == 2) response = ssdb.hget(name, keys[0]);//hget获取的response中[value]
                else response = ssdb.multi_hget(name, keys); //multi_hget获取的response中[k1,v1,k2,v2...]
            }
            long use_time = (System.currentTimeMillis() - start) / 1000;
            System.out.println("[" + type.toString() + " zget/hget/multi_zget/multi_hget 耗时=>]" + use_time);
            outputResp(type, response, params);
        }
    }

    private void outputResp(Type type, Response response, Object... keys) {
        if (response.datas.size() == 0) System.out.println(Arrays.toString(keys) + " lost data");
        else {
            StringBuilder builder = new StringBuilder(type.toString()).append("[内容=>]");
            if (response.datas.size() == 1) builder.append(response.asString());
            else {
                for (int i = 0; i < response.datas.size(); i += 2) {
                    builder.append(new String(response.datas.get(i), SSDBs.DEFAULT_CHARSET))
                            .append("=")
                            .append(new String(response.datas.get(i + 1), SSDBs.DEFAULT_CHARSET));
                    if (i != (response.datas.size() - 2)) builder.append(";");
                }
            }
            System.out.println(builder.toString());
        }
    }

    private class ScanDb {
        private Type type;

        private ScanDb(Type type) {
            this.type = type;
        }

        private void scan(Object... params) {
            switch (type) {
                case kv:
                    kv(params);
                    break;
                case list:
                    list(params);
                    break;
                case set:
                    set(params);
                    break;
                case hash:
                    hash(params);
                    break;
                default:
                    System.out.println(type.toString() + " is not support...");
            }
        }

        private void kv(Object... params) {
            long start = System.currentTimeMillis();
            Response response = ssdb.scan(params[0], params[1], (int) params[2]);//scan获取的response中[k1,v1,k2,v2...]
            long use_time = (System.currentTimeMillis() - start) / 1000;
            System.out.println("[" + type.toString() + " scan 耗时:]=>" + use_time);
            outputResp(type, response, params);
        }

        private void list(Object... params) {
            long start = System.currentTimeMillis();
            Response response = ssdb.qrange(params[0], (int) params[1], (int) params[2]);//qrange获取的response中[v1,v2,v3...]
            long use_time = (System.currentTimeMillis() - start) / 1000;
            System.out.println("[" + type.toString() + " qrange 耗时:]=>" + use_time);
            outputResp(type, response, params);
        }

        private void set(Object... params) {
            long start = System.currentTimeMillis();
            Response response = ssdb.zscan(params[0], params[1], params[2], params[3], (int) params[4]);//zscan获取的response中[k1,v1,k2,v2...]
            long use_time = (System.currentTimeMillis() - start) / 1000;
            System.out.println("[" + type.toString() + " zscan 耗时:]=>" + use_time);
            outputResp(type, response, params);
        }

        private void hash(Object... params) {
            long start = System.currentTimeMillis();
            Response response = ssdb.hscan(params[0], params[1], params[2], (int) params[3]);//hscan获取的response中[k1,v1,k2,v2...]
            long use_time = (System.currentTimeMillis() - start) / 1000;
            System.out.println("[" + type.toString() + " hscan 耗时:]=>" + use_time);
            outputResp(type, response, params);
        }
    }

    private void close() {
        try {
            if (ssdb != null) ssdb.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        if (args == null || args.length == 0) {
            System.out.println("empty args,exit...");
            System.exit(1);
        }
        Path path = Paths.get(args[0]);
        if (!path.toFile().exists()) {
            System.out.println(args[0] + " is not exit...");
            System.exit(1);
        }

        String defaultValue = "{\"facility_label\":\"kernel\",\"district\":\"彭州市\",\"icscompany_name\":\"北京三维力控科技有限公司\"}";

        String action = "write";
        String type = "kv";
        int total = 1;
        int threads = 1;
        String value = defaultValue;
        String ip = "127.0.0.1";
        int port = 8888;
        int timeout = 10000;

        try (InputStream in = new FileInputStream(path.toFile())) {
            Properties properties = new Properties();
            properties.load(in);
            action = properties.getProperty("action");
            type = properties.getProperty("type", "hash");
            total = Integer.parseInt(properties.getProperty("total", "1000"));
            threads = Integer.parseInt(properties.getProperty("threads", "1"));
            value = properties.getProperty("value", defaultValue);
            ip = properties.getProperty("ip", "127.0.0.1");
            port = Integer.parseInt(properties.getProperty("port", "8888"));
            timeout = Integer.parseInt(properties.getProperty("timeout", "10000"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        SSDBUtils ssdbUtils = new SSDBUtils(threads, value);
        ssdbUtils.connectSSDB(ip, port, timeout);

        Type _type = Type.kv;
        switch (type) {
            case "kv":
                _type = Type.kv;
                break;
            case "list":
                _type = Type.list;
                break;
            case "set":
                _type = Type.set;
                break;
            case "hash":
                _type = Type.hash;
                break;
            default:
                System.out.println("type: " + type + " is not support,exit...");
                System.exit(1);
        }
        if ("hashData".equals(action)) {
            HashData hashData = new HashData(ssdbUtils.ssdb, threads, total);
            hashData.writeHash(ssdbUtils.jarPath.resolve("hashData"));
        } else if ("listData".equals(action)) {
            ListData listData = new ListData(ssdbUtils.ssdb, threads, total);
            listData.writeList(ssdbUtils.jarPath.resolve("listData"));
        } else if ("write".equals(action)) {
            ssdbUtils.clearLogs();
            CountDownLatch downLatch = new CountDownLatch(threads);
            int count = total / threads;
            for (int i = 0; i < threads; i++) {
                ssdbUtils.new WriteDb(_type, count, downLatch).start();
            }
            try {
                downLatch.await();
            } catch (InterruptedException e) {
                System.out.println(e);
                System.exit(1);
            }
        } else if ("get".equals(action) || "multi_get".equals(action) || "scan".equals(action)) {
            QueryDb queryDb = ssdbUtils.new QueryDb(_type);
            ScanDb scanDb = ssdbUtils.new ScanDb(_type);
            Random random = new Random();
            for (int i = 1; i <= threads; i++) {
                String name = "Thread-" + i;
                Object[] keys;
                if ("get".equals(action) || "multi_get".equals(action)) {
                    if ("get".equals(action)) keys = ssdbUtils.randomKeys(name, 1, random);
                    else keys = ssdbUtils.randomKeys(name, 10, random);
                    switch (_type) {
                        case kv:
                            queryDb.query(keys);
                            break;
                        case list:
                            int max = ssdbUtils.ssdb.qsize(name).asInt();
                            queryDb.query(name, random.nextInt(max));
                            break;
                        case set:
                        case hash:
                            Object[] arr = new Object[keys.length + 1];
                            arr[0] = name;
                            System.arraycopy(keys, 0, arr, 1, keys.length);
                            queryDb.query(arr);
                            break;
                    }
                } else if ("scan".equals(action)) {
                    keys = ssdbUtils.randomKeys(name, 2, random);
                    String startK = (String) keys[0];
                    String endK = (String) keys[1];
                    if (startK.compareTo(endK) > 0) {
                        startK = (String) keys[1];
                        endK = (String) keys[0];
                    }
                    switch (_type) {
                        case kv:
                            scanDb.scan(startK, endK, 10);
                            break;
                        case list:
                            int max_l = ssdbUtils.ssdb.qsize(name).asInt();
                            scanDb.scan(name, random.nextInt(max_l), 10);
                            break;
                        case set:
                            int max_s = ssdbUtils.ssdb.zsize(name).asInt();
                            int start = random.nextInt(max_s);
                            int end = random.nextInt(max_s);
                            if (start > end) {
                                int temp = start;
                                start = end;
                                end = temp;
                            }
                            scanDb.scan(name, startK, start, end, 10);
                            break;
                        case hash:
                            scanDb.scan(name, startK, endK, 10);
                            break;
                    }
                }
            }
        } else if ("size".equals(action)) {
            int total_count = 0;
            switch (_type) {
                case kv:
                    System.out.println("no api support...");
                    break;
                case list:
                    for (int i = 1; i <= threads; i++) {
                        String name = "Thread-" + i;
                        Response resp = ssdbUtils.ssdb.qsize(name);
                        int _count = resp.asInt();
                        total_count += _count;
                        System.out.println(name + "[count=>]" + _count);
                    }
                    break;
                case set:
                    for (int i = 1; i <= threads; i++) {
                        String name = "Thread-" + i;
                        Response resp = ssdbUtils.ssdb.zsize(name);
                        int _count = resp.asInt();
                        total_count += _count;
                        System.out.println(name + "[count=>]" + _count);
                    }
                    break;
                case hash:
                    for (int i = 1; i <= threads; i++) {
                        String name = "Thread-" + i;
                        Response resp = ssdbUtils.ssdb.hsize(name);
                        int _count = resp.asInt();
                        total_count += _count;
                        System.out.println(name + "[count=>]" + _count);
                    }
                    break;
            }
            System.out.println("[total count=>]" + total_count);
        } else if ("sumTime".equals(action)) {
            ssdbUtils.sumLogTime();
        } else {
            System.out.println(action + " is not support...");
        }
        ssdbUtils.close();
        System.out.println("*******************finish*******************");
    }

    private Object[] randomKeys(String fileName, int times, Random random) {
        Object[] keys = new Object[times];
        Path path = jarPath.resolve("logs").resolve(fileName + ".log");
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            int max = lines.size() - 1;
            for (int i = 0; i < times; i++) {
                int index = random.nextInt(max);
                if (index == 0) index++;
                String line = lines.get(index);
                int start = line.indexOf("=>]");
                if (start > 0) {
                    int end = start + 3 + 14;
                    keys[i] = line.substring(start + 3, end).trim();
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return keys;
    }

    private void sumLogTime() {
        int total = 0;
        int counter = 0;
        Path dir = jarPath.resolve("logs");
        for (File file : Objects.requireNonNull(dir.toFile().listFiles())) {
            try {
                List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                String line = lines.get(lines.size() - 1);
                int start = line.indexOf("=>]");
                if (start > 0) {
                    int end = line.indexOf("[", start);
                    total += Integer.parseInt(line.substring(start + 3, end).trim());
                    counter += 1;
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        System.out.println("[total time=>]" + total + "[threads=>]" + counter + "[avg time=>]" + (total / counter));
    }

    private class NewKey {
        private final AtomicInteger NEXT_COUNTER = new AtomicInteger(new SecureRandom().nextInt());

        private final char[] HEX_CHARS = new char[]{
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        private NewKey() {
        }

        private String create() {
            int timestamp = (int) (System.currentTimeMillis() / 1000);
            int counter = NEXT_COUNTER.getAndIncrement() & 0x00ffffff;

            char[] chars = new char[14];
            int i = 0;
            for (byte b : toByteArray(timestamp, counter)) {
                chars[i++] = HEX_CHARS[b >> 4 & 0xF];
                chars[i++] = HEX_CHARS[b & 0xF];
            }
            return new String(chars);
        }

        private byte[] toByteArray(int timestamp, int counter) {
            ByteBuffer buffer = ByteBuffer.allocate(7);
            buffer.put(int3(timestamp));
            buffer.put(int2(timestamp));
            buffer.put(int1(timestamp));
            buffer.put(int0(timestamp));
            buffer.put(int2(counter));
            buffer.put(int1(counter));
            buffer.put(int0(counter));
            return buffer.array();
        }

        private byte int3(final int x) {
            return (byte) (x >> 24);
        }

        private byte int2(final int x) {
            return (byte) (x >> 16);
        }

        private byte int1(final int x) {
            return (byte) (x >> 8);
        }

        private byte int0(final int x) {
            return (byte) (x);
        }

    }
}
