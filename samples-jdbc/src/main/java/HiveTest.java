import org.apache.hadoop.security.UserGroupInformation;

import java.sql.*;
import java.util.Properties;

/**
 * apacheHive,hpdHive,cdhHive暂时测试用户密码方式,kerberos方式可以统一格式
 * <p>
 * hive创建表:
 * create table t1(id int,name string,hobby array<string>,add map<String,string>)
 * row format delimited
 * fields terminated by ','
 * collection items terminated by '-'
 * map keys terminated by ':'
 * ;
 * <p>
 * local file:
 * <p>
 * 1,xiaoming,book-TV-code,beijing:chaoyang-shagnhai:pudong
 * 2,lilei,book-code,nanjing:jiangning-taiwan:taibei
 * 3,lihua,music-book,heilongjiang:haerbin
 * load data:
 * load data local inpath '/home/zeek/t1' overwrite into table t1;
 * <p>
 * text换成parquet:
 * <p>
 * create table t1_par(id int,name string,hobby array<string>,add map<String,string>)
 * row format delimited
 * stored as PARQUET;
 * <p>
 * insert overwrite table t1_par select * from t1;
 * 如果执行后报错：
 * Job Submission failed with exception 'org.apache.hadoop.security.AccessControlException
 * (Permission denied: user=anonymous, access=WRITE, inode="/user":hdfs:supergroup:drwxr-xr-x
 * 可退出再进入!connect jdbc:hive2://localhost:10000/default的时候输入用户hdfs(未配置安全策略)
 */

//https://github.com/timveil/hive-jdbc-uber-jar

//driverName ="org.apache.hive.jdbc.HiveDriver"

public class HiveTest {

    public static void main(String[] args) throws ClassNotFoundException {
        HiveTest hiveTest = new HiveTest();
        hiveTest.query();
    }

    /**
     * bin/beeline
     * beeline> !connect jdbc:hive2://localhost:10000/default
     * <p>
     * 客户端每次获取fetchSize条数据
     */
    public void query() throws ClassNotFoundException {
        String url = "jdbc:hive2://192.168.1.183:10000/default";
        Properties properties = new Properties();
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        try (Connection conn = DriverManager.getConnection(url, properties);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select * from test")) {
            System.out.println(stmt.getResultSetType() == ResultSet.TYPE_FORWARD_ONLY);
            System.out.println(rs.getFetchSize());  //defaultSize from server,initSize from jdbc url
            ResultSetMetaData rsmd = rs.getMetaData();
            int size = rsmd.getColumnCount();
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i <= size; i++) {
                builder.append(rsmd.getColumnLabel(i));
                if (i != size) {
                    builder.append(",");
                }
            }
            System.out.println(builder.toString());
            builder.setLength(0);
            while (rs.next()) {
                for (int i = 1; i <= size; i++) {
//                    System.out.println(rsmd.getColumnLabel(i));
//                    System.out.println(rsmd.getColumnName(i));
//                    System.out.println(rsmd.getColumnTypeName(i));
//                    System.out.println(rsmd.getColumnDisplaySize(i));
//                    System.out.println(rsmd.getPrecision(i));
//                    System.out.println(rsmd.getScale(i));
//                    System.out.println(rsmd.getColumnType(i));
//                    System.out.println(rs.getString(i));
                    builder.append(rs.getObject(i));
                    if (i != size) {
                        builder.append(",");
                    }
                }
                System.out.println(builder.toString());
                builder.setLength(0);
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

    public void impalaKerberosTest() {
        String url = "jdbc:hive2://10.68.120.2:21050/default;principal=impala/cdh2.com@CDHKDC.COM";
        try {
            Properties properties = new Properties();
            org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
            conf.set("hadoop.security.authentication", "Kerberos");
            UserGroupInformation.setConfiguration(conf);
            UserGroupInformation.loginUserFromKeytab("wufang", "/tmp/wufang.keytab");
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

    public void kerberosTest() {
        String url = "jdbc:hive2://127.0.0.1:10000/default;principal=hive/cdh1.com@CDHKDC.COM";
        try {
            Properties properties = new Properties();
            org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
            conf.set("hadoop.security.authentication", "Kerberos");
            UserGroupInformation.setConfiguration(conf);
            UserGroupInformation.loginUserFromKeytab("wufang", "/tmp/wufang.keytab");
            Class.forName("org.apache.hive.jdbc.HiveDriver");
            try (Connection conn = DriverManager.getConnection(url, properties);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("select * from customers where 1=0")) {
                ResultSetMetaData rsmd = rs.getMetaData();
                while (rs.next()) {
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
//                    System.out.println(rsmd.getCatalogName(i));
//                    System.out.println(rsmd.getSchemaName(i));
//                    System.out.println(rsmd.getTableName(i));
//                    System.out.println(rsmd.getColumnLabel(i));
                        System.out.println(rsmd.getColumnName(i));
                        System.out.println(rsmd.getColumnTypeName(i));
//                    System.out.println(rsmd.getColumnDisplaySize(i));
//                    System.out.println(rsmd.getPrecision(i));
//                    System.out.println(rsmd.getScale(i));
                        System.out.println(rsmd.getColumnType(i));
                        System.out.println(rs.getString(i));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
