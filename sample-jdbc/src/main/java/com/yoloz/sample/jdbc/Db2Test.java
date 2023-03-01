package com.yoloz.sample.jdbc;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Arrays;
import java.util.Properties;

/**
 * driver:com.ibm.db2.jcc.DB2Driver
 */
public class Db2Test {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
//        Class.forName("com.ibm.db2.jcc.DB2Driver");
//        Db2Test db2Test = new Db2Test();
//        db2Test.readStream();

        String url = "jdbc:db2://192.168.1.133:50000/mydata";
        Properties properties = new Properties();
        properties.put("user","db2inst1");
        properties.put("password","fhcs2019");
//        try (Connection conn = DriverManager.getConnection(url, "db2inst1", "fhcs2019")) {
        try (Connection conn = DriverManager.getConnection(url, properties)) {
//            Util.getCatalogs(conn);
//            Util.getSchemas(conn);
//            Util.getTables(conn, null, "DB2INST1", "%", null);
//            Util.getColumns(conn, null, "DB2INST1", "PERSON", "%");
            Util.getColumn(conn,"select * from person");
        }
    }

    public void writeStream() {
        String url = "jdbc:db2://192.168.1.133:50000/mydata";
        String sql = "insert into blob_clob(id,b_lob,c_lob) values(?,?,?)";
        try (Connection conn = DriverManager.getConnection(url, "db2inst1", "");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, 1);
            stmt.setBlob(2,
                    Files.newInputStream(Paths.get(System.getProperty("user.home"), "test.doc")));
            stmt.setClob(3,
                    Files.newBufferedReader(Paths.get(System.getProperty("user.home"), "test.txt")));
            System.out.println(stmt.executeUpdate());
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }


    public void readStream() {
        String url = "jdbc:db2://192.168.1.133:50000/mydata";
        String sql = "select b_lob,c_lob from blob_clob where id=1";
        try (Connection conn = DriverManager.getConnection(url, "db2inst1", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                writeBlob(rs.getBinaryStream(1));
                writeClob(rs.getCharacterStream(2));
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void writeBlob(InputStream inputStream) throws IOException {
        if (inputStream == null) return;
        long length = 0L;
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(System.getProperty("user.home"),
                "test_down.doc"))) {
            int len;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes)) != -1) {
                length += len;
                outputStream.write(bytes, 0, len);
            }
        }
        System.out.println(length);
    }

    private void writeClob(Reader reader) throws IOException {
        if (reader == null) return;
        long length = 0L;
        try (Writer writer = Files.newBufferedWriter(Paths.get(System.getProperty("user.home"),
                "test_down.txt"))) {
            int len;
            char[] chars = new char[1024];
            while ((len = reader.read(chars)) != -1) {
                length += len;
                writer.write(chars, 0, len);
            }
        }
        System.out.println(length);
    }

    /**
     * 清空表数据:
     * 对于db2 9.7及以上版本，可以使用如下命令清空数据：
     * TRUNCATE TABLE table_name IMMEDIATE;
     * 对于db2 9.5，需要使用如下命令清空数据：
     * ALTER TABLE table_name ACTIVATE not logged initially ;
     * ALTER TABLE table_name ACTIVATE not logged initially with empty TABLE;
     */
    public void insertBatch() {
        String url = "jdbc:db2://192.168.1.133:50000/mydata";
        String sql = "INSERT INTO person(sfzh,birth,age,ip,post) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, "db2inst1", "");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < 10; i++) {
                for (int j = 1; j <= 10; j++) {
                    int id = (i * 10) + j;
                    stmt.setString(1, id + "");
                    stmt.setString(2, "1996-09-29");
                    stmt.setInt(3, 20);
                    stmt.setString(4, "172.17.23.35");
                    stmt.setString(5, "310004");
                    stmt.addBatch();
                }
                int[] ints = stmt.executeBatch();
                System.out.println(Arrays.toString(ints));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 一次性全表数据拉取出来
     * 反编译jar效果不明显，属性混淆以及next实现编译不出来
     * debug可以看到h.class(resultSet 实现)里的cursor_里的t(ByteArrayOutputStream)会有查询的数据
     */
    public void simpleQuery() {
        String url = "jdbc:db2://192.168.1.133:50000/mydata";
        String sql = "select ip from person";
        try (Connection conn = DriverManager.getConnection(url, "db2inst1", "");
             Statement stmt = conn.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * debug可以看到h.class(resultSet 实现)里的cursor_里的t(ByteArrayOutputStream)会有查询的fetchSize条数据
     */
    public void cursorQuery() {
        String url = "jdbc:db2://192.168.1.133:50000/mydata";
        String sql = "select * from person";
        try (Connection conn = DriverManager.getConnection(url, "db2inst1", "");
             Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(10);
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
