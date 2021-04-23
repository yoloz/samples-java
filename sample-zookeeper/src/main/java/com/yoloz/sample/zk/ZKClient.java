package com.yoloz.sample.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

public class ZKClient implements Closeable {

    private CuratorFramework curator;

    private ZKClient() {
    }

    private static class LazyHolder {
        static final ZKClient instance = new ZKClient();
    }

    static ZKClient getInstance() {
        return LazyHolder.instance;
    }

    void connect(String address) {
        Properties properties = new Properties();
        properties.put("zookeeper.connect", address);
        connect(properties);
    }

    void connect(Properties conf) {
        close();
        curator = CuratorFrameworkFactory.newClient(
                (String) conf.getOrDefault("zookeeper.connect", "127.0.0.1:2181"),
                (int) conf.getOrDefault("zookeeper.session.timeout.ms", 60000),
                (int) conf.getOrDefault("zookeeper.connection.timeout.ms", 60000),
                new RetryNTimes((int) conf.getOrDefault("zookeeper.retry.times", 5),
                        (int) conf.getOrDefault("zookeeper.retry.sleep.ms", 1000)));
        curator.start();
    }

    CuratorFramework getCurator() {
        return curator;
    }

    @Override
    public void close() {
        if (curator != null) curator.close();
    }


    void delete(String path) throws Exception {
        if (curator == null) throw new Exception("please connect first...");
        curator.delete().deletingChildrenIfNeeded().forPath(path);
    }

    void create(CreateMode mode, String path, String content) throws Exception {
        if (curator == null) throw new Exception("please connect first...");
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        if (curator.checkExists().forPath(path) == null) {
            if (mode == null) mode = CreateMode.EPHEMERAL;
            curator.create().creatingParentsIfNeeded().withMode(mode).forPath(path, bytes);
        } else curator.setData().forPath(path, bytes);
    }

    void update(String path, String content) throws Exception {
        if (curator == null) throw new Exception("please connect first...");
        curator.setData().forPath(path, content.getBytes(StandardCharsets.UTF_8));
    }

    String read(String path) throws Exception {
        if (curator == null) throw new Exception("please connect first...");
//        if (curator.checkExists().forPath(path) != null) {
        return new String(curator.getData().forPath(path), StandardCharsets.UTF_8);
    }

    List<String> ls(String path) throws Exception {
        if (curator == null) throw new Exception("please connect first...");
        return curator.getChildren().forPath(path);
    }

}
