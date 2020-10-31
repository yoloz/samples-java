
import java.lang.reflect.Field;
import java.sql.*;

/**
 * http://jtds.sourceforge.net/resultSets.html
 */
public class JtdsTest {

    public static void main(String[] args) {

    }

    /**
     * 类似sqlserver的默认adaptive模式
     * jtds文档默认全部拉取数据可能对应sqlserver先前的默认全拉取协议
     */
    public void simpleQuery() {
        String url = "jdbc:jtds:sqlserver://192.168.1.132:1433;databasename=test;";
        String sql = "select * from gongsi2";
        try (Connection conn = DriverManager.getConnection(url, "SA", "");
             Statement stmt = conn.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(sql);
            Field useCursors = conn.getClass().getDeclaredField("useCursors");
            useCursors.setAccessible(true);
            System.out.println(useCursors.get(conn));
            resultSet.close();
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void cursorQuery() {
        String url = "jdbc:jtds:sqlserver://192.168.1.132:1433;databasename=test;useCursors=true;";
        String sql = "select * from gongsi2";
        try (Connection conn = DriverManager.getConnection(url, "SA", "");
             Statement stmt = conn.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(sql);
            Field useCursors = conn.getClass().getDeclaredField("useCursors");
            useCursors.setAccessible(true);
            System.out.println(useCursors.get(conn));
            resultSet.close();
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
