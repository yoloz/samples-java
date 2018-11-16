package server.fs.listener;


import Context;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 */
public class JobFileL extends DefaultListener {

    private final Logger logger = Logger.getLogger(this.getClass());

    /**
     * File system observer started checking event.
     *
     * @param observer The file system observer
     */
    @Override
    public void onStart(FileAlterationObserver observer) {
        logger.debug("JobFileL listener start......");
    }

    /**
     * File created Event.
     *
     * @param file The file created
     */
    @Override
    public void onFileCreate(File file) {
        mapToZk(file);
        logger.debug("fileCreate:" + file.getAbsolutePath());
    }

    /**
     * Directory deleted Event.
     *
     * @param directory The directory deleted
     */
    @Override
    public void onDirectoryDelete(File directory) {
        this.deleteToZK(directory);
        logger.debug("directoryDelete:" + directory.getAbsolutePath());
    }

    /**
     * File deleted Event.
     *
     * @param file The file deleted
     */
    @Override
    public void onFileDelete(File file) {
        deleteToZK(file);
        delUiJobFile(file);
        logger.debug("fileDelete:" + file.getAbsolutePath());
    }

    /**
     * File changed Event.
     *
     * @param file The file changed
     */
    @Override
    public void onFileChange(File file) {
        mapToZk(file);
        logger.debug("fileChange:" + file.getAbsolutePath());
    }

    /**
     * File system observer finished checking event.
     *
     * @param observer The file system observer
     */
    @Override
    public void onStop(FileAlterationObserver observer) {
        logger.debug("JobFileL listener stop......");
    }

    private void delUiJobFile(File file) {
        Context context = Context.getInstance();
        String serviceId = analyServiceId(file);
        Path uiPath = Paths.get(context.getUiJobsDir(), serviceId);
        Path uedPath = Paths.get(context.getUiJobsDir(), serviceId + ".UED");
        try {
            if (Files.exists(uedPath)) {
                logger.debug("del file: " + uiPath);
                FileUtils.forceDelete(uedPath.toFile());
            }
            if (Files.exists(uiPath)) {
                logger.debug("del file: " + uiPath);
                FileUtils.forceDelete(uiPath.toFile());
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private String analyServiceId(File file) {
        if (file.getName().endsWith(".UED")) {
            return file.getName().replace(".UED", "");
        } else { //.utx/.ujx
            Path path = file.toPath();
            return path.getName(path.getNameCount() - 2).toString();
        }
    }
}