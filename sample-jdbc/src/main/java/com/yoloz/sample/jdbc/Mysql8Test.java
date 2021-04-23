package com.yoloz.sample.jdbc;

import com.mysql.cj.jdbc.result.ResultSetImpl;
import com.mysql.cj.protocol.ResultsetRows;

import java.sql.*;
import java.util.Random;

/**
 * mysql5:
 * url = jdbc:mysql://localhost:3306/user?useUnicode=true&characterEncoding=utf8
 * driver = com.mysql.jdbc.Driver
 * <p>
 * mysql8:
 * url = jdbc:mysql://localhost:3306/user?serverTimezone=UTC&characterEncoding=utf8&useUnicode=true&useSSL=false
 * drive = com.mysql.cj.jdbc.Driver
 */
public class Mysql8Test {

    public static void main(String[] args) throws ClassNotFoundException, SQLException, InterruptedException {
//        Class.forName("com.mysql.cj.jdbc.Driver");
//        Mysql8Test mysql8Test = new Mysql8Test();
//        mysql8Test.getColumns();
//        mysql8Test.getUpdateCount();
//        mysql8Test.getResultSet();
//        mysql8Test.simpleQuery1();
//        mysql8Test.transactionQuery();

        String url = "jdbc:mysql://192.168.1.116:3306";
        try (Connection conn = DriverManager.getConnection(url, "test", "")) {
            Util.getCatalogs(conn);
            Util.getTables(conn, "test", null, "%", null);
            Util.getColumns(conn,"test",null,"baoxian","%");
        }
    }

    public void transactionQuery() {
        String sql = "SELECT * FROM  baoxian b limit 10";
        String url = "jdbc:mysql://127.0.0.1:10001/123456789";
        try (Connection conn = DriverManager.getConnection(url, "test", "test")) {
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            stmt.setFetchSize(10);
            ResultSet resultSet = stmt.executeQuery(sql);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int colCount = resultSetMetaData.getColumnCount();
            int total = 0;
            long start = System.currentTimeMillis();
            while (resultSet.next()) {
                StringBuilder line = new StringBuilder();
                for (int i = 1; i <= colCount; i++) {
                    line.append(resultSet.getObject(i)).append(',');
                }
                System.out.println(line.substring(0, line.length() - 1));
                total++;
            }
            System.out.println(total + ",cost time:" + (System.currentTimeMillis() - start));
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void simpleQuery1() {
        String sql = "SELECT * FROM  baoxian b limit 10";
//        properties.put("responseBuffering","full");  //msssql
//        properties.put("selectMethod", "cursor");    //mssql
//        properties.put("useCursors", "true");        //jtds
        String url = "jdbc:mysql://127.0.0.1:10001/123456789";
        try (Connection conn = DriverManager.getConnection(url, "test", "test");
             Statement stmt = conn.createStatement()) {
//            stmt.setFetchSize(10);
            ResultSet resultSet = stmt.executeQuery(sql);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int colCount = resultSetMetaData.getColumnCount();
            int total = 0;
            long start = System.currentTimeMillis();
            while (resultSet.next()) {
                StringBuilder line = new StringBuilder();
                for (int i = 1; i <= colCount; i++) {
                    line.append(resultSet.getObject(i)).append(',');
                }
                System.out.println(line.substring(0, line.length() - 1));
                total++;
            }
            System.out.println(total + ",cost time:" + (System.currentTimeMillis() - start));
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //getResultSet可以多次取且每次取得都是最新的，一次返回多resultSet可以使用getMoreResults移动
    public void getResultSet() {
        String url = "jdbc:mysql://192.168.1.116:3306/test";
        try (Connection conn = DriverManager.getConnection(url, "test", "");
             Statement stmt = conn.createStatement()) {
            System.out.println(stmt.execute("select * from abc"));
            System.out.println(stmt.getResultSet());
            System.out.println(stmt.getResultSet());
            while (stmt.getResultSet().next()) {
                System.out.println(stmt.getResultSet().getObject(1));
            }
            System.out.println(stmt.execute("select * from abc"));
            System.out.println(stmt.getResultSet());
            System.out.println(stmt.getResultSet());
            while (stmt.getResultSet().next()) {
                System.out.println(stmt.getResultSet().getObject(1));
            }
            System.out.println(stmt.getMoreResults());
            System.out.println(stmt.getResultSet());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //getUpdateCount可以多次取，getLargeUpdateCount也可以取出来,直到getMoreResults移动
    public void getUpdateCount() {
        String url = "jdbc:mysql://192.168.1.116:3306/test";
        try (Connection conn = DriverManager.getConnection(url, "test", "");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO abc(a,b) VALUES (?,?)")) {
            Random random = new Random();
            stmt.setString(1, "testa");
            stmt.setString(2, "testb");
            stmt.execute();
            System.out.println(stmt.getUpdateCount());
            System.out.println(stmt.getUpdateCount());
            System.out.println(stmt.getLargeUpdateCount());
            System.out.println(stmt.getMoreResults());
            System.out.println(stmt.getUpdateCount());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 一次性全表数据拉取出来
     * 大字段数据byte[]
     */
    public void simpleQuery() {
        String url = "jdbc:mysql://192.168.1.116:3306/test";
        String sql = "select * from gongsi";
        try (Connection conn = DriverManager.getConnection(url, "test", "");
             Statement stmt = conn.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(sql);
            if (resultSet instanceof ResultSetImpl) {
                ResultSetImpl resultSetImpl = (ResultSetImpl) resultSet;
                ResultsetRows resultsetRows = resultSetImpl.getRows();
                System.out.println(resultsetRows.getClass());
                System.out.println(resultsetRows.size());
                System.out.println(resultSetImpl.last());
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * streaming resultSet未携带数据需要每次next取数据
     */
    public void streamingQuery() {
        String url = "jdbc:mysql://192.168.1.116:3306/test";
        String sql = "select * from gongsi";
        try (Connection conn = DriverManager.getConnection(url, "test", "");
             Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(Integer.MIN_VALUE);
            ResultSet resultSet = stmt.executeQuery(sql);
            if (resultSet instanceof ResultSetImpl) {
                ResultSetImpl resultSetImpl = (ResultSetImpl) resultSet;
                ResultsetRows resultsetRows = resultSetImpl.getRows();
                System.out.println(resultsetRows.getClass());
                System.out.println(resultsetRows.size());
                System.out.println(resultSetImpl.last());
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * cursor resultSet未携带数据需要每次next取数据
     * 添加useCursorFetch=true且statement.setFetchSize(resultSet.setFetchSize无效) 否则仍是static rows
     * fetchSize数据在服务端,驱动仍是一次一条数据
     */
    public void cursorQuery() {
        String url = "jdbc:mysql://192.168.1.116:3306/test?useCursorFetch=true";
        String sql = "select * from gongsi";
        try (Connection conn = DriverManager.getConnection(url, "test", "");
             Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(2);
            ResultSet resultSet = stmt.executeQuery(sql);
            if (resultSet instanceof ResultSetImpl) {
                ResultSetImpl resultSetImpl = (ResultSetImpl) resultSet;
                ResultsetRows resultsetRows = resultSetImpl.getRows();
                System.out.println(resultsetRows.getClass());
                System.out.println(resultsetRows.size());
                System.out.println(resultSetImpl.last());
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void mysql_procedure() {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://127.0.0.1:3306/test";
        String create = "create procedure add_pro(a int,b int,out sum int) begin set sum = a + b; end";
        String call = "{call add_pro(?,?,?)}";
        String drop = "drop procedure if exists add_pro";
        try {
            Class.forName(driver);
            try (Connection conn = DriverManager.getConnection(url, "user", "pwd")) {
                CallableStatement cstmt = null;

                cstmt = conn.prepareCall(create);
                System.out.println(cstmt.execute());
                System.out.println(cstmt.getResultSet());
                System.out.println(cstmt.getUpdateCount());
                System.out.println(cstmt.getMoreResults());
                cstmt.close();

                cstmt = conn.prepareCall(call);
                cstmt.setInt(1, 4);
                cstmt.setInt(2, 5);
                cstmt.registerOutParameter(3, Types.INTEGER);
                /*ResultSet rs = cstmt.executeQuery();
                rs.next(); java.sql.SQLException: ResultSet is from UPDATE. No Data.*/
                System.out.println(cstmt.execute());
                System.out.println(cstmt.getResultSet());
                System.out.println(cstmt.getUpdateCount());
                System.out.println(cstmt.getMoreResults());
                System.out.println(cstmt.getInt(3));
                cstmt.close();

                cstmt = conn.prepareCall(drop);
                System.out.println(cstmt.execute());
                System.out.println(cstmt.getResultSet());
                System.out.println(cstmt.getUpdateCount());
                System.out.println(cstmt.getMoreResults());
                cstmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mysql_procedure_rs() {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://127.0.0.1:3306/fea_flow";
        String create = "CREATE PROCEDURE pro_findById(IN eid INT) " +
                "BEGIN SELECT job_name,service_id FROM lgjob WHERE job_id=eid; END";
        String call = "CALL pro_findById(?)";
        String drop = "drop procedure if exists pro_findById";
        try {
            Class.forName(driver);
            try (Connection conn = DriverManager.getConnection(url, "user", "pwd")) {
                CallableStatement cstmt = null;

                cstmt = conn.prepareCall(create);
                System.out.println(cstmt.execute());
                System.out.println(cstmt.getResultSet());
                System.out.println(cstmt.getUpdateCount());
                System.out.println(cstmt.getMoreResults());
                cstmt.close();

                cstmt = conn.prepareCall(call);
                cstmt.setInt(1, 11900);
                // execute,executeUpdate,executeQuery均可以
                ResultSet rs = cstmt.executeQuery();
                while (rs.next()) {
                    String name = rs.getString("job_name");
                    int service_id = rs.getInt("service_id");
                    System.out.println(name + "," + service_id);
                }
                cstmt.close();

                cstmt = conn.prepareCall(drop);
                System.out.println(cstmt.execute());
                System.out.println(cstmt.getResultSet());
                System.out.println(cstmt.getUpdateCount());
                System.out.println(cstmt.getMoreResults());
                cstmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
