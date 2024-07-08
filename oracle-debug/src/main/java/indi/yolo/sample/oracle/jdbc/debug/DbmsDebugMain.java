package indi.yolo.sample.oracle.jdbc.debug;

import indi.yolo.sample.oracle.jdbc.debug.entity.RunTimeInfo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.util.concurrent.TimeUnit;

/**
 * <a href="https://docs.oracle.com/en/database/oracle/oracle-database/12.2/arpls/DBMS_DEBUG.html">DBMS_DEBUG</a>
 *
 * @author yolo
 */
public class DbmsDebugMain {

    String queryArgument = """
            SELECT
            	ARGUMENT_NAME,
            	IN_OUT,
            	DATA_TYPE,
            	DEFAULT_VALUE,
            	TYPE_OWNER,
            	TYPE_NAME
            FROM
            	SYS.ALL_ARGUMENTS
            WHERE
            	OWNER = 'TEST'
            	AND OBJECT_NAME = 'LOOP_TESTER'
            	AND DATA_LEVEL = 0
            	AND PACKAGE_NAME IS NULL
            ORDER BY
            	POSITION
            """;

    public static void main(String[] args) throws SQLException, InterruptedException {
        DbmsDebugMain debugMain = new DbmsDebugMain();
        debugMain.functionDebug();
//        debugMain.procedureDebug();
    }

    public void functionDebug() throws SQLException {
        String jdbcUrl = "jdbc:oracle:thin:@//192.168.124.231:1521/XEPDB1";
        //create function
        try (Connection connection = DriverManager.getConnection(jdbcUrl, "test", "test")) {
            Utils.createFunction(connection);
        }
        Connection target = DriverManager.getConnection(jdbcUrl, "test", "test");
        Connection debug = DriverManager.getConnection(jdbcUrl, "test", "test");
        DbmsTargetSession targetSession = new DbmsTargetSession(target);
        DbmsDebugSession debugSession = new DbmsDebugSession(debug);

        targetSession.switchSession();
        String sessionId = targetSession.initialize();
        targetSession.enableDebug();

        debugSession.attachSession(sessionId);
        int loop = 5;
        new Thread(() -> {
            try {
                // start....
                debugSession.waitTargetSession();
                debugSession.printAllBacktrace();
                debugSession.printSource1("TEST","FUNCTION","LOOP_TESTER");
                debugSession.getValue("ctr");
                debugSession.getValue("ret");
                debugSession.getValue("i");
                System.out.println("\n");
                int bp2 = debugSession.setBreakPoint("LOOP_TESTER", "TEST", 5);
                debugSession.showBreakpoints();
                System.out.println("line 5 bp:" + debugSession.getBreakpoint(5));

                for (int i = 0; i < 20; i++) {
                    if (i == 1) {
                        debugSession.setValue("ret", 4);
//                debugSession.deleteBreakPoint(bp2);
                        System.out.println("\n");
                    }
                    RunTimeInfo runTimeInfo = debugSession.stepOver();
                    debugSession.getValue("i", "ret");
//                debugSession.printLatestBacktrace();
                    debugSession.printAllBacktrace();
                    System.out.println("runtimeInfo:" + runTimeInfo);
                    System.out.println("\n");

//                    if (i == 3) {
//                        debugSession.stepStop();
//                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    debugSession.stepStop();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
        //如果:ctrl直接在参数会报错：ORA-06502: PL/SQL: numeric or value error，通过变量使用则可以
        String text = """
                DECLARE
                	retvar BINARY_INTEGER;
                    CTR BINARY_INTEGER;
                BEGIN
                	CTR := :ctr;
                    retvar := TEST.LOOP_TESTER(CTR);
                    :1 := retvar;
                END;
                """;
        try (CallableStatement statement = target.prepareCall(text)) {
            statement.setInt("ctr", loop);
            statement.registerOutParameter("1", Types.INTEGER);
            statement.execute();
            System.out.println("******target result******:" + statement.getInt("1"));
            // 结束后调用detachSession会让最后一次getValue出错，否则最后一次getValue会一直挂起debugSession
            debugSession.detachSession();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        target.close();
        debug.close();
    }

    public void procedureDebug() throws SQLException, InterruptedException {
        String jdbcUrl = "jdbc:oracle:thin:@//192.168.124.231:1521/XEPDB1";
        //create procedure
        try (Connection connection = DriverManager.getConnection(jdbcUrl, "test", "test")) {
            Utils.createProcedure(connection);
        }
        Connection target = DriverManager.getConnection(jdbcUrl, "test", "test");
        Connection debug = DriverManager.getConnection(jdbcUrl, "test", "test");
        DbmsTargetSession targetSession = new DbmsTargetSession(target);
        DbmsDebugSession debugSession = new DbmsDebugSession(debug);

        targetSession.switchSession();
        String sessionId = targetSession.initialize();
        targetSession.enableDebug();

        debugSession.attachSession(sessionId);
        int loop = 5;
        new Thread(() -> {
            //如果:ctrl直接在参数会报错：ORA-06502: PL/SQL: numeric or value error，通过变量使用则可以
            String text = """
                    DECLARE
                        "CTR" NUMBER;
                        "RETV" NUMBER;
                    BEGIN
                        "CTR" := :ctr;
                        -- "RETV" := '';
                        "TEST"."LOOP_TESTERP"("CTR" => "CTR","RETV" => "RETV");
                        :RETVR := RETV;
                    END;
                    """;
            try (CallableStatement statement = target.prepareCall(text)) {
                statement.setInt("ctr", loop);
                statement.registerOutParameter("RETVR", Types.INTEGER);
                statement.execute();
                System.out.println("******target result******:" + statement.getInt("RETVR"));
                // 结束后调用detachSession会让最后一次getValue出错，否则最后一次getValue会一直挂起debugSession
                debugSession.detachSession();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
        TimeUnit.SECONDS.sleep(1);
        try {
            // start....
            debugSession.waitTargetSession();
            debugSession.printAllBacktrace();
            debugSession.printSource1("TEST","PROCEDURE","LOOP_TESTERP");
            debugSession.getValue("ctr");
            debugSession.getValue("ret");
            debugSession.getValue("i");
            System.out.println("\n");
            int bp2 = debugSession.setBreakPoint("LOOP_TESTERP", "TEST", 5);
            debugSession.showBreakpoints();
            debugSession.printSource2("TEST","LOOP_TESTERP");
            System.out.println("line 5 bp:" + debugSession.getBreakpoint(5));

            for (int i = 0; i < 20; i++) {
                if (i == 1) {
                    debugSession.setValue("ret", 4);
//                debugSession.deleteBreakPoint(bp2);
                    System.out.println("\n");
                }
                RunTimeInfo runTimeInfo = debugSession.stepOver();
                debugSession.getValue("i", "ret");
//                debugSession.printLatestBacktrace();
                debugSession.printAllBacktrace();
                System.out.println("runtimeInfo:" + runTimeInfo);
                System.out.println("\n");

//                    if (i == 3) {
//                        debugSession.stepStop();
//                    }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        target.close();
        debug.close();
    }
}
