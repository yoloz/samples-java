import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 如果你的表在IMPALA启动之前就已存在，那么Impala在启动时会自动加载HIVE里的所有表，就可以直接在Impala中查询HIVE里的表;
 * <p>
 * 如果你的表是在IMPALA运行过程中创建的，那么你需要手动刷新Impala字典缓存(invalidate metadata)，才能识别到新建的表,否则报错:
 * Could not resolve table reference: 'tableName'
 */
public class ImpalaTest {

    public static void main(String[] args) throws IOException {
        ImpalaTest impalaTest = new ImpalaTest();
//        impalaTest.insert();
//        impalaTest.csv();
        impalaTest.query();
    }


    public void csv() throws IOException {
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= 10000; i++) {
            builder.append(i).append(",").append("zhangsan_").append(i);
            list.add(builder.toString());
            builder.setLength(0);
        }
        Files.write(Paths.get(System.getProperty("user.home"), "test_simple"), list);
    }

    /**
     * create table test_par(id int,name string,hobby array<string>,friend map<string,string>,mark struct<math:int,english:int>)
     * row format delimited
     * stored as PARQUET;
     * <p>
     * 通过array,str_to_map,named_struct来包装插入的三种集合数据
     * array('basketball','read'),str_to_map('xiaoming:no,xiaohong:no'),named_struct('math',90,'english',90)
     */
    public void insert() {
        String url = "jdbc:hive2://192.168.1.183:10000/default";
        Properties properties = new Properties();
        properties.put("user", "hdfs");
        properties.put("password", "");
        try (Connection conn = DriverManager.getConnection(url, properties);
             Statement stmt = conn.createStatement()) {
            System.out.println(stmt.executeUpdate("INSERT INTO test_par SELECT 1,'xiaohua',array('basketball','read')," +
                    "str_to_map('xiaoming:no,xiaohong:no'),named_struct('math',90,'english',90)"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * impala-shell -i hostname -d default
     */
    public void query() {
        String url = "jdbc:impala://192.168.1.183:21050/default";
        try {
            Properties properties = new Properties();
            properties.put("user", "hdfs");
            properties.put("password", "");
            Class.forName("com.cloudera.impala.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, properties);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("select * from test_simple")) {
                System.out.println(stmt.getResultSetType() == ResultSet.TYPE_FORWARD_ONLY);
                System.out.println(rs.getFetchSize());  //defaultSize from server,initSize from jdbc url
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


