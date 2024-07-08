package indi.yolo.sample.oracle.jdbc.debug;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collection;

/**
 * @author yolo
 */
public class Utils {

    // 创建测试函数
    public static void createFunction(Connection connection) throws SQLException {
        String text = """
                CREATE OR REPLACE FUNCTION TEST.loop_tester(ctr NUMBER) RETURN NUMBER IS ret NUMBER := 0;
                BEGIN
                	FOR i IN 1.. ctr
                LOOP
                    ret := ret + 1;
                END LOOP;
                RETURN ret;
                END;
                """;
        String compileText = " alter function TEST.loop_tester compile debug";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(text);
            stmt.execute(compileText);
        }
    }

    // 创建测试存储过程
    public static void createProcedure(Connection connection) throws SQLException {
        String text = """
                CREATE OR REPLACE PROCEDURE TEST.LOOP_TESTERP(CTR IN NUMBER,RETV OUT NUMBER) AS RET NUMBER := 0;
                    BEGIN
                    	FOR I IN 1.. CTR
                    LOOP
                        RET := RET + 1;
                    END LOOP;
                    RETV := RET;
                    END LOOP_TESTERP;
                """;
        String compileText = " alter procedure TEST.LOOP_TESTERP compile debug";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(text);
            stmt.execute(compileText);
        }
    }

    public static void dbmsOutput(Connection connection) throws SQLException {
        // Get dbms_output lines
        String getLinesSql = "BEGIN DBMS_OUTPUT.GET_LINE(?, ?); END;";
        CallableStatement getLinesStatement = connection.prepareCall(getLinesSql);
        getLinesStatement.registerOutParameter(1, Types.CHAR);
        getLinesStatement.registerOutParameter(2, Types.INTEGER);
        int status;
        do {
            getLinesStatement.execute();
            status = getLinesStatement.getInt(2);
            if (status == 0) {
                System.out.println(getLinesStatement.getString(1));
            }
        } while (status == 0);
        // Close Statements
        getLinesStatement.close();
    }

    public static void enableOutput(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("BEGIN DBMS_OUTPUT.ENABLE; END;");
        }
    }

    public static void killSession(Connection connection, String sidSerial) {
        //可能没有权限
        String text = "ALTER SYSTEM KILL SESSION ?";
        try (PreparedStatement statement = connection.prepareStatement(text)) {
            statement.setString(1, sidSerial);
            statement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void probeVersion(Connection connection) throws SQLException {
        String text = """
                DECLARE
                    MAJOR INTEGER;
                    MINOR INTEGER;
                BEGIN
                    DBMS_DEBUG.PROBE_VERSION(MAJOR,MINOR);
                    DBMS_OUTPUT.PUT_LINE('PROBE VERSION IS: ' || MAJOR || '.' || MINOR);
                END;
                """;
        enableOutput(connection);
        try (Statement statement = connection.createStatement()) {
            statement.execute(text);
            statement.execute(text);
        }
        dbmsOutput(connection);
    }

    //SYS.DBMS_DEBUG.Namespace_pkgspec_or_toplevel:1
    public static String outputConstants(Collection<String> names) {
        StringBuilder builder = new StringBuilder("BEGIN").append("\n");
        for (String value : names) {
            builder.append("SYS.DBMS_OUTPUT.PUT_LINE(SYS.DBMS_DEBUG.").append(value).append(");\n");
        }
        builder.append("END;");
        return builder.toString();
    }
}
