package indi.yolo.sample.sigar.gather.file;

import java.nio.file.Path;

public class Record {

    private Path path;
    private String lastTime; //YYYY-MM-DDThh:mm:ss[.s+]Z

    Record(Path path, String lastTime) {
        this.path = path;
        this.lastTime = lastTime;
    }

    public Path getPath() {
        return path;
    }

    public String getLastTime() {
        return lastTime;
    }

    @Override
    public String toString() {
        return path + ":" + lastTime;
    }
}