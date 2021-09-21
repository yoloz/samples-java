package com.yoloz.sample.sigar.gather.file;

//import LocalLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.Callable;
//import java.util.logging.Logger;

public class FileWatcher implements Callable<Record> {

    //    private Logger logger = LocalLog.getLogger();
    private final Logger logger = LoggerFactory.getLogger(FileWatcher.class);
    private Path path;

    public FileWatcher(Path path) {
        this.path = path;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result file path
     */
    @Override
    public Record call() {
        try {
            logger.debug(Thread.currentThread().getName() + "-FileWatcher: " + path + "-" + System.currentTimeMillis());
            FileTime time = (FileTime) Files.getAttribute(path, "lastModifiedTime");
            return new Record(path, time.toString());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return new Record(path, "");
    }
}
