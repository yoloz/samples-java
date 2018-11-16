package server.fs;


import Context;
import zk.ZkClient;
import zk.ZkUtils;

/**
 *
 */
public class LocalFSInitTest {
//    @Test
//    public void iterate() throws Exception {
//    }

    public static void main(String[] args) {
        Context context = Context.getInstance();
        context.init("/home/ethan/testData1", "114", "","");
        context.setZkClient(new ZkClient("UTL-164"));
        try {
            ZkClient zkClient = context.getZkClient();
            String zkPath = "/home/ethan/testData1/data";
            System.out.println((zkClient.getCurator().checkExists().forPath(zkPath) == null));
            ZkUtils.delete(zkClient, zkPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        new LocalFSInit(new DatFile1(), "/home/ethan/testData1/data").start();
//        try {
//            Thread.sleep(Integer.MAX_VALUE);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        context.getZkClient().close();
    }
}