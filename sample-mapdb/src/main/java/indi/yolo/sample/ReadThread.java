package indi.yolo.sample;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.IndexTreeList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yolo
 */
public class ReadThread implements Runnable {

    long lastTime = 0;
    private DB db;


    public ReadThread() {
    }

    @Override
    public void run() {
        if (!App.DBFile.toFile().exists()) {
            System.out.println("file is not exit...");
            return;
        }
        try {
            long time = ((FileTime) Files.getAttribute(App.DBFile, "lastModifiedTime")).toMillis();
            if (lastTime != time) {
                lastTime = time;
                System.out.println("file changed...");
                if (db != null) {
                    db.close();
                    System.out.println("close db...");
                }
            }
            if (db == null || db.isClosed()) {
                if (db == null) {
                    System.out.println("db is null, init read db...");
                } else {
                    System.out.println("db is closed, init read db...");
                }
                db = DBMaker
                        .fileDB(App.DBFile.toString())
                        .fileChannelEnable()
                        .fileMmapEnableIfSupported()
                        .closeOnJvmShutdown()
                        .readOnly()
                        .make();
            } else {
                System.out.println(" db is not change...");
            }
            if (db.exists("test")) {
                IndexTreeList<TestBean> dbList = db.indexTreeList("test", new TestSerializer<>()).open();
                List<TestBean> list = new ArrayList<>(dbList);
                System.out.println(list);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
