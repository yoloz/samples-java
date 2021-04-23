package com.yoloz.sample.jdbc;

import java.sql.*;

public class ClickHouseTest {

    public ClickHouseTest() {
    }

    public static void main(String[] args) throws ClassNotFoundException {
        ClickHouseTest clickHouseTest = new ClickHouseTest();
        clickHouseTest.simpleQuery();
    }

    public void simpleQuery() throws ClassNotFoundException {
        String url = "jdbc:clickhouse://192.168.1.189:8123/default";
        String sql = "select * from flow";
        Class.forName("ru.yandex.clickhouse.ClickHouseDriver");
        try (Connection conn = DriverManager.getConnection(url, "default", "client");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println(rs.getType() == ResultSet.TYPE_FORWARD_ONLY);
            // 下面两步不支持
//            System.out.println(rs.getConcurrency() == ResultSet.CONCUR_READ_ONLY);
//            System.out.println(rs.getFetchSize());
            ResultSetMetaData rsmd = rs.getMetaData();
            int size = rsmd.getColumnCount();
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i <= size; i++) {
                builder.append(rsmd.getColumnLabel(i));
                if (i != size) {
                    builder.append(",");
                }
            }
            System.out.println(builder.toString());
            builder.setLength(0);
            while (rs.next()) {
                for (int i = 1; i <= size; i++) {
//                    System.out.println(rsmd.getColumnLabel(i));
//                    System.out.println(rsmd.getColumnName(i));
//                    System.out.println(rsmd.getColumnTypeName(i));
//                    System.out.println(rsmd.getColumnDisplaySize(i));
//                    System.out.println(rsmd.getPrecision(i));
//                    System.out.println(rsmd.getScale(i));
//                    System.out.println(rsmd.getColumnType(i));
//                    System.out.println(rs.getString(i));
                    builder.append(rs.getObject(i));
                    if (i != size) {
                        builder.append(",");
                    }
                }
                System.out.println(builder.toString());
                builder.setLength(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
