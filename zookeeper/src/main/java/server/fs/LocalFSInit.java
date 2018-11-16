package server.fs;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class LocalFSInit {

    private final Logger logger = Logger.getLogger(LocalFSInit.class);
//    private boolean stop = false;
    private Map<String, FileVisitor<Path>> paths;

//    public LocalFSInit(String... paths) {
//        this(new DatFile1(), paths);
//    }

    public LocalFSInit(FileVisitor<Path> fileVisitor, String... paths) {
        this.paths = new HashMap<>(paths.length);
        for (String path : paths) {
            this.paths.put(path, fileVisitor);
        }
    }

    public LocalFSInit(Map<String, FileVisitor<Path>> paths) {
        this.paths = paths;
    }

    public void start() {
        paths.forEach((k, v) -> {
            try {
                Files.walkFileTree(Paths.get(k), v);
            } catch (IOException e) {
                logger.error(e);
            }
        });
    }

}
