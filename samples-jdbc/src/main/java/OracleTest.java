import java.sql.*;
import java.util.Random;

public class OracleTest {

    public void insertBatchSql() {
        String url = "jdbc:oracle:thin:@192.168.1.131:1521/XEPDB1";
        try (Connection conn = DriverManager.getConnection(url, "test", "test");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO zyltest(id,birth,age,ip,post) VALUES (?,?,?,?,?)")) {
            Random random = new Random();
            for (int j = 0; j < 30; j++) {
                for (int i = 0; i < 100; i++) {
                    int id = (j * 100) + (i + 1);
                    stmt.setInt(1, id);
                    stmt.setDate(2, new Date(System.currentTimeMillis()));
                    stmt.setInt(3, random.nextInt(80));
                    stmt.setString(4, "172.17.23." + random.nextInt(254));
                    stmt.setInt(5, 310004);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 普通查询数据一次返回10条,可在下面循环中加断点并wireshark验证
     */
    public void simpleQuery() {
        String url = "jdbc:oracle:thin:@192.168.1.131:1521/XEPDB1";
        String sql = "select * from zyltest";
        try (Connection conn = DriverManager.getConnection(url, "test", "test");
             Statement stmt = conn.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(sql);
            for (int i = 0; i < 30; i++) {
                if (resultSet.next()) {
                    System.out.println(resultSet.getInt(1));
                }
            }
            if ("oracle.jdbc.driver.ForwardOnlyResultSet".equals(resultSet.getClass().getName())) {
                System.out.println(resultSet.getFetchSize());
//                Field maxRows = resultSet.getClass().getDeclaredField("maxRows");
//                maxRows.setAccessible(true);
//                System.out.println("maxRows:" + maxRows.get(resultSet));
//                Field fetchedRowCount = resultSet.getClass().getField("fetchedRowCount");
//                fetchedRowCount.setAccessible(true);
//                System.out.println("fetchedRowCount:" + fetchedRowCount.get(resultSet));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 每次拉取指定size大小
     */
    public void cursorQuery() {
        String url = "jdbc:oracle:thin:@192.168.1.131:1521/XEPDB1";
        String sql = "select * from zyltest";
        try (Connection conn = DriverManager.getConnection(url, "test", "test");
             Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(1);
            ResultSet resultSet = stmt.executeQuery(sql);
            for (int i = 0; i < 3; i++) {
                if (resultSet.next()) {
                    System.out.println(resultSet.getInt(1));
                }
            }
            if ("oracle.jdbc.driver.ForwardOnlyResultSet".equals(resultSet.getClass().getName())) {
                System.out.println(resultSet.getFetchSize());
//                Field maxRows = resultSet.getClass().getDeclaredField("maxRows");
//                maxRows.setAccessible(true);
//                System.out.println("maxRows:" + maxRows.get(resultSet));
//                Field fetchedRowCount = resultSet.getClass().getDeclaredField("fetchedRowCount");
//                fetchedRowCount.setAccessible(true);
//                System.out.println("fetchedRowCount:" + fetchedRowCount.get(resultSet));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
