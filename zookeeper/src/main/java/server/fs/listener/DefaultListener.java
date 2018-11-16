package server.fs.listener;

import Context;
import zk.ZkClient;
import zk.ZkConfig;
import zk.ZkUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 */
public abstract class DefaultListener implements FileAlterationListener {

    private final Logger logger = Logger.getLogger(this.getClass());

    /**
     * File system observer started checking event.
     *
     * @param observer The file system observer
     */
    @Override
    public abstract void onStart(FileAlterationObserver observer);

    /**
     * Directory created Event.
     *
     * @param directory The directory created
     */
    @Override
    public void onDirectoryCreate(File directory) {
        logger.debug("directoryCreate: " + directory.getAbsolutePath());
    }

    /**
     * Directory changed Event.
     *
     * @param directory The directory changed
     */
    @Override
    public void onDirectoryChange(File directory) {
        logger.debug("directoryChange: " + directory.getAbsolutePath());
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
     * File created Event.
     *
     * @param file The file created
     */
    @Override
    public abstract void onFileCreate(File file);

    /**
     * File changed Event.
     *
     * @param file The file changed
     */
    @Override
    public abstract void onFileChange(File file);

    /**
     * File deleted Event.
     *
     * @param file The file deleted
     */
    @Override
    public void onFileDelete(File file) {
        this.deleteToZK(file);
        logger.debug("fileDelete:" + file.getAbsolutePath());
    }

    /**
     * File system observer finished checking event.
     *
     * @param observer The file system observer
     */
    @Override
    public abstract void onStop(FileAlterationObserver observer);

    private String getZkPath(File file) {
//        Path fileDir = file.toPath();
//        return ZkConfig.zk_pmMp + "/" +
//                Context.getInstance().getLocalNodeId() + "/" +
//                TransUtil.relativePath(null, fileDir.toString());
        return null;
    }

    void deleteToZK(File file) {
        ZkClient zkClient = Context.getInstance().getZkClient();
        String zkPath = getZkPath(file);
        try {
            if (zkClient.getCurator().checkExists().forPath(zkPath) != null) {
                ZkUtils.delete(zkClient, zkPath);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    void mapToZk(File file) {
        ZkClient zkClient = Context.getInstance().getZkClient();
        String zkPath = getZkPath(file);
        try {
            ZkUtils.createOrUpdate(zkClient, zkPath,
                    Files.getLastModifiedTime(file.toPath()).toMillis() + "",
                    CreateMode.EPHEMERAL);
        } catch (Exception e) {
            logger.error(e);
        }
    }

}
