package indi.yoloz.sample.ssdb;

//import org.apache.log4j.Logger;

import org.nutz.ssdb4j.spi.SSDB;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

class HashData {

//    private final Logger logger = Logger.getLogger(HashData.class);

    private final String[] types = new String[]{"aws", "bind", "bro", "vpn", "firewall",
            "proxy", "httpd", "syslog", "rails", "dev", "nginx", "waf", "ids", "ips", "soc", "siem",
            "vs", "utm", "ddos"};

    private final DateTimeFormatter keyFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSSSS");

    private AtomicInteger counter = new AtomicInteger(1);
    private LocalDate localDate;
    private LocalTime localTime;

    private int threads;
    private int days;
    private SSDB ssdb;

    HashData(SSDB ssdb, int threads, int days) {
        this.threads = threads;
        this.ssdb = ssdb;
        this.days = days;
    }

    private class WriteOneDay extends Thread {

        private Map<String, Object> value = new HashMap<>(6);

        private CountDownLatch latch;
        private int num;
        private String hashName;

        WriteOneDay(String tn, String hn, CountDownLatch latch, int num) {
            super(tn);
            this.latch = latch;
            this.num = num;
            this.hashName = hn;
        }

        @Override
        public void run() {
            try {
                value.put("district", "彭州市");
                value.put("city", "hangzhou");
                value.put("icscompany_name", "北京三维力控科技有限公司");

                for (int i = 0; i < num; i++) {
                    LocalTime _key = localTime.plusNanos(counter.getAndIncrement() * 100_000);
                    value.put("fakeTime", LocalDateTime.of(localDate, _key).format(dateTimeFormatter));
                    value.put("actualTime", LocalDateTime.now().format(dateTimeFormatter));
                    value.put("index", i);
                    ssdb.hset(hashName, _key.format(keyFormatter), toJson(value));
                }
            } catch (Exception e) {
                System.err.println(getName() + "=>" + e);
            }
            latch.countDown();
        }
    }

    void writeHash(Path datePath) {
        try {
            localDate = LocalDate.parse(Files.readAllLines(datePath).get(0), DateTimeFormatter.BASIC_ISO_DATE);
        } catch (Exception e) {
            System.out.println("init date err=>" + e);
            localDate = LocalDate.now();
            System.out.println("init date =>" + localDate.format(DateTimeFormatter.BASIC_ISO_DATE));
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                try {
                    if (ssdb != null) ssdb.close();
                } catch (IOException ignore) {
                }
                Files.write(datePath, localDate.format(DateTimeFormatter.BASIC_ISO_DATE)
                                .getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            } catch (Exception e) {
                System.err.println("save last date fail=>" + e);
            }
        }));

        Random random = new SecureRandom();
        while (days > 0) {
            String name = localDate.format(DateTimeFormatter.BASIC_ISO_DATE);
            System.out.println("days=>" + name + ",start=>" + System.currentTimeMillis());
            CountDownLatch downLatch = new CountDownLatch(threads);
            int num = 50_000_000 / threads;
            localTime = LocalTime.now();
            for (int i = 0; i < threads; i++) {
                int index = random.nextInt(types.length);
                new WriteOneDay(name + "-" + i, types[index] + "_" + name, downLatch, num).start();
            }
            try {
                downLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
            System.out.println("days=>" + name + ",finish=>" + System.currentTimeMillis());
            localDate = localDate.plusDays(1);
            counter.set(1);
            days--;
        }
    }

    private String toJson(Map<String, Object> map) {
        if (map == null || map.isEmpty()) return "{}";
        else {
            StringBuilder builder = new StringBuilder("{");
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                builder.append("\"").append(entry.getKey()).append("\"").append(":");
                if (entry.getValue() instanceof Integer) builder.append(entry.getValue()).append(",");
                else builder.append("\"").append(entry.getValue()).append("\",");
            }
            return builder.substring(0, builder.length() - 1) + "}";
        }
    }
}
