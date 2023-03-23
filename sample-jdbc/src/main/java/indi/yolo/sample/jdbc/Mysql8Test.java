package indi.yolo.sample.jdbc;

import com.mysql.cj.jdbc.result.ResultSetImpl;
import com.mysql.cj.protocol.ResultsetRows;

import java.sql.*;
import java.util.Random;

/**
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
//        mysql8Test.insertInto();

        String url = "jdbc:mysql://192.168.1.116:3306/test";
        try (Connection conn = DriverManager.getConnection(url, "test", "dcap123")) {
            Util.getCatalogs(conn);
//            Util.getTables(conn, "test", null, "%", null);
//            Util.getColumns(conn,"test",null,"baoxian","%");
//            Util.getColumn(conn,"select * from baoxian limit 1");
        }
    }

    //字符混淆
    public void insertInto() {
        int cons = 0x7fffffff;
        String sql = "INSERT INTO test.confusionCharTest(identify, value)VALUES(?, ?);";
        sql = "select * from confusionCharTest t where t.value like ?";
        sql = "INSERT INTO test.IntegerTest(origin, confusion)VALUES(?, ?);";
        sql = "SELECT * FROM IntegerTest it where it.confusion<0 && it.confusion > ? order by it.origin";
        sql = "SELECT identify,name from TestMix";
//        sql = "INSERT INTO TestMix(identify,name)values(?,?);";
        String url = "jdbc:mysql://192.168.1.116:3306/test";
        try (Connection conn = DriverManager.getConnection(url, "test", "dcap123");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, "测试中文");
//            pstmt.setString(2, "测试中文");
//            pstmt.addBatch();
//            pstmt.setString(1, "测试中文");
//            pstmt.setString(2, new String(reverseChar("测试中文".toCharArray())));
//            pstmt.addBatch();
//            pstmt.setString(1, "Test中文");
//            pstmt.setString(2, "Test中文");
//            pstmt.addBatch();
//            pstmt.setString(1, "Test中文");
//            pstmt.setString(2, new String(reverseChar("Test中文".toCharArray())));
//            pstmt.addBatch();
//            pstmt.executeBatch();

//            String where = new String(reverseChar("Test".toCharArray())) + '%';
//            pstmt.setString(1, where);
//            ResultSet rs = pstmt.executeQuery();
//            Util.print(rs);

//            Random random = new Random();
//            for (int i = 0; i < 30; i++) {
//                int j = random.nextInt();
//                pstmt.setInt(1, j);
//                pstmt.setInt(2, j ^ cons);
//                pstmt.addBatch();
//            }
//            pstmt.executeBatch();

//            pstmt.setInt(1, -1431737016 ^ cons);
//            ResultSet rs = pstmt.executeQuery();
//            Util.print(rs);

            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int colCount = resultSetMetaData.getColumnCount();
            int total = 0;
            StringBuilder header = new StringBuilder();
            while (rs.next()) {
                StringBuilder line = new StringBuilder();
                for (int i = 1; i <= colCount; i++) {
                    if (total == 0) {
                        header.append(resultSetMetaData.getColumnLabel(i)).append(" ");
                    }
                    if (i == 1) {
                        line.append(rs.getInt(i) ^ cons).append(',');
                    } else if (i == 2) {
                        line.append(reverseChar(new String(rs.getBytes(i)).toCharArray())).append(" ");
                    } else {
                        line.append(rs.getObject(i)).append(',');
                    }
                }
                if (total == 0) {
                    System.out.println(header.toString());
                }
                System.out.println(line.substring(0, line.length() - 1));
                total++;
            }
            System.out.println("==========" + total + "============");

//            pstmt.setInt(1, 1 ^ cons);
//            pstmt.setString(2, new String(reverseChar("文章第一节".toCharArray())));
//            pstmt.addBatch();
//            pstmt.setInt(1, 2 ^ cons);
//            pstmt.setString(2, new String(reverseChar("文章第二节".toCharArray())));
//            pstmt.addBatch();
//            pstmt.setInt(1, 3 ^ cons);
//            pstmt.setString(2, new String(reverseChar("文章第三节".toCharArray())));
//            pstmt.addBatch();
//            pstmt.setInt(1, 4 ^ cons);
//            pstmt.setString(2, new String(reverseChar("文章第四节".toCharArray())));
//            pstmt.addBatch();
//            pstmt.setInt(1, 5 ^ cons);
//            pstmt.setString(2, new String(reverseChar("气候越来越复杂多变".toCharArray())));
//            pstmt.addBatch();
//            pstmt.executeBatch();

//            pstmt.setInt(1, 1 ^ cons);
//            pstmt.setString(2, new String(new char[]{0xdd15})); //56085
//            pstmt.addBatch();
//            pstmt.setInt(1, 2 ^ cons);
//            pstmt.setString(2, new String(new char[]{0xdd16}));//56086
//            pstmt.addBatch();
//            pstmt.setInt(1, 3 ^ cons);
//            pstmt.setString(2, new String(new char[]{0xaa17}));
//            pstmt.addBatch();
//            pstmt.setInt(1, 4 ^ cons);
//            pstmt.setString(2, new String(new char[]{0xbb18}));
//            pstmt.addBatch();
//            pstmt.setInt(1, 5 ^ cons);
//            pstmt.setString(2, new String(new char[]{0xee19}));
//            pstmt.addBatch();
//            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private char[] reverseChar(char[] chars) {
        for (int i = 0; i < chars.length; i++) {
            String hexB = Integer.toHexString(chars[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            char[] temp = hexB.toCharArray();
            String hexStr = new String(new char[]{temp[2], temp[3], temp[0], temp[1]});
            System.out.println(hexB + "=========" + hexStr);
            chars[i] = (char) Integer.parseInt(hexStr, 16);
        }
        return chars;
    }

    public void queryCondition() {
        String sql = "SELECT * FROM  baoxian b limit 10";
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
    public void queryAll() {
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
