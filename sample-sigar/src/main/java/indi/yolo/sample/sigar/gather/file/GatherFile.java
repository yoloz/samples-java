package indi.yolo.sample.sigar.gather.file;

//import LocalLog;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
//import java.util.logging.Logger;
import indi.yolo.sample.sigar.ConfigException;
import indi.yolo.sample.sigar.output.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件采集单个文件不可大于(Integer.MAX_VALUE - 8)字节;
 */
public class GatherFile {

    //    private final Logger logger = LocalLog.getLogger();
    private final Logger logger = LoggerFactory.getLogger(GatherFile.class);

    /**
     * configuration definition
     */
    private enum CONFIG {
        PATHS("gather.file.paths"), MODE("gather.file.path.mode"),
        THREAD_NUM("gather.file.num.threads"), INTERVAL("gather.file.interval.sec");
        private String value;

        CONFIG(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private final int threads;
    private final long interval;
    private final String _paths;
    private final String pathMode;
    private final Output output;

    private ScheduledExecutorService scheduledService;
    private ScheduledFuture<?> scheduledFuture;
    private ScheduledExecutorService registryService;
    private ScheduledFuture<?> registryFuture;
    private ExecutorService executor;

    private ConcurrentHashMap<String, String> cache;

    public GatherFile(Properties config, Output output) {
        String _threads = config.getProperty(CONFIG.THREAD_NUM.getValue(), "");
        this.threads = _threads.isEmpty() ? Runtime.getRuntime().availableProcessors() : Integer.parseInt(_threads);
        String _interval = config.getProperty(CONFIG.INTERVAL.getValue(), "5");
        this.interval = _interval.isEmpty() ? 5 : Integer.parseInt(_interval);
        String mode = config.getProperty(CONFIG.MODE.getValue(), "lazy");
        this.pathMode = mode.isEmpty() ? "lazy" : mode;
        String pathStr = config.getProperty(CONFIG.PATHS.getValue(), "");
        if (pathStr.isEmpty()) throw new ConfigException(CONFIG.PATHS.getValue() + " is empty...");
        this._paths = pathStr;
        this.output = output;
    }

    public void gather() {
        cache = new ConcurrentHashMap<>(0);
        logger.debug("threads:" + threads + "-interval:" + interval);
        logger.info("=================gatherFile start=================");
        Map<String, String> initCache = Registry.get();
        if (!initCache.isEmpty()) cache.putAll(initCache);
        logger.debug("init cache size:" + cache.size());
        scheduledService = Executors.newScheduledThreadPool(1);
        registryService = Executors.newScheduledThreadPool(1);
        executor = Executors.newFixedThreadPool(threads);
        if ("lazy".equals(pathMode)) scheduledFuture = scheduledService.scheduleWithFixedDelay(
                new GatherImpl(executor, resolvePaths(_paths).toArray()), 1, interval, TimeUnit.SECONDS);
        else scheduledFuture = scheduledService.scheduleWithFixedDelay(
                new GatherImpl(executor, _paths), 1, interval, TimeUnit.SECONDS);
        registryFuture = registryService.scheduleWithFixedDelay(() -> Registry.write(cache),
                1, 60, TimeUnit.SECONDS);
    }

    public void close() {
        if (executor != null) {
            executor.shutdown();
            try {
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) { //ignore
            }
            executor.shutdownNow();
        }
        if (registryFuture != null) registryFuture.cancel(true);
        if (registryService != null) {
            registryService.shutdown();
            try {
                registryService.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {//ignore
            }
            registryService.shutdownNow();
        }
        if (scheduledFuture != null) scheduledFuture.cancel(true);
        if (scheduledService != null) {
            scheduledService.shutdown();
            try {
                scheduledService.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {//ignore
            }
            scheduledService.shutdownNow();
        }
        logger.info("=================gatherFile stop=================");
    }

    private List<Path> resolvePaths(String paths) {
        List<Path> pathList = new ArrayList<>();
        String[] s_paths = paths.split(",");
        for (String s_path : s_paths) {
            File file = Paths.get(s_path).toFile();
            String filter = "*";
            if (file.getName().contains("*")) {
                filter = file.getName();
                file = file.getParentFile();
            }
            if (!file.exists()) {
                logger.warn(file + " does not exit...");
                continue;
            }
            if (file.isFile()) {
                pathList.add(file.toPath());
                continue;
            }
            if (file.isDirectory()) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(file.toPath(), filter)) {
                    for (Path p : stream) if (p.toFile().isFile()) pathList.add(p);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return pathList;
    }

    private class GatherImpl implements Runnable {

        private ExecutorService executor;
        private Object[] paths;

        private GatherImpl(ExecutorService executor, Object... paths) {
            this.executor = executor;
            this.paths = paths;
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            logger.debug("once gather start...");
            List<Path> paths;
            if (!"lazy".equals(pathMode)) paths = resolvePaths((String) this.paths[0]);
            else paths = Arrays.asList((Path[]) this.paths);
            List<Future<Record>> results = new ArrayList<>(paths.size());
            for (Path path : paths) {
                if (path.toFile().exists()) results.add(executor.submit(new FileWatcher(path)));
                else {
                    logger.warn(path + " does not exit...");
                    cache.remove(path.toString());
                }
            }
            for (Future<Record> r : results) {
                try {
                    Record record = r.get();
                    String value = record.getLastTime();
                    if (!value.isEmpty()) {
                        String key = record.getPath().toString();
                        if (!cache.containsKey(key) || !value.equals(cache.get(key))) {
                            try {
                                output.apply(key, Files.readAllBytes(Paths.get(key)));
                                cache.put(key, value);
                            } catch (Throwable e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            logger.debug("once gather finish...");
        }
    }
}
