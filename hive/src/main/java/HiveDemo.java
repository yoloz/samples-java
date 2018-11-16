
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HiveDemo {

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
