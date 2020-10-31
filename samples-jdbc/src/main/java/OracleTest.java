import java.sql.*;
import java.util.Random;

/**
 * Class.forName("oracle.jdbc.driver.OracleDriver")
 */
public class OracleTest {

    public static void main(String[] args) throws ClassNotFoundException, SQLException, InterruptedException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        OracleTest oracleTest = new OracleTest();
//        oracleTest.getUpdateCount();
        oracleTest.createTable();

    }
    public void createTable() {
        String sql = "CREATE TABLE TEST.PERSON (\n" +
                "SFZH VARCHAR2(100) NOT NULL,\n" +
                "BIRTH DATE NOT NULL,\n" +
                "AGE INTEGER NOT NULL,\n" +
                "IP VARCHAR2(100) NOT NULL,\n" +
                "POST INTEGER NOT NULL,\n" +
                "PRIMARY KEY (SFZH)\n" +
                ")";
        String url = "jdbc:oracle:thin:@192.168.1.131:1521/XEPDB1";
        try (Connection conn = DriverManager.getConnection(url, "test", "test");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            System.out.println(stmt.executeUpdate());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //getResultSet可以多次取且每次取得都是最新的，一次返回多resultSet可以使用getMoreResults移动
    public void getResultSet() {
        String url = "jdbc:oracle:thin:@192.168.1.131:1521/XEPDB1";
        try (Connection conn = DriverManager.getConnection(url, "test", "test");
             Statement stmt = conn.createStatement()) {
            System.out.println(stmt.execute("select * from zyltest"));
            System.out.println(stmt.getResultSet());
            System.out.println(stmt.getResultSet());
            while (stmt.getResultSet().next()) {
                System.out.println(stmt.getResultSet().getObject(1));
            }
            System.out.println(stmt.execute("select * from zyltest"));
            System.out.println(stmt.getResultSet());
            System.out.println(stmt.getResultSet());
            while (stmt.getResultSet().next()) {
                System.out.println(stmt.getResultSet().getObject(1));
            }
            System.out.println(stmt.getMoreResults());
            System.out.println(stmt.getResultSet());
            while (stmt.getResultSet().next()) { //java.sql.SQLException: ORA-01001: invalid cursor
                System.out.println(stmt.getResultSet().getObject(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //getUpdateCount只可取一次,getLargeUpdateCount也可以取出来且也只有一次
    public void getUpdateCount() {
        String url = "jdbc:oracle:thin:@192.168.1.131:1521/XEPDB1";
        try (Connection conn = DriverManager.getConnection(url, "test", "test");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO zyltest(id,birth,age,ip,post) VALUES (?,?,?,?,?)")) {
            Random random = new Random();
            stmt.setInt(1, 1);
            stmt.setDate(2, new Date(System.currentTimeMillis()));
            stmt.setInt(3, random.nextInt(80));
            stmt.setString(4, "172.17.23." + random.nextInt(254));
            stmt.setInt(5, 310004);
            stmt.execute();
            System.out.println(stmt.getLargeUpdateCount());
            System.out.println(stmt.getUpdateCount());
            System.out.println(stmt.getUpdateCount());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
