import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 *
 */
public class Context implements Closeable {

    private CuratorFramework curator;

    private Context() {
        try (InputStream in = Files.newInputStream(Paths.get(System.getProperty("zkConf")))) {
            Properties properties = new Properties();
            properties.load(in);
            curator = initCurator(properties);
            curator.start();
        } catch (IOException e) {
            e.printStackTrace();
            Logger logger = Logger.getLogger(Context.class);
            logger.error(e);
            System.exit(1);
        }
    }

    public static Context getInstance() {
        return LazyHolder.instance;
    }

    public CuratorFramework getCurator() {
        return curator;
    }

    @Override
    public void close() {
        if (curator != null) curator.close();
    }

    private static class LazyHolder {
        static final Context instance = new Context();
    }

    private CuratorFramework initCurator(Properties conf) throws IOException {
        String address = conf.getProperty("zookeeper.connect");
        if (address == null || address.isEmpty()) throw new IOException("zookeeper.connect is not defined");
        return CuratorFrameworkFactory.newClient(address,
                (int) conf.getOrDefault("zookeeper.session.timeout.ms", 60000),
                (int) conf.getOrDefault("zookeeper.connection.timeout.ms", 60000),
                new RetryNTimes((int) conf.getOrDefault("zookeeper.retry.times", 5),
                        (int) conf.getOrDefault("zookeeper.retry.sleep.ms", 1000)));
    }

}
