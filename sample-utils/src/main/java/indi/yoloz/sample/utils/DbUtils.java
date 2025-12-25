package indi.yoloz.sample.utils;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DbUtils {

    private static BasicDataSource dataSource;

    private DbUtils() {

    }

    public static void initDbSource(String driver, String host, String port, String user, String pwd,
                                    String dbName, int connMin, int connMax) {
        String url = "jdbc:mysql://" + host + ":" + port +
                "/" + dbName + "?useUnicode=true&characterEncoding=utf-8";
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setInitialSize(connMin);
        dataSource.setMaxTotal(connMax);
        dataSource.setMaxIdle(connMax);
        dataSource.setMinIdle(connMin);
        dataSource.setUsername(user);
        dataSource.setPassword(pwd);
//        dataSource.setDefaultAutoCommit(false);
    }

    public static void close() throws SQLException {
        if (dataSource != null) dataSource.close();
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) throw new SQLException("dataSource is null ");
        return dataSource.getConnection();
    }

    public static List<Map<String, Object>> query(String sql, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner(dataSource);
        ResultSetHandler<List<Map<String, Object>>> h = new MapListHandler();
        return runner.query(sql, h, params);
    }

    public static Object[] insert(String sql, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner(dataSource);
        ResultSetHandler<Object[]> h = new ArrayHandler();
        return runner.insert(sql, h, params);
    }

    public static List<Object[]> insertBatch(String sql, Object[][] params) throws SQLException {
        QueryRunner runner = new QueryRunner(dataSource);
        ResultSetHandler<List<Object[]>> h = new ArrayListHandler();
        return runner.insertBatch(sql, h, params);
    }

    public static Object[] insert(Connection conn, String sql, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner();
        ResultSetHandler<Object[]> h = new ArrayHandler();
        return runner.insert(conn, sql, h, params);
    }

    public static List<Object[]> insertBatch(Connection conn, String sql, Object[][] params) throws SQLException {
        QueryRunner runner = new QueryRunner();
        ResultSetHandler<List<Object[]>> h = new ArrayListHandler();
        return runner.insertBatch(conn, sql, h, params);
    }

    public static int update(String sql, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner(dataSource);
        return runner.update(sql, params);
    }

    public static int update(Connection conn, String sql, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner();
        return runner.update(conn, sql, params);
    }

    public static void main(String[] args) {

        try {
            DbUtils.initDbSource("com.mysql.jdbc.Driver",
                    "xxxx", "3306", "xxxx"
                    , "xxxx", "sdas", 1, 4);
            List<Map<String, Object>> l = DbUtils.query("select count(jobid) from multiapp where ip=?", "");
            int total = 0;
//            if (!l.isEmpty()) total = (int) l.get(0).get("count(jobid)");
//            取出来的count(jobid)是long,可以通过下面方式转成int
            if (!l.isEmpty()) total = ((Number) l.get(0).get("count(jobid)")).intValue();
            System.out.println("======" + total);
            List<Map<String, Object>> list = DbUtils.query("select ip,amount,status from multinode");
            for (Map<String, Object> map : list) {
                System.out.println("ip: " + String.valueOf(map.get("ip")));
                System.out.println("amount: " + (int) map.get("amount"));
                System.out.println("status: " + (int) map.get("status"));
            }
            DbUtils.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
