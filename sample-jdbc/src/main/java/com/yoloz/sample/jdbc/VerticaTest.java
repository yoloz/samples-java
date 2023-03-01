package com.yoloz.sample.jdbc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * <p>
 * Class.forName("com.vertica.jdbc.Driver")
 */
public class VerticaTest {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
//        Class.forName("com.vertica.jdbc.Driver");
        VerticaTest verticaTest = new VerticaTest();

        String url = "jdbc:vertica://192.168.1.149:5433/test";
        try (Connection conn = DriverManager.getConnection(url, "dbadmin", "vertica")) {
//            Util.getCatalogs(conn);
//            Util.getSchemas(conn,"test","%");
//            Util.getTables(conn, "test", "testsc", "%", null);
//            Util.getColumns(conn,"test", "testsc", "person","%");
            Util.getColumn(conn, "select * from test.testsc.typeTest");
            verticaTest.simpleQuery(conn);
        }
    }

    public void simpleQuery(Connection connection) {
        String sql = "select * from testsc.typeTest";
        try (Statement stmt = connection.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(sql);
            Util.print(resultSet);
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void inertTest(Connection connection) throws SQLException, IOException {
        String sql = "INSERT INTO test.testsc.typeTest\n" +
                "(int_t, varchar_t, timestamp_t, float_t, varbinary_t, boolean_t, longbinary_t, longvarchar_t, uuid_t)\n" +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, 1);
        pstmt.setString(2, "test");
        pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
        pstmt.setFloat(4, 1.0f);
        pstmt.setBytes(5, "test".getBytes());
        pstmt.setBoolean(6, true);
        pstmt.setBlob(7,
                Files.newInputStream(Paths.get(System.getProperty("user.home"), "test.pdf")));
        pstmt.setClob(8,
                Files.newBufferedReader(Paths.get(System.getProperty("user.home"), "test.txt")));

        System.out.println(pstmt.execute());
        pstmt.close();
    }


}
