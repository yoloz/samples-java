
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.ReflectionUtils;
import org.apache.hadoop.hive.ql.io.RCFile;
import org.apache.hadoop.hive.serde2.ColumnProjectionUtils;
import org.apache.hadoop.hive.serde2.columnar.BytesRefArrayWritable;
import org.apache.hadoop.hive.serde2.columnar.BytesRefWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;

public class RCFDemo {

    private static final String TAB = "\t";
    private static String strings[] = {"1,true,123.123,2012-10-24 08:55:00",
            "2,false,1243.5,2012-10-25 13:40:00",
            "3,false,24453.325,2008-08-22 09:33:21.123",
            "4,false,243423.325,2007-05-12 22:32:21.33454",
            "5,true,243.325,1953-04-22 09:11:33 "};

    private static Configuration getConf(boolean kerberos) throws IOException {
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS", "hdfs://bigdata-205:8020/");
        if (kerberos) {
            System.setProperty("java.security.krb5.conf", "E:\\projects\\kerberos\\hdfs\\krb5.conf");
            configuration.set("hadoop.security.authentication", "kerberos");
            /**如果运行报错：java.lang.IllegalArgumentException: Failed to specify server's Kerberos principal name.则configuration添加core-site.xml和hdfs-site.xml
             *原因可参考:http://stackoverflow.com/questions/35325720/connecting-to-kerberrized-hdfs-java-lang-illegalargumentexception-failed-to-s
             I believe the problem is that HDFS expects the Configuration to have a value for dfs.datanode.kerberos.principal, which is the principal of the datanodes, and it is missing in this case.
             I had this same problem when I created a Configuration instance from only core-site.xml and forgot to add hdfs-site.xml. As soon as I added hdfs-site.xml it started working, and hdfs-site.xml had:
             <property>
             <name>dfs.datanode.kerberos.principal</name>
             <value>....</value>
             </property>
             */
            //configuration.addResource(new Path("E:\\projects\\kerberos\\hdfs\\hdfs-site.xml"));
            //configuration.addResource(new Path("E:\\projects\\kerberos\\hdfs\\core-site.xml"));
            UserGroupInformation.setConfiguration(configuration);
            UserGroupInformation.loginUserFromKeytab("hdfs-bigdata@UNIMAS.COM", "E:\\projects\\kerberos\\hdfs\\hdfs.headless.keytab");
        }
        return configuration;
    }

    private static CompressionCodec compress(String className,Configuration configuration) throws ClassNotFoundException {
        Class codecClass = Class.forName(className);
        return (CompressionCodec) ReflectionUtils.newInstance(codecClass,configuration);
    }
    private static void createRcFile(Path src, boolean kerberos) throws IOException, ClassNotFoundException {
        Configuration conf = getConf(kerberos);
        conf.setInt(RCFile.COLUMN_NUMBER_CONF_STR, 4);// 列数
        conf.setInt(RCFile.Writer.COLUMNS_BUFFER_SIZE_CONF_STR, 4 * 1024 * 1024);// 决定行数参数一
        conf.setInt(RCFile.RECORD_INTERVAL_CONF_STR, 3);// 决定行数参数二
        FileSystem fs = FileSystem.get(conf);
        RCFile.Writer writer = new RCFile.Writer(fs, conf, src);
//        RCFile.Writer writer = new RCFile.Writer(fs,conf,src,null, compress("org.apache.hadoop.io.compress.GzipCodec",conf));
        BytesRefArrayWritable cols = new BytesRefArrayWritable(4);// 列数,可以动态获取
        BytesRefWritable col = null;
        for (String s : strings) {
            String splits[] = s.split(",");
            int count = 0;
            for (String split : splits) {
                col = new BytesRefWritable(Bytes.toBytes(split), 0,
                        Bytes.toBytes(split).length);
                cols.set(count, col);
                count++;
            }
            writer.append(cols);
        }
        writer.close();
        fs.close();
    }

    private static void readRcFile(Path src, boolean kerberos) throws IOException {
        Configuration conf = getConf(kerberos);
        // 需要获取的列,必须指定,具体看ColumnProjectionUtils中的设置方法
        ColumnProjectionUtils.setReadAllColumns(conf);
        FileSystem fs = FileSystem.get(conf);
        RCFile.Reader reader = new RCFile.Reader(fs, src, conf);
        readerByRow(reader);
//        readerByCol(reader);
        reader.close();
    }

    public static void readerByRow(RCFile.Reader reader) throws IOException {
        // 已经读取的行数
        LongWritable rowID = new LongWritable();
        // 一个行组的数据
        BytesRefArrayWritable cols = new BytesRefArrayWritable();
        while (reader.next(rowID)) {
            reader.getCurrentRow(cols);
            System.out.println(getData(cols));
        }
    }

    public static void readerByCol(RCFile.Reader reader) throws IOException {
        // 一个行组的数据
        BytesRefArrayWritable cols = new BytesRefArrayWritable();
        while (reader.nextBlock()) {
            for (int count = 0; count < 4; count++) {
                cols = reader.getColumn(count, cols);
                System.out.println(getData(cols));
            }
        }
    }

    private static String getData(BytesRefArrayWritable cols) throws IOException {
        BytesRefWritable brw = null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cols.size(); i++) {
            brw = cols.get(i);
            // 根据start 和 length 获取指定行-列数据
            sb.append(Bytes.toString(brw.getData(), brw.getStart(),
                    brw.getLength()));
            if (i < cols.size() - 1) {
                sb.append(TAB);
            }
        }
        return sb.toString();
    }

    private static void delete(Path src, boolean kerberos) throws IOException {
        Configuration configuration = getConf(kerberos);
        FileSystem fs = FileSystem.get(configuration);
        fs.delete(src, true);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Path src = new Path("/rcfile.gz");
        createRcFile(src, true);
//        readRcFile(src, true);
//        delete(src,true);
    }
}
