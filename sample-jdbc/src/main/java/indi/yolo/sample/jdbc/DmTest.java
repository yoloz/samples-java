package indi.yolo.sample.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DmTest {

    public DmTest() {
    }

    public static void main(String[] args) throws Exception {
        DmTest dmTest = new DmTest();
        dmTest.insertCharacterStream();
    }

    public void insertCharacterStream() throws ClassNotFoundException, SQLException, IOException {
        String url = "jdbc:dm://192.168.124.235:5236/";
        Class.forName("dm.jdbc.driver.DmDriver");
        String sql = "insert into PRPIBNRMODEL(TASKCODE,INSERTDATE,TCOL1,IBNRMODEL )values ( ?,?,?,? )";
        try (Connection conn = DriverManager.getConnection(url, "TEST", "test");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            Path ibnrModelPath = Paths.get(System.getProperty("user.dir"), "sample-jdbc/src/main/resources/ibnrmodel.json");
            InputStream inputStream = Files.newInputStream(ibnrModelPath);
            stmt.setCharacterStream(4, new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            stmt.setObject(1, "202308001002");
            stmt.setObject(2, new Timestamp(System.currentTimeMillis()));
            stmt.setObject(3, "database");
            stmt.execute();
            System.out.println("update count:" + stmt.getUpdateCount());
        }
    }
}
