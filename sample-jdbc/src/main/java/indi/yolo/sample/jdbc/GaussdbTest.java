package indi.yolo.sample.jdbc;

import org.postgresql.jdbc.PgResultSet;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;

/**
 * https://support.huaweicloud.com/mgtg-dws/dws_01_0077.html
 * <p>
 * Class.forName("com.huawei.gauss200.jdbc.Driver")
 */
public class GaussdbTest {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String url = "jdbc:gaussdb://192.168.124.135:5432/postgres";
        try (Connection conn = DriverManager.getConnection(url, "test", "test")) {
//            Util.getCatalogs(conn);
//            Util.getSchemas(conn,"postgres","%");
            Util.getTables(conn, "postgres", "public", "%", null);
//            Util.getColumns(conn,"postgres", "public", "baoxian","%");
//            Util.getColumn(conn,"select * from person");
        }
    }

    /**
     * 普通查询数据一次性返回
     */
    public void simpleQuery() {
        String url = "jdbc:gaussdb://192.168.1.132:5432/postgres";
        String sql = "select * from gongsi";
        try (Connection conn = DriverManager.getConnection(url, "postgres", "postgres");
             Statement stmt = conn.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(sql);
            if (resultSet instanceof PgResultSet) {
                PgResultSet pgResultSet = (PgResultSet) resultSet;
                Field cursor = pgResultSet.getClass().getDeclaredField("cursor");
                cursor.setAccessible(true);
                System.out.println(cursor.get(pgResultSet));
                Field rows = pgResultSet.getClass().getDeclaredField("rows");
                rows.setAccessible(true);
                List<Object> list = (List<Object>) rows.get(pgResultSet);
                System.out.println("rows size: " + list.size());
            }
            resultSet.close();
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cursor based ResultSets cannot be used in all situations.
     * There a number of restrictions which will make the driver silently fall back to fetching the whole ResultSet at once.
     * <p>
     * 1,The connection to the server must be using the V3 protocol.
     * This is the default for (and is only supported by) server versions 7.4 and later.
     * 2,The Connection must not be in autocommit mode.
     * The backend closes cursors at the end of transactions, so in autocommit mode the backend will have closed the cursor before anything can be fetched from it.
     * 3,The Statement must be created with a ResultSet type of ResultSet.TYPE_FORWARD_ONLY.
     * This is the default, so no code will need to be rewritten to take advantage of this, but it also means that you cannot scroll backwards or otherwise jump around in the ResultSet.
     * 4,The query given must be a single statement, not multiple statements strung together with semicolons.
     * <p>
     * Setting the fetch size back to 0 will cause all rows to be cached (the default behaviour).
     */
    public void cursorQuery() {
        String url = "jdbc:gaussdb://192.168.1.132:5432/postgres";
        String sql = "select * from gongsi";
        try (Connection conn = DriverManager.getConnection(url, "postgres", "postgres")) {
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            stmt.setFetchSize(1);
            ResultSet resultSet = stmt.executeQuery(sql);
            if (resultSet instanceof PgResultSet) {
                PgResultSet pgResultSet = (PgResultSet) resultSet;
                Field cursor = pgResultSet.getClass().getDeclaredField("cursor");
                cursor.setAccessible(true);
                System.out.println(cursor.get(pgResultSet));
                Field rows = pgResultSet.getClass().getDeclaredField("rows");
                rows.setAccessible(true);
                List<Object> list = (List<Object>) rows.get(pgResultSet);
                System.out.println("rows size: " + list.size());
                System.out.println(pgResultSet.isAfterLast());
                pgResultSet.next();
                System.out.println(pgResultSet.isAfterLast());
                pgResultSet.next();
                System.out.println(pgResultSet.isAfterLast());
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
