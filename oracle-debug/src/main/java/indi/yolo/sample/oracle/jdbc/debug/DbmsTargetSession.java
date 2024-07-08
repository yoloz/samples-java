package indi.yolo.sample.oracle.jdbc.debug;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * @author yolo
 */
public class DbmsTargetSession {

    private final Connection connection;

    public DbmsTargetSession(Connection connection) {
        this.connection = connection;
    }

    //There are two ways to ensure that debug information is generated: through a session switch, or through individual recompilation
    public void switchSession() throws SQLException {
        try (Statement statement = this.connection.createStatement()) {
            statement.execute("ALTER SESSION SET PLSQL_DEBUG = true");
        }
    }

    public String initialize() throws SQLException {
        String text = """
                BEGIN
                    :retvar := SYS.DBMS_DEBUG.INITIALIZE(null, 0);
                END;
                """;
        try (CallableStatement statement = this.connection.prepareCall(text)) {
            statement.registerOutParameter("retvar", Types.VARCHAR);
            statement.execute();
            return statement.getString("retvar");
        }
    }

    public void enableDebug() throws SQLException {
        String text = """
                BEGIN
                    SYS.DBMS_DEBUG.DEBUG_ON(true,false);
                END;
                """;
        try (CallableStatement statement = this.connection.prepareCall(text)) {
            statement.execute();
        }
    }

    public void execute(String sql) throws SQLException {
        try (Statement statement = this.connection.createStatement()) {
            statement.execute(sql);
        }
    }

}
