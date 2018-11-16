package server.fs;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class LocalFSMonitor implements Runnable {

    private final Logger logger = Logger.getLogger(LocalFSMonitor.class);

    private final int interval = 5000;
    private boolean error = false;
    private Map<String, FileAlterationListener> paths;
    private FileAlterationMonitor monitor;

//    public LocalFSMonitor(String... paths) {
//        this(new DatFile1(), paths);
//    }

    public LocalFSMonitor(FileAlterationListener fileAlterationListener, String... paths) {
        this.paths = new HashMap<>(paths.length);
        for (String path : paths) {
            this.paths.put(path, fileAlterationListener);
        }
        this.monitor = new FileAlterationMonitor(this.interval);
    }

    public LocalFSMonitor(Map<String, FileAlterationListener> paths) {
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
            logger.error(e);
            error = true;
            close();
        }
    }

    public void close() {
        if (monitor != null) {
            try {
                monitor.stop();
                monitor = null;
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    public boolean isError() {
        if (error && monitor != null) {
            close();
        }
        return error;
    }

}
