package indi.yolo.sample.fk.monitor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Created on 17-2-21.
 */
public class ClientImpl implements Runnable {

    private boolean normal = true;
    private Map<String, String> environment;
    private JMXConnector jmxConnector;
    private String address;
    private String basePath;
    private int sleep_time;
    private String[] topics = null;
//    private String[] attrs = new String[]{"Count", "FifteenMinuteRate", "FiveMinuteRate", "OneMinuteRate", "MeanRate"};

    ClientImpl(Map<String, String> jmxEnv, String address, Properties properties) {
        this.environment = jmxEnv;
        this.address = address;
        this.basePath = properties.getProperty("basePath", "/tmp/unimas/metric/");
        this.sleep_time = Integer.parseInt(properties.getProperty("sleepTime", "30"));
        if (properties.containsKey("topic")) {
            topics = properties.getProperty("topic").split(",");
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("=============开始JMX连接============");
            JMXServiceURL jmxServiceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + address + "/jmxrmi");
            jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, this.environment);
            MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();
            System.out.println("=============完成JMX连接============");
            List<String> list = new ArrayList<>(2);
            while (normal) {
                list.clear();
                String content;
                if (topics == null) content = this.brokerMetric(mBeanServerConnection);
                else content = this.topicMetric(mBeanServerConnection, topics);
                System.out.println("=============获取内容写进文件============");
                String fileName = "kafka_jmx_" + address.replace(":", ".");
                list.add(Command.simpleDateFormat.format(new Date()));
                list.add(content);
                Files.write(Paths.get(basePath, fileName), list, Charset.forName("UTF-8"), StandardOpenOption.APPEND,
                        StandardOpenOption.CREATE);
                System.out.println("=============休眠等待============");
                Thread.sleep(sleep_time * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            normal = false;
        } finally {
            try {
                if (jmxConnector != null) jmxConnector.close();
            } catch (IOException e) {
                //ignore
            }

        }
    }

    void destroy() {
        try {
            System.out.println("============准备关闭，倒计时10s...");
            if (jmxConnector != null) jmxConnector.close();
        } catch (Exception e) {
            //ignore
        }
    }

    private String brokerMetric(MBeanServerConnection mBeanServerConnection) throws MalformedObjectNameException {
        Map<String, Long> map = new HashMap<>();
        long value;
        for (Map.Entry<String, String> entry : InstanceSet.objectNames.entrySet()) {
            try {
                value = (long) mBeanServerConnection.getAttribute(new ObjectName(entry.getValue()), "Count");
            } catch (Exception e) {
                value = 0;
            }
            map.put(entry.getKey(), value);
        }
        return new Gson().toJson(map, new TypeToken<Map<String, Long>>() {
        }.getType());
    }

    private String topicMetric(MBeanServerConnection mBeanServerConnection, String[] topics) {
        Map<String, Map<String, Long>> map_topic = new HashMap<>();
        long value;
        for (String topic : topics) {
            Map<String, Long> map = new HashMap<>();
            for (Map.Entry<String, String> entry : InstanceSet.objectNames.entrySet()) {
                String objectName = entry.getValue() + ",topic=" + topic;
                try {
                    value = (long) mBeanServerConnection.getAttribute(new ObjectName(objectName), "Count");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    value = 0;
                }
                map.put(entry.getKey(), value);
            }
            map_topic.put(topic, map);
        }
        return new Gson().toJson(map_topic, new TypeToken<Map<String, Map<String, Long>>>() {
        }.getType());
    }
}
