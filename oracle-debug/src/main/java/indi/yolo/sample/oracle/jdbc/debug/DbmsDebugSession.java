package indi.yolo.sample.oracle.jdbc.debug;

import indi.yolo.sample.oracle.jdbc.debug.entity.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

/**
 * oracle存储过程调试,达梦类似
 *
 * @author yolo
 */
public class DbmsDebugSession {

    private final Connection connection;

    public DbmsDebugSession(Connection connection) {
        this.connection = connection;
    }

    //    SYS.DBMS_DEBUG.SET_TIMEOUT(1);
    public void attachSession(String sessionId) throws SQLException {
        String text = """
                DECLARE
                    debug_session_id VARCHAR2(255);
                BEGIN
                    debug_session_id := :sessionId;
                    SYS.DBMS_DEBUG.ATTACH_SESSION(debug_session_id);
                END;
                """;
        try (CallableStatement statement = this.connection.prepareCall(text)) {
            statement.setString("sessionId", sessionId);
            statement.execute();
        }
        System.out.println("attach session:" + sessionId + " success...");
    }

    //This function waits until the target program signals an event.
    public RunTimeInfo waitTargetSession() throws SQLException {
        String text = """
                DECLARE
                	runinfo dbms_debug.runtime_info;
                    retval binary_integer;
                BEGIN
                	retval := dbms_debug.synchronize(runinfo,0);
                	retval := DBMS_DEBUG.CONTINUE(runinfo,DBMS_DEBUG.break_any_call,DBMS_DEBUG.info_getlineinfo + DBMS_DEBUG.info_getbreakpoint + DBMS_DEBUG.info_getstackdepth + DBMS_DEBUG.info_getoerinfo);
                    :1 := runinfo.terminated;
                END;
                """;
        text = """
                DECLARE
                	run_info SYS.DBMS_DEBUG.RUNTIME_INFO;
                    info_requested BINARY_INTEGER;
                    retval BINARY_INTEGER;
                BEGIN
                    info_requested := SYS.DBMS_DEBUG.info_getStackDepth + SYS.DBMS_DEBUG.info_getLineInfo + SYS.DBMS_DEBUG.info_getBreakpoint + SYS.DBMS_DEBUG.info_getOerInfo;
                	retval := SYS.DBMS_DEBUG.SYNCHRONIZE(run_info, 0);
                	-- retval := DBMS_DEBUG.CONTINUE(run_info,SYS.DBMS_DEBUG.break_next_line,info_requested);
                	:1 := retval;
                	:line := run_info.line#;
                	:terminated := run_info.terminated;
                	:breakpoint := run_info.breakpoint;
                	:stackdepth := run_info.stackdepth;
                	:interpreterdepth := run_info.interpreterdepth;
                	:reason := run_info.reason;
                	:namespace := run_info.program.namespace;
                	:name := run_info.program.name;
                	:owner := run_info.program.owner;
                	:dblink := run_info.program.dblink;
                	:pline := run_info.program.line#;
                	:libunittype := run_info.program.libunittype;
                	:entrypointname := run_info.program.entrypointname;
                END;
                """;
        try (CallableStatement statement = this.connection.prepareCall(text)) {
            statement.registerOutParameter("1", Types.INTEGER);
            statement.registerOutParameter("line", Types.INTEGER);
            statement.registerOutParameter("terminated", Types.INTEGER);
            statement.registerOutParameter("breakpoint", Types.INTEGER);
            statement.registerOutParameter("stackdepth", Types.INTEGER);
            statement.registerOutParameter("interpreterdepth", Types.INTEGER);
            statement.registerOutParameter("reason", Types.INTEGER);
            //program
            statement.registerOutParameter("namespace", Types.INTEGER);
            statement.registerOutParameter("name", Types.VARCHAR);
            statement.registerOutParameter("owner", Types.VARCHAR);
            statement.registerOutParameter("dblink", Types.VARCHAR);
            statement.registerOutParameter("pline", Types.INTEGER);
            statement.registerOutParameter("libunittype", Types.INTEGER);
            statement.registerOutParameter("entrypointname", Types.VARCHAR);

            statement.execute();
            int result = statement.getInt("1");
            if (result == 0) { //success
                ProgramInfo programInfo = new ProgramInfo(statement.getInt("namespace"), statement.getString("name"),
                        statement.getString("owner"), statement.getString("dblink"), statement.getInt("pline"),
                        statement.getInt("libunittype"), statement.getString("entrypointname"));
                RunTimeInfo runTimeInfo = new RunTimeInfo(statement.getInt("line"), statement.getInt("terminated"),
                        statement.getInt("breakpoint"), statement.getInt("stackdepth"), statement.getInt("interpreterdepth"),
                        statement.getInt("reason"), programInfo);
                System.out.println("wait target session:" + runTimeInfo);
                return runTimeInfo;
            } else {
                throw new SQLException(ExceptionFlags.getMessage(result));
            }
        }
    }

    public String printAllBacktrace() throws SQLException {
        String text = """
                DECLARE
                	backtrace_table SYS.DBMS_DEBUG.BACKTRACE_TABLE;
                	i NUMBER;
                	string_buffer VARCHAR2(32767) := '';
                BEGIN
                	SYS.DBMS_DEBUG.PRINT_BACKTRACE(backtrace_table);
                	i := backtrace_table.first();
                	WHILE i IS NOT NULL LOOP
                	    string_buffer := backtrace_table(i).namespace || '<nav>' || backtrace_table(i).name || '<nav>' || backtrace_table(i).owner || '<nav>' || backtrace_table(i).dblink || '<nav>' || backtrace_table(i).line# || '<nav>' || backtrace_table(i).libunittype || '<nav_hr>' || string_buffer;
                	    i := backtrace_table.next(i);
                	END LOOP;
                	:1 := string_buffer;
                END;
                """;
        try (CallableStatement statement = this.connection.prepareCall(text)) {
            statement.registerOutParameter("1", Types.VARCHAR);
            statement.execute();
            String trace = statement.getString("1");
            System.out.println("print all backtrace:" + trace);
            return trace;
        }
    }

    public void printLatestBacktrace() throws SQLException {
        String text = """
                DECLARE
                    backtrace SYS.DBMS_DEBUG.backtrace_table;
                    varPROGRAM SYS.DBMS_DEBUG.PROGRAM_INFO;
                BEGIN
                    SYS.DBMS_DEBUG.PRINT_BACKTRACE(backtrace);
                    varPROGRAM := backtrace(backtrace.count);
                    :namespace := varPROGRAM.namespace;
                	:name := varPROGRAM.name;
                	:owner := varPROGRAM.owner;
                	:dblink := varPROGRAM.dblink;
                	:pline := varPROGRAM.line#;
                	:libunittype := varPROGRAM.libunittype;
                	:entrypointname := varPROGRAM.entrypointname;
                END;
                """;
        try (CallableStatement statement = this.connection.prepareCall(text)) {
            statement.registerOutParameter("namespace", Types.INTEGER);
            statement.registerOutParameter("name", Types.VARCHAR);
            statement.registerOutParameter("owner", Types.VARCHAR);
            statement.registerOutParameter("dblink", Types.VARCHAR);
            statement.registerOutParameter("pline", Types.INTEGER);
            statement.registerOutParameter("libunittype", Types.INTEGER);
            statement.registerOutParameter("entrypointname", Types.VARCHAR);
            statement.execute();
            ProgramInfo programInfo = new ProgramInfo(statement.getInt("namespace"), statement.getString("name"),
                    statement.getString("owner"), statement.getString("dblink"), statement.getInt("pline"),
                    statement.getInt("libunittype"), statement.getString("entrypointname"));
            System.out.println("print latest backTrace:" + programInfo);
        }
    }

    public String printSource1(String owner, String type, String name) throws SQLException {
        String text = """
                DECLARE
                source_buffer VARCHAR2(32767) := '';
                BEGIN
                	FOR rec IN(SELECT text FROM all_source WHERE owner = '%OWNER%' AND type = '%TYPE%' AND name = '%NAME%') LOOP
                		source_buffer := source_buffer || rec.text;
                	END LOOP;
                	:1 := source_buffer;
                END;
                """;
        text = text.replaceAll("%OWNER%", owner);
        text = text.replaceAll("%TYPE%", type);
        text = text.replaceAll("%NAME%", name);
        try (CallableStatement statement = this.connection.prepareCall(text)) {
            statement.registerOutParameter("1", Types.VARCHAR);
            statement.execute();
            String trace = statement.getString("1");
            System.out.println("print source 1:" + trace);
            return trace;
        }
    }

    public String printSource2(String owner, String name) throws SQLException {
        String text = """
                DECLARE
                	source_buffer VARCHAR2(32767) := '';
                BEGIN
                	FOR rec IN(SELECT text FROM all_source WHERE owner = '%OWNER%' AND name = '%NAME%') LOOP
                		source_buffer := source_buffer || rec.text;
                	END LOOP;
                	:1 := source_buffer;
                END;
                """;
        text = text.replaceAll("%OWNER%", owner);
        text = text.replaceAll("%NAME%", name);
        try (CallableStatement statement = this.connection.prepareCall(text)) {
            statement.registerOutParameter("1", Types.VARCHAR);
            statement.execute();
            String trace = statement.getString("1");
            System.out.println("print source 2:" + trace);
            return trace;
        }
    }

    // SET_BREAKPOINT需要在Interpreter is starting(即synchronize收到事件)后设置否则返回bp值都是0
    public int setBreakPoint(String fucName, String owner, int line) throws SQLException {
        String text = """
                DECLARE
                	retval binary_integer;
                	breakpoint_id binary_integer;
                BEGIN
                	retval := DBMS_DEBUG.SET_BREAKPOINT(NULL,5,breakpoint_id);
                	:1 := retval;
                	:2 := breakpoint_id;
                END;
                """;
        text = """
                DECLARE
                    varPROGRAM SYS.DBMS_DEBUG.PROGRAM_INFO;
                    varLine BINARY_INTEGER;
                    retval BINARY_INTEGER;
                    pointnum BINARY_INTEGER;
                BEGIN
                    varPROGRAM.Namespace:= :namespace;
                    varPROGRAM.NAME:= :fucName;
                    varPROGRAM.Owner := :owner;
                    varPROGRAM.DBLink := null; --no dblink access
                    varLine := :line;
                    retval := SYS.DBMS_DEBUG.SET_BREAKPOINT(varPROGRAM, varLine, pointnum);
                    :1 := retval;
                    :2 := pointnum;
                END;
                """;
        try (CallableStatement statement = this.connection.prepareCall(text)) {
            statement.setInt("namespace", Namespaces.Namespace_pkgspec_or_toplevel.getCode());
            statement.setString("fucName", fucName);
            statement.setString("owner", owner);
            statement.setInt("line", line);
            statement.registerOutParameter("1", Types.INTEGER);
            statement.registerOutParameter("2", Types.INTEGER);
            statement.execute();
            int result = statement.getInt("1");
            if (result == 0) {
                int point = statement.getInt("2");
                System.out.println("set break point:" + point + " success...");
                return point;
            } else {
                throw new SQLException(ExceptionFlags.getMessage(result));
            }
        }
    }

    public String showBreakpoints() throws SQLException {
        String text = """
                DECLARE
                	breakpoint_table dbms_debug.breakpoint_table;
                	i NUMBER;
                	string_buffer VARCHAR2(32767) := '';
                BEGIN
                	dbms_debug.SHOW_BREAKPOINTS(breakpoint_table);
                	i := breakpoint_table.first();
                	WHILE i IS NOT NULL LOOP
                		string_buffer := string_buffer || '<nav_hr>' || breakpoint_table(i).name || '<nav>' || breakpoint_table(i).owner || '<nav>' || breakpoint_table(i).dblink || '<nav>' || breakpoint_table(i).line# || '<nav>' || breakpoint_table(i).libunittype || '<nav>' || breakpoint_table(i).status;
                		i := breakpoint_table.next(i);
                	END LOOP;
                	:1 := string_buffer;
                END;
                """;
        try (CallableStatement statement = this.connection.prepareCall(text)) {
            statement.registerOutParameter("1", Types.VARCHAR);
            statement.execute();
            String result = statement.getString("1");
            System.out.println("show all breakpoint:" + result);
            return result;
        }
    }

    public int getBreakpoint(int line) throws SQLException {
        String text = """
                DECLARE
                    listing SYS.DBMS_DEBUG.breakpoint_table;
                    breakpointinfo SYS.DBMS_DEBUG.breakpoint_info;
                    linenum BINARY_INTEGER;
                    breakpoint BINARY_INTEGER := 0;
                BEGIN
                    linenum := :line;
                    SYS.DBMS_DEBUG.SHOW_BREAKPOINTS(listing);
                    FOR i IN 1 .. listing.count LOOP
                       EXIT WHEN breakpointinfo.line# = linenum;
                       breakpointinfo := listing(i);
                       breakpoint := i;
                    END LOOP;
                    :1 := breakpoint;
                END;
                """;
        try (CallableStatement statement = this.connection.prepareCall(text)) {
            statement.setInt("line", line);
            statement.registerOutParameter("1", Types.INTEGER);
            statement.execute();
            return statement.getInt("1");
        }
    }

    public void deleteBreakPoint(int point) throws SQLException {
        String text = """
                DECLARE
                    retval BINARY_INTEGER;
                    pointnum BINARY_INTEGER;
                BEGIN
                    pointnum := :pointNumber;
                    retval := SYS.DBMS_DEBUG.DELETE_BREAKPOINT(pointnum);
                    :1 := retval;
                END;
                """;
        try (CallableStatement statement = this.connection.prepareCall(text)) {
            statement.setInt("pointNumber", point);
            statement.registerOutParameter("1", Types.INTEGER);
            statement.execute();
            int result = statement.getInt("1");
            if (result == 0) {
                System.out.println("delete break point[" + point + "] success");
            } else {
                throw new SQLException(ExceptionFlags.getMessage(result));
            }
        }
    }


    public void getValue(String... variables) throws SQLException {
        String text = """
                DECLARE
                    value_name VARCHAR2(100);
                	retval binary_integer;
                	value_buffer VARCHAR2(32767) := '';
                BEGIN
                    value_name := :variable;
                	retval := SYS.DBMS_DEBUG.GET_VALUE(value_name, 0, value_buffer);
                	:1 := retval;
                	:2 := value_buffer;
                END;
                """;
        String value;
        try (CallableStatement statement = this.connection.prepareCall(text)) {
            for (String variable : variables) {
                statement.setString("variable", variable);
                statement.registerOutParameter("1", Types.INTEGER);
                statement.registerOutParameter("2", Types.VARCHAR);
                statement.execute();
                int result = statement.getInt("1");
                if (result == ExceptionFlags.success.getCode()) {
                    value = statement.getString("2");
                    System.out.println("get value:" + variable + "=>" + value);
                } else if (result == ExceptionFlags.error_nullvalue.getCode()) {
                    //Value is NULL
                    value = null;
                    System.out.println("get value:" + variable + "=>" + value);
                } else {
                    System.out.println(variable + "=>:" + ExceptionFlags.getMessage(result));
                }
            }
        }
    }

    public void setValue(String variable, Object value) throws SQLException {
        String text = """
                DECLARE
                	retval binary_integer;
                	assignment_statement VARCHAR2(32767) := '';
                BEGIN
                    assignment_statement := :assignment;
                	retval := SYS.DBMS_DEBUG.SET_VALUE(0, assignment_statement);
                	:1 := retval;
                END;
                """;
        try (CallableStatement statement = this.connection.prepareCall(text)) {
            statement.setString("assignment", variable + " := " + value + ";");
            statement.registerOutParameter("1", Types.INTEGER);
            statement.execute();
            int result = statement.getInt("1");
            if (result == 0) {
                System.out.println("setValue[" + variable + " := " + value + ";] success");
            } else {
                throw new SQLException(ExceptionFlags.getMessage(result));
            }
        }
    }

    public void getSource(RunTimeInfo runTimeInfo) throws SQLException {
        String text = """
                DECLARE
                    powner VARCHAR2(100);
                    pname VARCHAR2(255);
                    pline BINARY_INTEGER;
                    psource VARCHAR2(255) := '';
                BEGIN
                    powner := :owner;
                    pname := :name;
                    pline := :line;
                    SELECT text INTO psource FROM all_source WHERE owner = powner AND name  = pname AND line  = pline;
                    :1 := psource;
                END;
                """;
        try (CallableStatement statement = this.connection.prepareCall(text)) {
            statement.setString("owner", runTimeInfo.getProgram().getOwner());
            statement.setString("name", runTimeInfo.getProgram().getName());
            statement.setInt("line", runTimeInfo.getLine());
            statement.registerOutParameter("1", Types.VARCHAR);
            statement.execute();
            String result = statement.getString("1");
            System.out.println("getSource:" + result);
        }
    }

    public RunTimeInfo breakWithException() throws SQLException {
        RunTimeInfo runTimeInfo = this.continueImpl(BreakFlags.break_exception.getCode(), this.connection);
        System.out.println("break when an exception is raised:" + runTimeInfo);
        if (runTimeInfo.getReason() == SuspensionFlags.reason_knl_exit.getCode() || runTimeInfo.getReason() == SuspensionFlags.reason_exit.getCode()) {
            throw new SQLException("Target Session is finished");
        }
        return runTimeInfo;
    }

    // 逐过程
    public RunTimeInfo stepOver() throws SQLException {
        RunTimeInfo runTimeInfo = this.continueImpl(BreakFlags.break_next_line.getCode(), this.connection);
        System.out.println("stepOver:" + runTimeInfo);
        if (runTimeInfo.getReason() == SuspensionFlags.reason_knl_exit.getCode() || runTimeInfo.getReason() == SuspensionFlags.reason_exit.getCode()) {
            throw new SQLException("Target Session is finished");
        }
        return runTimeInfo;
    }

    // 逐语句
    public RunTimeInfo stepInto() throws SQLException {
        RunTimeInfo runTimeInfo = this.continueImpl(BreakFlags.break_any_call.getCode(), this.connection);
        System.out.println("stepInto:" + runTimeInfo);
        if (runTimeInfo.getReason() == SuspensionFlags.reason_knl_exit.getCode() || runTimeInfo.getReason() == SuspensionFlags.reason_exit.getCode()) {
            throw new SQLException("Target Session is finished");
        }
        return runTimeInfo;
    }

    // 跳过
    public RunTimeInfo stepOut() throws SQLException {
        RunTimeInfo runTimeInfo = this.continueImpl(BreakFlags.break_any_return.getCode(), this.connection);
        System.out.println("stepOut:" + runTimeInfo);
        if (runTimeInfo.getReason() == SuspensionFlags.reason_knl_exit.getCode()
                || runTimeInfo.getReason() == SuspensionFlags.reason_exit.getCode()) {
            throw new SQLException("Break Exit...");
        }
        return runTimeInfo;
    }

    // 结束程序
    public RunTimeInfo stepBreak() throws SQLException {
        RunTimeInfo runTimeInfo = this.continueImpl(BreakFlags.break_return.getCode(), this.connection);
        System.out.println("break return:" + runTimeInfo);
        if (runTimeInfo.getReason() == SuspensionFlags.reason_knl_exit.getCode() || runTimeInfo.getReason() == SuspensionFlags.reason_exit.getCode()) {
            throw new SQLException("Target Session is finished");
        }
        return runTimeInfo;
    }

    //abort后再调一次stepContinue会退出调试并关闭session
    public void stepStop() throws SQLException {
        this.continueImpl(BreakFlags.abort_execution.getCode(), this.connection);
        this.continueImpl(BreakFlags.break_continue.getCode(), this.connection);
    }

    //it waits until the target process runs to completion or signals an event.
    private RunTimeInfo continueImpl(int breakFlags, Connection connection) throws SQLException {
        String text = """
                DECLARE
                runinfo dbms_debug.runtime_info;
                retval binary_integer;
                info_requested BINARY_INTEGER;
                BEGIN
                    info_requested := SYS.DBMS_DEBUG.info_getStackDepth + SYS.DBMS_DEBUG.info_getLineInfo + SYS.DBMS_DEBUG.info_getBreakpoint + SYS.DBMS_DEBUG.info_getOerInfo;
                	retval := DBMS_DEBUG.CONTINUE(runinfo,info_requested);
                	:1 := runinfo.terminated;
                END;
                """;
        text = """
                DECLARE
                     run_info SYS.DBMS_DEBUG.RUNTIME_INFO;
                     info_requested BINARY_INTEGER;
                     break_flags BINARY_INTEGER;
                     retval BINARY_INTEGER;
                BEGIN
                    info_requested := SYS.DBMS_DEBUG.info_getStackDepth + SYS.DBMS_DEBUG.info_getLineInfo + SYS.DBMS_DEBUG.info_getBreakpoint + SYS.DBMS_DEBUG.info_getOerInfo;
                    break_flags := :breakFlag;
                    retval := SYS.DBMS_DEBUG.CONTINUE(run_info, break_flags, info_requested);
                    :1 := retval;
                    :line := run_info.line#;
                	:terminated := run_info.terminated;
                	:breakpoint := run_info.breakpoint;
                	:stackdepth := run_info.stackdepth;
                	:interpreterdepth := run_info.interpreterdepth;
                	:reason := run_info.reason;
                	:namespace := run_info.program.namespace;
                	:name := run_info.program.name;
                	:owner := run_info.program.owner;
                	:dblink := run_info.program.dblink;
                	:pline := run_info.program.line#;
                	:libunittype := run_info.program.libunittype;
                	:entrypointname := run_info.program.entrypointname;
                END;
                """;
        try (CallableStatement statement = connection.prepareCall(text)) {
            statement.setInt("breakFlag", breakFlags);
            statement.registerOutParameter("1", Types.INTEGER);
            statement.registerOutParameter("line", Types.INTEGER);
            statement.registerOutParameter("terminated", Types.INTEGER);
            statement.registerOutParameter("breakpoint", Types.INTEGER);
            statement.registerOutParameter("stackdepth", Types.INTEGER);
            statement.registerOutParameter("interpreterdepth", Types.INTEGER);
            statement.registerOutParameter("reason", Types.INTEGER);
            //program
            statement.registerOutParameter("namespace", Types.INTEGER);
            statement.registerOutParameter("name", Types.VARCHAR);
            statement.registerOutParameter("owner", Types.VARCHAR);
            statement.registerOutParameter("dblink", Types.VARCHAR);
            statement.registerOutParameter("pline", Types.INTEGER);
            statement.registerOutParameter("libunittype", Types.INTEGER);
            statement.registerOutParameter("entrypointname", Types.VARCHAR);

            statement.execute();
            int result = statement.getInt("1");
            if (result == 0) { //success
                ProgramInfo programInfo = new ProgramInfo(statement.getInt("namespace"), statement.getString("name"),
                        statement.getString("owner"), statement.getString("dblink"), statement.getInt("pline"),
                        statement.getInt("libunittype"), statement.getString("entrypointname"));
                RunTimeInfo runTimeInfo = new RunTimeInfo(statement.getInt("line"), statement.getInt("terminated"),
                        statement.getInt("breakpoint"), statement.getInt("stackdepth"), statement.getInt("interpreterdepth"),
                        statement.getInt("reason"), programInfo);
                return runTimeInfo;
            } else {
                throw new SQLException(ExceptionFlags.getMessage(result));
            }
        }
    }

    public void detachSession() throws SQLException {
        String text = """
                BEGIN
                    SYS.DBMS_DEBUG.DETACH_SESSION;
                END;
                """;
        try (CallableStatement statement = this.connection.prepareCall(text)) {
            statement.execute();
        }
    }


}
