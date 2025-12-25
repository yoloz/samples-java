package indi.yoloz.sample.utils;//docs.oracle.com/javase/tutorial/essential/io/notification.html

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 监控文件系统变化，但是只能这个目录下的文件变化，如果有多级目录，下层目录下的文件变化没有监听
 * <p>
 * 17-5-12
 */

public class FilesWatcher implements Runnable {
//    private final Logger logger = Logger.getLogger(FilesWatcher.class);

    private boolean stop = false;
    private List<FileWatcher> watchers;
    //    private List<WatchService> watchServices;
    //    private ExecutorService executor = Executors.newCachedThreadPool();
    private String[] paths;


    public FilesWatcher(String... paths) {
        this.paths = paths;
        this.watchers = new ArrayList<>(5);
//        this.watchServices = new ArrayList<>(5);
    }

    @Override
    public void run() {
        try {
            for (String sp : paths) {
                Path path = Paths.get(sp);
                if (Files.notExists(path)) {
                    if (Files.isDirectory(path)) {
                        Files.createDirectories(path);
                    } else {
                        Files.createFile(path);
                    }
                }
                System.out.println("start watcher path " + path);
                FileWatcher fileWatcher = new FileWatcher(path);
                this.watchers.add(fileWatcher);
                fileWatcher.start();
            }
            while (!this.stop) {
                Thread.sleep(5000);
                for (FileWatcher watcher : this.watchers) {
                    if (watcher.error) {
                        this.watchers.forEach(FileWatcher::close);
                        this.stop = true;
                        break;
                    }
                }
            }
            System.out.println("files watcher is dead.......");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            this.stop = true;
        }
    }


    public boolean isStop() {
        return stop;
    }

    private class FileWatcher extends Thread {

        boolean error;
        String name;
        Path path;
        WatchService watchService;

        FileWatcher(Path path) throws IOException {
            this.name = "thread-watcher-" + path;
            this.path = path;
            this.watchService = FileSystems.getDefault().newWatchService();
        }

//        FileWatcher(Path path, WatchService watchService) {
//            this.name = "thread-watcher-" + path;
//            this.path = path;
//            this.watchService = watchService;
//        }
//
//        FileWatcher(String name, Path path) throws IOException {
//            this.name = name;
//            this.path = path;
//            this.watchService = FileSystems.getDefault().newWatchService();
//        }

        @Override
        public void run() {
            try {
                this.path.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
                while (!error) {
                    WatchKey key = this.watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        System.out.println(event.kind());
                        Path ep = (Path) event.context();
                        ep = path.resolve(ep);
                        FileTime fileTime = FileTime.fromMillis(0);
                        if (event.kind() != StandardWatchEventKinds.ENTRY_DELETE) {
                            fileTime = (FileTime) Files.getAttribute(ep.toAbsolutePath(), "lastModifiedTime");
                        }
                        System.out.println(ep.toAbsolutePath() + " => " + fileTime);
                    }
                    key.reset();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                error = true;
            } finally {
                this.close();
            }
        }

        void close() {
            if (this.watchService != null) {
                try {
                    System.out.println("close " + name + " watcher service....");
                    this.watchService.close();
                    this.watchService = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void main(String[] args) {
        FilesWatcher filesWatcher = new FilesWatcher("/XXX/ethan/projects", "/XXX/ethan/test");
        new Thread(filesWatcher).start();
        while (true) {
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(filesWatcher.isStop());
            if (filesWatcher.isStop()) {
                filesWatcher = new FilesWatcher("/XXX/ethan/projects", "/XXX/ethan/test");
                new Thread(filesWatcher).start();
            }
        }


    }

}
