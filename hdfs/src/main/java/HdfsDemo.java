
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

public class HdfsDemo {

    private Configuration conf;
    private FileSystem fSystem; /* HDFS file system */
    private String DEST_PATH = "/user/hdfs-examples";
    private String FILE_NAME = "test.txt";

    /**
     * HDFS operator instance
     *
     * @throws IOException
     */
    public void examples() {
        try {
            confLoad();    //Add configuration file
            authentication();    //kerberos security authentication
            instanceBuild();    //build HDFS instance
        } catch (IOException e) {
            System.err.println("Init hdfs filesystem failed.");
            e.printStackTrace();
            System.exit(1);
        }
        // operator file system
        try {
            // create directory
            mkdir();
            // write file
            write();
            // append file
            append();
            // read file
            read();
            // delete file
            delete();
            // delete directory
            delDir();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Add configuration file
     */
    private void confLoad() throws IOException {
        conf = new Configuration();
        conf.addResource(new Path(System.getProperty("user.dir")
                + File.separator + "conf" + File.separator + "hdfs-site.xml"));
        conf.addResource(new Path(System.getProperty("user.dir")
                + File.separator + "conf" + File.separator + "core-site.xml"));
    }

    /**
     * kerberos security authentication
     */
    private void authentication() throws IOException {
        if ("kerberos".equalsIgnoreCase(conf.get("hadoop.security.authentication"))) {
            // kerberos path
            String krbfilepath = System.getProperty("user.dir") + File.separator + "conf" + File.separator + "krb5.conf";
            System.setProperty("java.security.krb5.conf", krbfilepath);
            //login
            UserGroupInformation.setConfiguration(conf);
            UserGroupInformation.loginUserFromKeytab("hdfstest@HADOOP.COM",
                    System.getProperty("user.dir") + File.separator + "conf" + File.separator + "user.keytab");
        }
    }


    /**
     * get filesystem
     * build HDFS instance
     */
    private void instanceBuild() throws IOException {
        try {
            fSystem = FileSystem.get(conf);
        } catch (IOException e) {
            throw new IOException("Get fileSystem failed.");
        }
    }

    public void copyFromLocal(String src, String dst) throws IOException {
        fSystem.copyFromLocalFile(new Path(src), new Path(dst));
        fSystem.close();
    }

    public void copyToLocal(String src, String dst) throws IOException {
        fSystem.copyToLocalFile(new Path(src), new Path(dst));
        fSystem.close();
    }

    /**
     * delete directory
     */
    private void delDir() {
        Path destPath = new Path(DEST_PATH);
        if (!deletePath(destPath)) {
            System.err.println("failed to delete destPath " + DEST_PATH);
            return;
        }

        System.out.println("succee to delete path " + DEST_PATH);

    }

    /**
     * create directory
     */
    private void mkdir() {
        Path destPath = new Path(DEST_PATH);
        if (!createPath(destPath)) {
            System.err.println("failed to create destPath " + DEST_PATH);
            return;
        }
        System.out.println("succee to create path " + DEST_PATH);
    }

    /**
     * create file,write file
     */
    private void write() throws Exception {
        final String content = "hi, I am bigdata. It is successful if you can see me.";
        InputStream in = new ByteArrayInputStream(content.getBytes());
        try {
            HdfsWriter writer = new HdfsWriter(fSystem, DEST_PATH + File.separator + FILE_NAME);
            writer.doWrite(in);
            System.out.println("success to write.");
        } catch (IOException e2) {
            e2.printStackTrace();
            System.err.println("failed to write.");
        } finally {
            close(in);
        }
    }

    /**
     * append file content
     */
    private void append() throws Exception {
        final String content = "I append this content.";
        InputStream in = new ByteArrayInputStream(content.getBytes());
        try {
            HdfsWriter writer = new HdfsWriter(fSystem, DEST_PATH + File.separator + FILE_NAME);
            writer.doAppend(in);
            System.out.println("success to append.");
        } catch (IOException e2) {
            e2.printStackTrace();
            System.err.println("failed to append.");
        } finally {
            close(in);
        }
    }

    /**
     * read file
     */
    private void read() throws IOException {
        String strPath = DEST_PATH + File.separator + FILE_NAME;
        Path path = new Path(strPath);
        FSDataInputStream in = null;
        BufferedReader reader = null;
        StringBuilder strBuffer = new StringBuilder();
        try {
            in = fSystem.open(path);
            reader = new BufferedReader(new InputStreamReader(in));
            String sTempOneLine;
            // write file
            while ((sTempOneLine = reader.readLine()) != null) {
                strBuffer.append(sTempOneLine);
            }
            System.out.println("result is : " + strBuffer.toString());
            System.out.println("success to read.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("failed to read.");
        } finally {
            close(reader);
            close(in);
        }
    }

    /**
     * delete file
     */
    private void delete() throws IOException {
        Path beDeletedPath = new Path(DEST_PATH + File.separator + FILE_NAME);
        try {
            fSystem.deleteOnExit(beDeletedPath);
            System.out.println("succee to delete the file " + DEST_PATH + File.separator + FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("failed to delete the file " + DEST_PATH + File.separator + FILE_NAME);
        }
    }

    /**
     * close stream
     */
    private void close(Closeable stream) {
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * create file path
     */
    private boolean createPath(final Path filePath) {
        try {
            if (!fSystem.exists(filePath)) {
                fSystem.mkdirs(filePath);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * delete file path
     */
    private boolean deletePath(final Path filePath) {
        try {
            if (!fSystem.exists(filePath)) {
                return true;
            }
            fSystem.delete(filePath, true);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * HDFS客户端的权限错误：Permission denied
     * 1,将当前运行用户修改为hdfs系统有权限的用户；
     * 2,使用HDFS的命令行修改相应目录或文件的权限；
     * 3,在系统的环境变量或java环境变量里面添加HADOOP_USER_NAME;
     * 4,使用impersonate机制来解决(类似linux的sudo), 在系统的环境变量或java环境变量里面添加HADOOP_PROXY_USER;
     */
    public static void main(String[] args) {
        HdfsDemo demo = new HdfsDemo();
//        demo.examples();
        demo.conf = new Configuration();
        demo.conf.addResource(new Path("E:\\bigdata\\config\\hdfs\\hdfs-site.xml"));
        demo.conf.addResource(new Path("E:\\bigdata\\config\\hdfs\\core-site.xml"));
        try {
            System.setProperty("HADOOP_USER_NAME", "unimas");
            System.setProperty("HADOOP_PROXY_USER", "unimas");
            demo.fSystem = FileSystem.get(demo.conf);
            demo.append();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class HdfsWriter {

        private FSDataOutputStream hdfsOutStream;

        private BufferedOutputStream bufferOutStream;

        private FileSystem fSystem;
        private String fileFullName;

        public HdfsWriter(FileSystem fSystem, String fileFullName) throws Exception {
            if ((null == fSystem) || (null == fileFullName)) {
                throw new Exception("some of input parameters are null.");
            }
            this.fSystem = fSystem;
            this.fileFullName = fileFullName;
        }

        /**
         * append the inputStream to a file in HDFS
         */
        public void doWrite(InputStream inputStream) throws Exception {
            if (null == inputStream) {
                throw new Exception("some of input parameters are null.");
            }
            setWriteResource();
            try {
                outputToHDFS(inputStream);
            } finally {
                closeResource();
            }
        }

        /**
         * append the inputStream to a file in HDFS
         */
        public void doAppend(InputStream inputStream) throws Exception {
            if (null == inputStream) {
                throw new Exception("some of input parameters are null.");
            }
            setAppendResource();
            try {
                outputToHDFS(inputStream);
            } finally {
                closeResource();
            }
        }

        private void outputToHDFS(InputStream inputStream) throws IOException {
            final int countForOneRead = 10240; // 10240 Bytes each time
            final byte buff[] = new byte[countForOneRead];
            int count;
            while ((count = inputStream.read(buff, 0, countForOneRead)) > 0) {
                bufferOutStream.write(buff, 0, count);
            }
            bufferOutStream.flush();
            hdfsOutStream.hflush();
        }

        private void setWriteResource() throws IOException {
            Path filepath = new Path(fileFullName);
            hdfsOutStream = fSystem.create(filepath);
            bufferOutStream = new BufferedOutputStream(hdfsOutStream);
        }

        private void setAppendResource() throws IOException {
            Path filepath = new Path(fileFullName);
            hdfsOutStream = fSystem.append(filepath);
            bufferOutStream = new BufferedOutputStream(hdfsOutStream);
        }

        private void closeResource() {
            // close hdfsOutStream
            if (hdfsOutStream != null) {
                try {
                    hdfsOutStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // close bufferOutStream
            if (bufferOutStream != null) {
                try {
                    bufferOutStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
