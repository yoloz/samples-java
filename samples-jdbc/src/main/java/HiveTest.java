import org.apache.hadoop.security.UserGroupInformation;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * apacheHive,hpdHive,cdhHive暂时测试用户密码方式,kerberos方式可以统一格式
 * <br/>
 * hive创建表:
 * beeline>
 * create table t1(id int,name string,hobby array<string>,address map<String,string>)
 * row format delimited
 * fields terminated by ','
 * collection items terminated by '-'
 * map keys terminated by ':'
 * ;
 * create table t2(id int,name string,hobby string,address String)
 * row format delimited
 * fields terminated by ','
 * ;
 * <br/>
 * local file:
 * <br/>
 * 1,xiaoming,book-TV-code,beijing:chaoyang-shagnhai:pudong
 * 2,lilei,book-code,nanjing:jiangning-taiwan:taibei
 * 3,lihua,music-book,heilongjiang:haerbin
 * <br/>
 * 1,xiaoming,book,beijing
 * 2,lilei,tv,nanjing
 * 3,lihua,music,heilongjiang
 * <br/>
 * load data:
 * hive>
 * load data local inpath '/home/zeek/t1.txt' overwrite into table t1;
 * <br/>
 * text换成parquet:
 * <br/>
 * CREATE TABLE IF NOT EXISTS t1_parquet(id int,name string,hobby array<string>,add map<String,string>) stored AS PARQUET;
 * <br/>
 * insert overwrite table t1_parquet select * from t1;
 * 如果执行后报错：
 * Job Submission failed with exception 'org.apache.hadoop.security.AccessControlException
 * (Permission denied: user=anonymous, access=WRITE, inode="/user":hdfs:supergroup:drwxr-xr-x
 * 可退出再进入!connect jdbc:hive2://localhost:10000/default的时候输入用户hdfs(未配置安全策略)
 * <br/>
 * org.elasticsearch.xpack.sql.client.Version.class中加载驱动版本信息时对url末尾后缀判断
 * (
 * URL url = Version.class.getProtectionDomain().getCodeSource().getLocation();
 * String urlStr = url.toString();
 * if (urlStr.endsWith(".jar"))...
 * ),
 * 故
 * new URL("jar:file:" + f + "!/") ==> f.toURI().toURL()
 */

//https://github.com/timveil/hive-jdbc-uber-jar

//driverName ="org.apache.hive.jdbc.HiveDriver"

public class HiveTest {

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
        PropertyConfigurator.configure(System.getProperty("user.dir") + "/samples-jdbc/src/main/java/log4j.properties");
//        HiveTest hiveTest = new HiveTest();
//        hiveTest.kerberosTest();
//        hiveTest.impalaKerberosTest();

        String url = "jdbc:hive2://192.168.1.162:10000/test;principal=hive/cdh162@ZHDS.CO";
        Properties properties = new Properties();
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.set("hadoop.security.authentication", "Kerberos");
        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation.loginUserFromKeytab("test", "/data/yoloz/test.keytab");
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        try (Connection conn = DriverManager.getConnection(url, properties)) {
            Util.getCatalogs(conn);
            Util.getSchemas(conn);
            Util.getTables(conn, null, "test", "%", null);
            Util.getColumns(conn, null, "test", "t1", "%");
        }
    }

    /**
     * com.cloudera.hive.jdbc41.HS2Driver
     * HiveJDBC41.jar
     */
    public void cdhHiveTest() {
        String url = "jdbc:hive2://192.168.1.162:10000/default;AuthMech=1;" +
                "KrbRealm=ZHDS.CO;KrbHostFQDN=cdh160;" +
                "KrbServiceName=hive;KrbAuthType=2";
        try {
            Properties properties = new Properties();
            try (Connection conn = DriverManager.getConnection(url, properties);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("show databases")) {
                while (rs.next()) {
                    System.out.println(rs.getString(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * http://spark.apache.org/docs/latest/sql-distributed-sql-engine.html#running-the-thrift-jdbcodbc-server
     */
    public void sparkSQLTest() {
        String url = "jdbc:hive2://192.168.1.183:10015/default";
        try {
            Properties properties = new Properties();
            Class.forName("org.apache.hive.jdbc.HiveDriver");
            try (Connection conn = DriverManager.getConnection(url, properties);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("show databases")) {
                while (rs.next()) {
                    System.out.println(rs.getString(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * impala-shell -i hostname -d default
     */
    public void impalaKerberosTest() {
        String url = "jdbc:hive2://192.168.1.160:21050/test;principal=impala/cdh160@ZHDS.CO";
        try {
            Properties properties = new Properties();
            org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
            conf.set("hadoop.security.authentication", "Kerberos");
            UserGroupInformation.setConfiguration(conf);
            UserGroupInformation.loginUserFromKeytab("test", "/data/yoloz/test.keytab");
            Class.forName("org.apache.hive.jdbc.HiveDriver");
            try (Connection conn = DriverManager.getConnection(url, properties);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("show databases")) {
                while (rs.next()) {
                    System.out.println(rs.getString(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * bin/beeline
     * beeline> !connect jdbc:hive2://cdh162:10000/default;principal=hive/cdh162@ZHDS.CO;
     * <p>
     * 客户端每次获取fetchSize条数据
     */
    public void kerberosTest() {
        String url = "jdbc:hive2://192.168.1.162:10000/test;principal=hive/cdh162@ZHDS.CO";
        try {
            Properties properties = new Properties();
            org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
            conf.set("hadoop.security.authentication", "Kerberos");
            UserGroupInformation.setConfiguration(conf);
            UserGroupInformation.loginUserFromKeytab("test", "/data/yoloz/test.keytab");
            Class.forName("org.apache.hive.jdbc.HiveDriver");
            try (Connection conn = DriverManager.getConnection(url, properties);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("select * from t1")) {
                ResultSetMetaData rsmd = rs.getMetaData();
                while (rs.next()) {
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        //rsmd.getCatalogName(i),rsmd.getSchemaName(i), rsmd.getTableName(i), not support
                        System.out.println(String.join(",",
                                rsmd.getColumnLabel(i), rsmd.getColumnName(i), rsmd.getColumnTypeName(i),
                                //rsmd.getColumnDisplaySize(i), rsmd.getPrecision(i), rsmd.getScale(i), rsmd.getColumnType(i),
                                rs.getString(i)
                        ));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
