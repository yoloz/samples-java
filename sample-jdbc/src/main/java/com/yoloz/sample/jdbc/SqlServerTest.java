package com.yoloz.sample.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerResultSet;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * https://docs.microsoft.com/en-us/sql/connect/jdbc/setting-the-connection-properties?view=sql-server-ver15
 * <p>
 * com.microsoft.jdbc.sqlserver.SQLServerDriver //<2005
 * com.microsoft.sqlserver.jdbc.SQLServerDriver  //>=2005
 * <p>
 * jdbc:microsoft:sqlserver://127.0.0.1:1433;databaseName=test;integratedSecurity=true; //<2005
 * jdbc:sqlserver://127.0.0.1:1433;databaseName=test; //>=2005
 */
public class SqlServerTest {

    public static void main(String[] args) throws SQLException {
        String url = "jdbc:sqlserver://192.168.1.132:1433;database=test;";
        try (Connection conn = DriverManager.getConnection(url, "SA", "Fhcs2019")) {
//            Util.getCatalogs(conn);
//            Util.getSchemas(conn,"test","%");
//            Util.getTables(conn, "test", "dbo", "%", null);
//            Util.getColumns(conn,"test", "dbo", "baoxian","%");
//            Util.getColumn(conn,"select * from person");
            test02(conn);
        }
    }

    public  static void test02(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement(1003,1007)) {
            stmt.setMaxRows(200);
            boolean b = stmt.execute("select * from dbo.baoxian");
            System.out.printf("stmt execute:%s\n", b);
            ResultSet resultSet = stmt.getResultSet();
            Util.print(resultSet);
            String resultSetName = resultSet.getCursorName();
            System.out.printf("result set name:%s\n", resultSetName);
            resultSet.close();
        }
    }

    /**
     * the minimum possible data is buffered when necessary.
     */
    public void simpleQuery() {
        String url = "jdbc:sqlserver://192.168.1.132:1433;database=test;";
        String sql = "select * from gongsi2";
        try (Connection conn = DriverManager.getConnection(url, "SA", "");
             Statement stmt = conn.createStatement()) {
            SQLServerResultSet resultSet = (SQLServerResultSet) stmt.executeQuery(sql);
            Method isForwardOnly = resultSet.getClass().getDeclaredMethod("isForwardOnly");
            isForwardOnly.setAccessible(true);
            System.out.println(isForwardOnly.invoke(resultSet));
            Field serverCursorId = resultSet.getClass().getDeclaredField("serverCursorId");
            serverCursorId.setAccessible(true);
            System.out.println(0 == (int) serverCursorId.get(resultSet));
            System.out.println(resultSet.getFetchSize());
            Field responseBuffering = conn.getClass().getDeclaredField("responseBuffering");
            responseBuffering.setAccessible(true);
            System.out.println("adaptive".equals(responseBuffering.get(conn)));
            resultSet.close();
        } catch (SQLException | NoSuchFieldException | IllegalAccessException | NoSuchMethodException |
                InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * responseBuffering  String ["full" | "adaptive"]
     * <p>
     * If this property is set to "adaptive", the minimum possible data is buffered when necessary.
     * The default mode is "adaptive."
     * <p>
     * When this property is set to "full", the entire result set is read from the server when a statement is executed.
     * <p>
     * Note: After upgrading the JDBC driver from version 1.2, the default buffering behavior will be "adaptive."
     * If your application has never set the "responseBuffering" property and you want to keep the version 1.2
     * default behavior in your application, you must set the responseBufferring property to "full"
     * either in the connection properties or by using the setResponseBuffering method of the SQLServerStatement object.
     */
    public void fullQuery() {
        String url = "jdbc:sqlserver://192.168.1.132:1433;database=test;responseBuffering=full";
        String sql = "select * from gongsi2";
        try (Connection conn = DriverManager.getConnection(url, "SA", "");
             Statement stmt = conn.createStatement()) {
            SQLServerResultSet resultSet = (SQLServerResultSet) stmt.executeQuery(sql);
            Method isForwardOnly = resultSet.getClass().getDeclaredMethod("isForwardOnly");
            isForwardOnly.setAccessible(true);
            System.out.println(isForwardOnly.invoke(resultSet));
            Field serverCursorId = resultSet.getClass().getDeclaredField("serverCursorId");
            serverCursorId.setAccessible(true);
            System.out.println(0 == (int) serverCursorId.get(resultSet));
            System.out.println(resultSet.getFetchSize());
            Field responseBuffering = conn.getClass().getDeclaredField("responseBuffering");
            responseBuffering.setAccessible(true);
            System.out.println("full".equals(responseBuffering.get(conn)));
            resultSet.close();
        } catch (SQLException | NoSuchFieldException | IllegalAccessException | NoSuchMethodException |
                InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * selectMethod String ["direct" | "cursor"]
     * <p>
     * If this property is set to "cursor," a database cursor is created for each query created on the connection for
     * TYPE_FORWARD_ONLY and CONCUR_READ_ONLY cursors.
     * This property is typically required only if the application generates large result sets that cannot be fully contained in client memory.
     * When this property is set to "cursor," only a limited number of result set rows are retained in client memory.
     * <p>
     * The default behavior is that all result set rows are retained in client memory.(need set responseBuffering="full")
     * This behavior provides the fastest performance when the application is processing all rows.
     */
    public void cursorQuery() {
        String url = "jdbc:sqlserver://192.168.1.132:1433;database=test;selectMethod=cursor";
        String sql = "select * from gongsi2";
        try (Connection conn = DriverManager.getConnection(url, "SA", "")) {
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            stmt.setFetchSize(10);
            SQLServerResultSet resultSet = (SQLServerResultSet) stmt.executeQuery(sql);
            Method isForwardOnly = resultSet.getClass().getDeclaredMethod("isForwardOnly");
            isForwardOnly.setAccessible(true);
            System.out.println(isForwardOnly.invoke(resultSet));
            Field serverCursorId = resultSet.getClass().getDeclaredField("serverCursorId");
            serverCursorId.setAccessible(true);
            System.out.println(0 != (int) serverCursorId.get(resultSet));
            System.out.println(resultSet.getFetchSize());
            Field responseBuffering = conn.getClass().getDeclaredField("responseBuffering");
            responseBuffering.setAccessible(true);
            System.out.println("adaptive".equals(responseBuffering.get(conn)));
            resultSet.close();
        } catch (SQLException | NoSuchFieldException | IllegalAccessException | NoSuchMethodException |
                InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
