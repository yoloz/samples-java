package indi.yoloz.sample.utils;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.util.HashMap;
import java.util.Map;

/**
 * 监控文件系统变化，支持多级目录下文件
 * <p>
 * 17-5-14
 */
public class FilesMonitor implements Runnable {

//    private final Logger logger = Logger.getLogger(FilesMonitor.class);

    private final int interval = 5000;
    private boolean stop = false;
    private Map<String, FileAlterationListener> paths;
    private FileAlterationMonitor monitor;

    public FilesMonitor(String... paths) {
        this(new FilesAlterationListener(), paths);
    }

    public FilesMonitor(FileAlterationListener fileAlterationListener, String... paths) {
        this.paths = new HashMap<>(paths.length);
        for (String path : paths) {
            this.paths.put(path, fileAlterationListener);
        }
        this.monitor = new FileAlterationMonitor(this.interval);
    }

    public FilesMonitor(Map<String, FileAlterationListener> paths) {
        this.paths = paths;
        this.monitor = new FileAlterationMonitor(this.interval);
    }

    @Override
    public void run() {
        for (Map.Entry<String, FileAlterationListener> path : paths.entrySet()) {
            FileAlterationObserver observer =
                    new FileAlterationObserver(path.getKey(), file -> !file.getName().contains(".swp"));
            observer.addListener(path.getValue());
            monitor.addObserver(observer);
        }
        try {
            monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
            stop = true;
            close();
        }
    }

    private void close() {
        if (monitor != null) {
            try {
                monitor.stop();
                monitor = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isStop() {
        if (stop && monitor != null) {
            close();
        }
        return stop;
    }

    public static void main(String[] args) {
        FilesMonitor filesMonitor = new FilesMonitor("/XXX/ethan/projects", "/XXX/ethan/test");
        new Thread(filesMonitor).start();
        while (true) {
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(filesMonitor.isStop());
            if (filesMonitor.isStop()) {
                filesMonitor = new FilesMonitor("/XXX/ethan/projects", "/XXX/ethan/test");
                new Thread(filesMonitor).start();
            }
        }


    }
}
