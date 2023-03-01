package com.yoloz.sample.hive;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class HiveDemo {

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

    private void kerberos() {

        Connection con = null;
        String JDBC_DB_URL = "jdbc:hive2://10.68.23.190:10000/;principal=hive/suse190.bigdata@UNIMAS.COM";
        try {
            //设置系统属性,用于加载的kdc服务器的相关信息的配置
            System.setProperty("java.security.krb5.conf", "E:\\projects\\GitHub\\BigData\\HDatabase\\resources\\krb5.conf");
            Configuration conf = new Configuration();
            conf.setBoolean("hadoop.security.authorization", true);
            conf.set("hadoop.security.authentication", "kerberos");
            UserGroupInformation.setConfiguration(conf);
            UserGroupInformation.loginUserFromKeytab("hive/suse190.bigdata@UNIMAS.COM", "E:\\projects\\GitHub\\BigData\\HDatabase\\resources\\hive.service.keytab");

            Class.forName("org.apache.hive.jdbc.HiveDriver");
            con = DriverManager.getConnection(JDBC_DB_URL, "admin", "admin");
            try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("show tables")) {
                while (rs.next()) {
                    System.out.println(rs.getString(1));
                }
            }
        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String[] regexProp(String uri, String key) {
        String[] result = {"", ""};
        Pattern p = Pattern.compile("(" + key + ")=([^;]*)[;]?");
        Matcher m = p.matcher(uri);
        while (m.find()) {
            result[0] = m.group();
            result[1] = m.group(2);
        }
        return result;
    }

    private void kerberosByUrl() {
        Connection con = null;
        String url = "jdbc:hive2://suse190.bigdata:2181,suse191.bigdata:2181,suse192.bigdata:2181/default;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2;auth=KERBEROS;principal=hive/_HOST@UNIMAS.COM;user.principal=hive/suse190.bigdata@UNIMAS.COM;user.keytab=E:\\hive_kerb\\hive.service.keytab;user.krb5conf=E:\\hive_kerb\\krb5.conf";
        String[] keytabPrincipal = regexProp(url, "user.principal");
        String[] keytabFile = regexProp(url, "user.keytab");
        String[] krb5Conf = regexProp(url, "user.krb5conf");
        try {
            //设置系统属性,用于加载的kdc服务器的相关信息的配置
            System.setProperty("java.security.krb5.conf", krb5Conf[1]);
            Configuration conf = new Configuration();
            conf.setBoolean("hadoop.security.authorization", true);
            conf.set("hadoop.security.authentication", "kerberos");
            UserGroupInformation.setConfiguration(conf);
            UserGroupInformation.loginUserFromKeytab(keytabPrincipal[1], keytabFile[1]);
            String JDBC_DB_URL = url.replace(keytabPrincipal[0], "").replace(keytabFile[0], "").replace(krb5Conf[0], "");
            Class.forName("org.apache.hive.jdbc.HiveDriver");
            con = DriverManager.getConnection(JDBC_DB_URL, "admin", "admin");
            try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("show tables")) {
                while (rs.next()) {
                    System.out.println(rs.getString(1));
                }
            }
        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void noSecure() {
        String driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";
        try {
            Class.forName(driverName);
            Connection conn = DriverManager.getConnection("jdbc:hive://cloudera132:10000/default", "", "");
            Statement stmt = conn.createStatement();
//            String sql="create table trade(key string,p_name string,p_addr string,g_id string,g_name string,g_desc string,costs int ) " +
//                    "STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' with " +
//                    "SERDEPROPERTIES(\"hbase.columns.mapping\" = \":key,info:p_name,info:p_addr,info:g_id,info:g_name,info:g_desc,info:costs\")" +
//                    "tblproperties(\"hbase.table.name\" = \"trade\")";
            String sql = "select g_name,costs from trade where key between '340625421' and '340635421'";
//            String sql = "select g_name,costs from trade where key ='340323102'";
            long t1 = System.currentTimeMillis();
            ResultSet resultSet = stmt.executeQuery(sql);
            //select * from queryTest where rowkey<'340111122'    create table test(id INT,name STRING)
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1));
            }
            long t2 = System.currentTimeMillis();
            System.out.println("耗时: " + (t2 - t1));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
        HiveDemo hiveDemo = new HiveDemo();
        hiveDemo.kerberosByUrl();
    }
}
