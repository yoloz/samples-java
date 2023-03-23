package indi.yolo.sample.jdbc;

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

        String url = "jdbc:vertica://192.168.124.234:5433/test_db";
        try (Connection conn = DriverManager.getConnection(url, "dbadmin", "xxxxx")) {
//            Util.getCatalogs(conn);
//            Util.getSchemas(conn,"test_db","%");
//            Util.getTables(conn, "test_db", "public", "%", null);
//            Util.getColumns(conn,"test_db", "public", "person","%");
//            Util.getColumn(conn, "select * from test_db.public.typeTest");
            verticaTest.simpleUpdate(conn);
//            verticaTest.simpleQuery(conn);
        }
    }

    public void simpleQuery(Connection connection) {
        String sql = "select * from public.typeTest";
        try (Statement stmt = connection.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(sql);
            Util.print(resultSet);
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void simpleUpdate(Connection connection) {
        String sql = "CREATE TABLE public.typeTest\n" +
                "(\n" +
                "    int_t int,\n" +
                "    varchar_t varchar(80),\n" +
                "    timestamp_t timestamp,\n" +
                "    float_t float,\n" +
                "    varbinary_t varbinary(65000),\n" +
                "    boolean_t boolean,\n" +
                "    longbinary_t long varbinary(32000000),\n" +
                "    longvarchar_t long varchar(65000),\n" +
                "    uuid_t uuid\n" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            boolean resultSet = stmt.execute(sql);
            System.out.println(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void inertTest(Connection connection) throws SQLException, IOException {
        String sql = "INSERT INTO test_db.public.typeTest\n" +
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
