package com.yoloz.sample.jdbc;

import org.datanucleus.metadata.MetaData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

/**
 * @author yoloz
 */
public class MysqlTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

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