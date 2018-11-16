package server.fs.vistor;

import Context;
import zk.ZkClient;
import zk.ZkConfig;
import zk.ZkUtils;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 *
 */
public abstract class DefaultVistor extends SimpleFileVisitor<Path> {

    private final Logger logger = Logger.getLogger(this.getClass());

    /**
     * Invoked for a directory before entries in the directory are visited.
     * <p>
     * <p> Unless overridden, this method returns {@link FileVisitResult#CONTINUE
     * CONTINUE}.
     */
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        return FileVisitResult.CONTINUE;
    }

    /**
     * Invoked for a file in a directory.
     * <p>
     * <p> Unless overridden, this method returns {@link FileVisitResult#CONTINUE
     * CONTINUE}.
     */
    @Override
    public abstract FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException;

    /**
     * Invoked for a file that could not be visited.
     * <p>
     * <p> Unless overridden, this method re-throws the I/O exception that prevented
     * the file from being visited.
     */
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        logger.error(exc);
        return FileVisitResult.CONTINUE;
//        throw exc;
    }

    /**
     * Invoked for a directory after entries in the directory, and all of their
     * descendants, have been visited.
     * <p>
     * <p> Unless overridden, this method returns {@link FileVisitResult#CONTINUE
     * CONTINUE} if the directory iteration completes without an I/O exception;
     * otherwise this method re-throws the I/O exception that caused the iteration
     * of the directory to terminate prematurely.
     */
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (exc != null) logger.error(exc);
//            throw exc;
        return FileVisitResult.CONTINUE;
    }

    private String getZkPath(Path file) {
//        return ZkConfig.zk_pmMp + "/" +
//                Context.getInstance().getLocalNodeId() + "/" +
//                TransUtil.relativePath(null, file.toString());
        return null;
    }

    void mapToZk(Path file, long millis) throws Exception {
        ZkClient zkClient = Context.getInstance().getZkClient();
        String zkPath = getZkPath(file);
        ZkUtils.createOrUpdate(zkClient, zkPath,
                millis + "",
                CreateMode.EPHEMERAL);
    }
}
