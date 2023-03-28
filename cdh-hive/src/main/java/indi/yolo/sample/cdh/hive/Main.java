package indi.yolo.sample.cdh.hive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author ${USER}
 */
public class Main {

    public static void main(String[] args) {
        Main main = new Main();
//        main.kerberos();
        main.noSasl();
//        main.userAndPwd();
    }

    static void print(ResultSet resultSet) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int colCount = resultSetMetaData.getColumnCount();
        int total = 0;
        StringBuilder header = new StringBuilder();
        while (resultSet.next()) {
            StringBuilder line = new StringBuilder();
            for (int i = 1; i <= colCount; i++) {
                if (total == 0) {
                    header.append(resultSetMetaData.getColumnLabel(i)).append(" ");
                }
                line.append(resultSet.getObject(i)).append(',');
            }
            if (total == 0) {
                System.out.println(header.toString());
            }
            System.out.println(line.substring(0, line.length() - 1));
            total++;
        }
        System.out.println("==========" + total + "============");
    }


    /**
     * com.cloudera.hive.jdbc41.HS2Driver
     * HiveJDBC41.jar
     */
    public void kerberos() {
        //principal: hive/cdh162@ZHDS.CO
        String url = "jdbc:hive2://192.168.124.162:10000/test;AuthMech=1;" +
                "KrbRealm=ZHDS.CO;KrbHostFQDN=cdh162;" +
                "KrbServiceName=hive;LogLevel=6;LogPath=/home/yoloz/kblog";
        try {
            Properties properties = new Properties();
            try (Connection conn = DriverManager.getConnection(url, properties);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("desc t1")) {
//                System.out.println(conn.getCatalog() + "," + conn.getSchema());
                while (rs.next()) {
                    print(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void noSasl() {
        String url = "jdbc:hive2://192.168.124.236:10000/default;AuthMech=0;transportMode=binary;";
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

    public void userAndPwd() {
        String url = "jdbc:hive2://192.168.124.236:10000/default;AuthMech=3;UID=test;PWD=test;";
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
}