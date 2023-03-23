package indi.yolo.sample.jdbc;


import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * @author yoloz
 */
public class MysqlTest {


    @Test
    public void yearTest() throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:10001/123456789", sql = "SELECT * FROM test_year_01 ty";
        try (Connection conn = DriverManager.getConnection(url, "test", "test");
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(sql)
        ) {
            while (resultSet.next()) {
                System.out.println(resultSet.getObject(1));
            }

        }
    }

    @Test
    public void blobTest() throws SQLException, IOException {
        String url = "jdbc:mysql://127.0.0.1:10001/123456789", sql = "select * from test_longblob_01 tl";
        try (Connection conn = DriverManager.getConnection(url, "test", "test");
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(sql)
        ) {
            while (resultSet.next()) {
                Blob blob = resultSet.getBlob(2);
                System.out.println(blob.length());
                Files.write(Paths.get(System.getProperty("user.dir")).resolve("test.bmp"), blob.getBytes(1, (int) blob.length()));
            }

        }
    }
}