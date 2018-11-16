package server.fs;

import Context;
import server.fs.listener.DatFileL;
import zk.ZkClient;
import org.junit.Test;


/**
 *
 */
public class LocalFSMonitorTest {
    @Test
    public void run() throws Exception {
    }

    @Test
    public void isStop() throws Exception {
    }

    public static void main(String[] args) {
        Context context = Context.getInstance();
        context.init("/home/ethan/testData1", "114", "","");
        context.setZkClient(new ZkClient("UTL-164"));
        LocalFSMonitor localFSMonitor = new LocalFSMonitor(new DatFileL(), "/home/ethan/testData1/data");
        new Thread(localFSMonitor).start();
        while (true) {
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(localFSMonitor.isError());
            if (localFSMonitor.isError()) {
                localFSMonitor = new LocalFSMonitor(new DatFileL(), "/home/ethan/testData1/data","/home/ethan/testData1/conf");
                new Thread(localFSMonitor).start();
            }
        }


    }
}