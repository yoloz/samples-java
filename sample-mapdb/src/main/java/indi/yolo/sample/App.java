package indi.yolo.sample;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * 验证一方在更新文件，一方读文件的同步效果
 */
public class App {

    static final Path DBFile = Paths.get(System.getProperty("java.io.tmpdir"), "mapdb.db");

    public static void main(String[] args) throws InterruptedException {
        if (DBFile.toFile().exists()) {
            boolean b = DBFile.toFile().delete();
            System.out.println("remove " + DBFile + ": " + b);
        }
        DB writeDB = DBMaker
                .fileDB(App.DBFile.toString())
                .fileChannelEnable()
                .fileMmapEnableIfSupported()
                .fileMmapPreclearDisable()
                .transactionEnable()
                .closeOnJvmShutdown()
                .make();
        int i = 0;
        ReadThread readThread = new ReadThread();
        WriteThread writeThread = new WriteThread(writeDB);
        readThread.run();
        while (i < 5) {
            writeThread.updateIndex(i);
            if (i == 2) {
                writeThread.remove(i - 1);
            } else if (i == 3) {
                writeThread.update(new TestBean("testA0", "testB0", "testC0"), new TestBean("testA2", "testB2", "testC2"));
            } else {
                writeThread.run();
            }
            TimeUnit.SECONDS.sleep(1);
            readThread.run();
            i++;
        }
        readThread.run();
    }
}
