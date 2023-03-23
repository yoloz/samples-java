package indi.yolo.sample.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * common method.
 *
 * @author yoloz
 */
public class Util {

    static void print(ResultSet resultSet) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int colCount = resultSetMetaData.getColumnCount();
        int total = 0;
        StringBuilder header = new StringBuilder();
        while (resultSet.next()) {
            StringBuilder line = new StringBuilder();
            for (int i = 1; i <= colCount; i++) {
                if (total == 0) {
                    header.append(resultSetMetaData.getColumnLabel(i)).append(" ");
                }
                line.append(resultSet.getObject(i)).append(',');
            }
            if (total == 0) {
                System.out.println(header.toString());
            }
            System.out.println(line.substring(0, line.length() - 1));
            total++;
        }
        System.out.println("==========" + total + "============");
    }

    /**
     * get catalogs.
     *
     * @param connection connection
     */
    static void getCatalogs(Connection connection) throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet resultSet = databaseMetaData.getCatalogs();
        print(resultSet);
    }

    static void getSchemas(Connection connection) throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet resultSet = databaseMetaData.getSchemas();
        print(resultSet);
    }

    static void getSchemas(Connection connection, String catalog, String schemaPattern) throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet resultSet = databaseMetaData.getSchemas(catalog, schemaPattern);
        print(resultSet);
    }

    static void getTables(Connection connection, String catalog, String schemaPattern, String tablePattern, String[] types)
            throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet resultSet = databaseMetaData.getTables(catalog, schemaPattern, tablePattern, types);
        print(resultSet);
    }

    static void getColumns(Connection connection, String catalog, String schemaPattern, String tablePattern, String columnPattern)
            throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet resultSet = databaseMetaData.getColumns(catalog, schemaPattern, tablePattern, columnPattern);
        print(resultSet);
    }

    static void getColumn(Connection connection, String selectSql) throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(selectSql)
        ) {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int colCount = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= colCount; i++) {
                System.out.printf("catalog:%s,schema:%s,table:%s,column:%s\n", resultSetMetaData.getCatalogName(i),
                        resultSetMetaData.getSchemaName(i), resultSetMetaData.getTableName(i),
                        resultSetMetaData.getColumnName(i));
            }
        }
    }

    static void getIndexInfo(Connection connection, String catalog, String schema, String table,
                             boolean unique, boolean approximate) throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet resultSet = databaseMetaData.getIndexInfo(catalog, schema, table, unique,approximate);
        print(resultSet);
    }
}
