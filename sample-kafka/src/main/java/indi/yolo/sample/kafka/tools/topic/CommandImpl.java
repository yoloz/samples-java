package indi.yolo.sample.kafka.tools.topic;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.security.JaasUtils;
import org.apache.kafka.tools.consumer.ConsoleConsumer;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created on 17-2-7.
 */
public class CommandImpl {

    private static void initZkSecurity(Properties proper) {
        if (proper.containsKey(JaasUtils.ZK_SASL_CLIENT))
            System.setProperty(JaasUtils.ZK_SASL_CLIENT, proper.getProperty(JaasUtils.ZK_SASL_CLIENT));
        if (proper.containsKey(JaasUtils.ZK_LOGIN_CONTEXT_NAME_KEY))
            System.setProperty(JaasUtils.ZK_LOGIN_CONTEXT_NAME_KEY, proper.getProperty(JaasUtils.ZK_LOGIN_CONTEXT_NAME_KEY));
    }

    private static void performKafSecurity(Properties properties) {
        if (properties.getProperty(Command.security_protocol).contains("SASL")) {
            System.setProperty("java.security.krb5.conf", properties.getProperty("java.security.krb5.conf"));
            System.setProperty("java.security.auth.login.config", properties.getProperty("java.security.auth.login.config"));
        }
    }

    static void testConnect(Properties proper) throws Exception {
        if (!proper.containsKey("topic")) throw new Exception("topic not configured......");
        if (!proper.containsKey("bootstrap.servers")) throw new Exception("bootstrap.servers not configured......");
        KafkaProducer<String, String> producer = null;
        try {
            if (proper.getProperty("topic").equals(getTopic(proper))) {
                System.out.println("topic " + proper.getProperty("topic") + " is exist...");
            } else {
                createTopic(proper);
            }
            performKafSecurity(proper);
            proper.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            proper.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            producer = new KafkaProducer<>(proper);
            producer.send(new ProducerRecord<>(proper.getProperty("topic"), "test available...")).get();
            String[] args = new String[]{"--bootstrap-server", proper.getProperty("bootstrap.servers"), "--topic",
                    proper.getProperty("topic"), "--max-messages", "1", "--from-beginning"};
            ConsoleConsumer.main(args);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (producer != null) producer.close();
//            deleteTopic(proper);
        }
        System.out.println("Kafka is ok...");
    }

    /**
     * 创建主题
     * "kafka-topics.sh --config retention.ms=86400000 --create --partitions 2 --replication-factor 2 --topic test --zookeeper node11,node12,node14/kafka"
     *
     * @param proper 参数
     * @throws Exception error
     */
    private static void createTopic(Properties proper) throws Exception {
        if (!proper.containsKey("bootstrap.servers")) throw new Exception("bootstrap.servers not configured......");
        if (!proper.containsKey("topic")) throw new Exception("topic not configured......");

        Properties adminProps = new Properties();
        adminProps.put("bootstrap.servers", proper.getProperty("bootstrap.servers"));
        performKafSecurity(adminProps);

        try (AdminClient adminClient = AdminClient.create(adminProps)) {
            String topicName = proper.getProperty("topic");
            int partitions = Integer.parseInt(proper.getProperty("partitions", "1"));
            short replicationFactor = Short.parseShort(proper.getProperty("replication-factor", "1"));

            NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);

            // 添加配置参数
            Set<String> keys = proper.stringPropertyNames();
            Map<String, String> configs = new HashMap<>();
            keys.stream().filter(key -> key.contains("config-")).forEach(key -> {
                String value = proper.getProperty(key);
                String configKey = key.substring(key.indexOf("config-") + 7);
                configs.put(configKey, value);
            });
            newTopic.configs(configs);

            CreateTopicsResult result = adminClient.createTopics(Collections.singletonList(newTopic));
            result.all().get(); // 等待创建完成
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static String getTopic(Properties proper) throws Exception {
        if (!proper.containsKey("bootstrap.servers")) throw new Exception("bootstrap.servers not configured......");

        Properties adminProps = new Properties();
        adminProps.put("bootstrap.servers", proper.getProperty("bootstrap.servers"));
        performKafSecurity(adminProps);

        try (AdminClient adminClient = AdminClient.create(adminProps)) {
            ListTopicsResult topicsResult = adminClient.listTopics();
            Set<String> topics = topicsResult.names().get();
            String targetTopic = proper.getProperty("topic");

            if (topics.contains(targetTopic)) {
                return targetTopic;
            } else {
                return ""; // Topic does not exist
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw e;
        }
    }

    static void deleteTopic(Properties proper) throws Exception {
        if (!proper.containsKey("bootstrap.servers")) throw new Exception("bootstrap.servers not configured......");
        if (!proper.containsKey("topic")) throw new Exception("topic not configured......");

        Properties adminProps = new Properties();
        adminProps.put("bootstrap.servers", proper.getProperty("bootstrap.servers"));
        performKafSecurity(adminProps);

        try (AdminClient adminClient = AdminClient.create(adminProps)) {
            DeleteTopicsResult result = adminClient.deleteTopics(Collections.singletonList(proper.getProperty("topic")));
            result.all().get(); // 等待删除完成
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw e;
        }
    }

    static void performance(Properties proper) throws Exception {
        if (!proper.containsKey("topic")) throw new Exception("topic not configured......");
//        String result = "";
//        PrintStream console = System.out;
//        ByteArrayOutputStream bo = new ByteArrayOutputStream();
//        try (PrintStream ps = new PrintStream(bo)) {
        if ("producer".equals(proper.getProperty("type"))) {
            if (proper.getProperty("topic").equals(getTopic(proper))) {
                System.out.println("topic " + proper.getProperty("topic") + " is exist...");
            } else {
                createTopic(proper);
            }
//                System.setOut(ps);
            List<String> argsList = new ArrayList<>();
            argsList.add("--topic");
            argsList.add(proper.getProperty("topic"));
            argsList.add("--num-records");
            argsList.add(proper.getProperty("num-records", "100000"));
            argsList.add("--record-size");
            argsList.add(proper.getProperty("record-size", "1024"));
            argsList.add("--throughput");
            argsList.add(proper.getProperty("throughput", "2"));
            argsList.add("--producer-props");
            Set<String> keys = proper.stringPropertyNames();
            keys.stream().filter(key -> key.contains("props-")).forEach(key -> {
                String value = proper.getProperty(key);
                key = key.substring(key.indexOf("props-") + 6);
                argsList.add(key + "=" + value);
            });
            String[] args = argsList.toArray(new String[argsList.size()]);
            org.apache.kafka.tools.ProducerPerformance.main(args);
//                String[] results = bo.toString().split(",");
//                result = results[0] + " " + results[1];
        }
//        }
//        System.setOut(console);
//        return result;
    }

    public static void main(String[] args) throws Exception {
//        Properties properties = new Properties();
//        properties.load(new FileInputStream("E:\\projects\\GitHub\\BigData\\Kafka\\src\\com\\unimas\\kafka\\Template.properties"));
//        deleteTopic(properties);
//        System.out.println(testConnect(properties));
//        System.out.println(performance(properties));
    }
}
