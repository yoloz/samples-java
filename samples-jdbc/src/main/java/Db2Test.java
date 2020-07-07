
import java.sql.*;
import java.util.Arrays;


public class Db2Test {

    public static void main(String[] args) {

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
        try (Connection conn = DriverManager.getConnection(url, "db2inst1", "fhcs2019");
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
        try (Connection conn = DriverManager.getConnection(url, "db2inst1", "fhcs2019");
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
        try (Connection conn = DriverManager.getConnection(url, "db2inst1", "fhcs2019");
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
