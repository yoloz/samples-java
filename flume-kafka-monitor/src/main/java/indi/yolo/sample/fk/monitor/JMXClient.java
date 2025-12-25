package indi.yolo.sample.fk.monitor;//import javax.management.MBeanServerConnection;
//import javax.management.ObjectName;
//import javax.management.remote.JMXConnector;
//import javax.management.remote.JMXConnectorFactory;
//import javax.management.remote.JMXServiceURL;
//import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created on 17-2-21.
 */
public class JMXClient {

    private Map<String, String> defaultJmxConnectorProperties = new HashMap<>();
    private Properties properties;

    public JMXClient(Properties properties) {
        this.defaultJmxConnectorProperties.put("jmx.remote.x.request.waiting.timeout", properties.getProperty("jmx.remote.x.request.waiting.timeout", "3000"));
        this.defaultJmxConnectorProperties.put("jmx.remote.x.notification.fetch.timeout", properties.getProperty("jmx.remote.x.notification.fetch.timeout", "3000"));
        this.defaultJmxConnectorProperties.put("sun.rmi.transport.connectionTimeout", properties.getProperty("sun.rmi.transport.connectionTimeout", "3000"));
        this.defaultJmxConnectorProperties.put("sun.rmi.transport.tcp.handshakeTimeout", properties.getProperty("sun.rmi.transport.tcp.handshakeTimeout", "3000"));
        this.defaultJmxConnectorProperties.put("sun.rmi.transport.tcp.responseTimeout", properties.getProperty("sun.rmi.transport.tcp.responseTimeout", "3000"));
        this.properties = properties;
    }

    public void loadByJMX() throws Exception {
        Set<String> addrSet = new HashSet<>(1);
        //todo下面代码中添加验证ip和端口正则
        properties.stringPropertyNames().stream().filter(key -> key.startsWith("addr")).forEach(key -> addrSet.add(properties.getProperty(key)));
        if (addrSet.isEmpty()) {
            System.out.println("no address configure......");
        }
        Files.createDirectories(Paths.get(properties.getProperty("basePath", "/tmp/unimas/metric/")));
        final ExecutorService executorService = Executors.newFixedThreadPool(addrSet.size());
        List<ClientImpl> clients = new ArrayList<>(addrSet.size());
        for (String addr : addrSet) {
            ClientImpl client = new ClientImpl(this.defaultJmxConnectorProperties, addr, properties);
            clients.add(client);
            executorService.submit(client);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (ClientImpl client : clients) {
                client.destroy();
            }
            executorService.shutdown();
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }

//    public static void main(String[] args) {
//        indi.yolo.sample.fk.monitor.JMXClient jmxClient = new indi.yolo.sample.fk.monitor.JMXClient(new Properties());
//        JMXConnector jmxConnector = null;
//        try {
//            System.out.println("=============开始JMX连接============");
//            JMXServiceURL jmxServiceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://node11:9999/jmxrmi");
//            jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, jmxClient.defaultJmxConnectorProperties);
//            MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();
//            System.out.println("=============完成JMX连接============");
//            while (true) {
//                System.out.println("=============获取内容============");
//                ObjectName mbeanName = new ObjectName("kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec,topic=flume_kafka_test");
//                System.out.println("MessagesInPerSec = " + mBeanServerConnection.getAttribute(mbeanName, "Count"));
//                System.out.println("=============休眠等待============");
//                Thread.sleep(30 * 1000);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (jmxConnector != null) jmxConnector.close();
//            } catch (IOException e) {
//                //ignore
//            }
//        }
//    }
}
