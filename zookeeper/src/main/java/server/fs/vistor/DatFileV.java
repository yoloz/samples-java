package server.fs.vistor;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 *
 */
public class DatFileV extends DefaultVistor {

    private final Logger logger = Logger.getLogger(this.getClass());

    /**
     * Invoked for a file in a directory.
     * <p>
     * <p> Unless overridden, this method returns {@link FileVisitResult#CONTINUE
     * CONTINUE}.
     *
     * @param file  file path
     * @param attrs file attr
     */
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (file.toFile().getName().endsWith(".dat")) {
            try {
                mapToZk(file, attrs.lastModifiedTime().toMillis());
                logger.debug("visit file: " + file);
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return FileVisitResult.CONTINUE;
    }
}
