package server.fs.listener;

import Context;
import zk.ZkCache;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

/**
 */
public class DatFileL extends DefaultListener {

    private final Logger logger = Logger.getLogger(this.getClass());
    private final String suffix = ".dat";

    /**
     * File system observer started checking event.
     *
     * @param observer The file system observer
     */
    @Override
    public void onStart(FileAlterationObserver observer) {
        logger.debug("DatFile1 listener start......");
    }

    /**
     * File created Event.
     *
     * @param file The file created
     */
    @Override
    public void onFileCreate(File file) {
        if (file.getName().endsWith(suffix)) {
            mapToZk(file);
            transFile(file);
            logger.debug("fileCreate:" + file.getAbsolutePath());
        }
    }

    /**
     * File changed Event.
     *
     * @param file The file changed
     */
    @Override
    public void onFileChange(File file) {
        if (file.getName().endsWith(suffix)) {
            mapToZk(file);
            transFile(file);
            logger.debug("fileChange:" + file.getAbsolutePath());
        }
    }

    /**
     * File system observer finished checking event.
     *
     * @param observer The file system observer
     */
    @Override
    public void onStop(FileAlterationObserver observer) {
        logger.debug("DatFile1 listener stop......");
    }


    private void transFile(File file) {
        Path path = file.toPath();
        String serviceId = path.getName(path.getNameCount() - 2).toString();
        int localNodeId = Context.getInstance().getLocalNodeId();
        if (Objects.equals(ZkCache.getInstance().getServiceLeader(serviceId), localNodeId)) {
            logger.debug("trans breakpoint file to follower......");
            ZkCache.getInstance().getAllNodes().forEach(node -> {
                if (!Objects.equals(node.getId(), localNodeId)) {
//                    TransUtil.transFile(node.getHost(),
//                            node.getPort() + "",
//                            file.getAbsolutePath());
                }
            });
        }
    }
}
