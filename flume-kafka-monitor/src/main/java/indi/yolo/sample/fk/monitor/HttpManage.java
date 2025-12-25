package indi.yolo.sample.fk.monitor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Created on 17-2-20.
 */
public class HttpManage {

    public static void manageByHttp(Properties properties) throws Exception {
        String basePath = properties.getProperty("basePath", "/tmp/unimas/metric/");
        String sleep_time = properties.getProperty("sleepTime", "30");
        Set<URL> urls = new HashSet<>(1);
        properties.stringPropertyNames().stream().filter(key -> key.startsWith("url")).forEach(key -> {
            URL url = null;
            try {
                url = new URL(properties.getProperty(key));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (url != null) urls.add(url);
        });
        HttpImpl httpImpl = new HttpImpl(sleep_time, urls, basePath);
        Runtime.getRuntime().addShutdownHook(new Thread(httpImpl::destroy));
        httpImpl.load();
    }

//    public static void main(String[] args) throws Exception {
//        Properties properties = new Properties();
//        properties.load(new FileInputStream("E:\\projects\\23.11\\kafka\\MetricTool\\resources\\Template.properties"));
//        manageByHttp(properties);
//    }
}
