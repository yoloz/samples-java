package v_1;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 */
public class HbaseDemo {

    private static final String TABLE_NAME = "indexToUdbTest";
    private static final String CF_DEFAULT = "info";

    static void createOrOverwrite(Admin admin, HTableDescriptor table) throws IOException {
        if (admin.tableExists(table.getTableName())) {
            admin.disableTable(table.getTableName());
            admin.deleteTable(table.getTableName());
        }
        admin.createTable(table);
    }

    static void createSchemaTables(Configuration config) throws IOException {
        try (Connection connection = ConnectionFactory.createConnection(config);
             Admin admin = connection.getAdmin()) {

            HTableDescriptor table = new HTableDescriptor(TableName.valueOf(TABLE_NAME));
            table.addFamily(new HColumnDescriptor(CF_DEFAULT).setCompressionType(Algorithm.NONE));

            System.out.print("Creating table. ");
            createOrOverwrite(admin, table);
            System.out.println(" Done.");
        }
    }

    static void modifySchema(Configuration config) throws IOException {
        try (Connection connection = ConnectionFactory.createConnection(config);
             Admin admin = connection.getAdmin()) {

            TableName tableName = TableName.valueOf(TABLE_NAME);
            if (!admin.tableExists(tableName)) {
                System.out.println("Table does not exist.");
                System.exit(-1);
            }

            HTableDescriptor table = admin.getTableDescriptor(tableName);

            // Update existing table
            HColumnDescriptor newColumn = new HColumnDescriptor("new_column_family");
            newColumn.setCompactionCompressionType(Algorithm.GZ);
            newColumn.setMaxVersions(HConstants.ALL_VERSIONS);
            admin.addColumn(tableName, newColumn);

            // Update existing column family
            HColumnDescriptor existingColumn = new HColumnDescriptor(CF_DEFAULT);
            existingColumn.setCompactionCompressionType(Algorithm.GZ);
            existingColumn.setMaxVersions(HConstants.ALL_VERSIONS);
            table.modifyFamily(existingColumn);
            admin.modifyTable(tableName, table);

            // Disable an existing table
            admin.disableTable(tableName);

            // Delete an existing column family
            admin.deleteColumn(tableName, CF_DEFAULT.getBytes());

            // Delete a table (Need to be disabled first)
            admin.deleteTable(tableName);
        }
    }

    static void putData(Configuration config) {
        try (Connection connection = ConnectionFactory.createConnection(config);
             Table table = connection.getTable(TableName.valueOf(TABLE_NAME))) {
            System.out.println("start put...");
//            List<Put> puts = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, +1);
            for (int i = 1; i <= 5; i++) {
                byte[] rowKey = (i + "").getBytes();
                Put put = new Put(rowKey);
                put.addColumn("info".getBytes(), "id".getBytes(), calendar.getTimeInMillis(), ("3409" + i).getBytes());
                put.addColumn("info".getBytes(), "name".getBytes(), calendar.getTimeInMillis(), ("ethan1_" + i).getBytes());
                put.addColumn("info".getBytes(), "sex".getBytes(), calendar.getTimeInMillis(), ("女").getBytes());
//                puts.add(put);
//                if (puts.size() == 100) {
//                    table.put(puts);
//                    puts.clear();
//                }
                table.put(put);
            }
//            if (puts.size() != 0) {
//                table.put(puts);
//            }
            System.out.println("end put...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void getWithFilter(Configuration config) {
        List<Filter> filters = new ArrayList<>();
        try (Connection connection = ConnectionFactory.createConnection(config);
             Table table = connection.getTable(TableName.valueOf(TABLE_NAME))) {
            Get get = new Get("340323102".getBytes());// 根据rowkey查询
            Filter filter1 = new SingleColumnValueFilter
                    (Bytes.toBytes("info"), null, CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("id")));
            filters.add(filter1);
            Filter filter2 = new SingleColumnValueFilter
                    (Bytes.toBytes("info"), null, CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("name")));
            filters.add(filter2);
            FilterList filterList = new FilterList(filters);
            get.setFilter(filterList);
//            get.setMaxVersions();      //获取所有版本信息
            for (Cell cell : table.get(get).rawCells()) {
                System.out.println("列：" + new String(CellUtil.cloneFamily(cell)) + ":" + new String(CellUtil.cloneQualifier(cell))
                        + "==>:" + new String(CellUtil.cloneValue(cell)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void scanWithFilter(Configuration config) {
        List<Filter> filters = new ArrayList<>();
        try (Connection connection = ConnectionFactory.createConnection(config);
             Table table = connection.getTable(TableName.valueOf(TABLE_NAME))) {
            Scan scan = new Scan("340625421".getBytes(), "340635421".getBytes());
            Filter filter1 = new SingleColumnValueFilter
                    (Bytes.toBytes("info"), null, CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("id")));
            filters.add(filter1);
            Filter filter2 = new SingleColumnValueFilter
                    (Bytes.toBytes("info"), null, CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("name")));
            filters.add(filter2);
            FilterList filterList = new FilterList(filters);
            scan.setFilter(filterList);
//            scan.addColumn(Bytes.toBytes("info"), Bytes.toBytes("id"));
//            scan.addColumn(Bytes.toBytes("info"), Bytes.toBytes("name"));
//            scan.setMaxVersions();
            ResultScanner resultScanner = table.getScanner(scan);
            for (Result r : resultScanner) {
                for (Cell cell : r.rawCells()) {
                    System.out.println("列：" + new String(CellUtil.cloneFamily(cell)) + ":" + new String(CellUtil.cloneQualifier(cell))
                            + "==>:" + new String(CellUtil.cloneValue(cell)));
                }
            }
            resultScanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * setTimeRange:在设置的时间范围内把最新版本的时间戳取出来,与实际最新版本的时间戳没有关系.
     * 如：表中有一个family M,M下有一个列N, M有3个版本, A版本最新,但不在timerange时间范围内, B,C 都在时间范围内,
     * B相对较新,那么在Scan到这个列N的时候会返回B.
     *
     * @param config 配置
     */
    static void scanWithTimeRange(Configuration config) {
        try (Connection connection = ConnectionFactory.createConnection(config);
             Table table = connection.getTable(TableName.valueOf(TABLE_NAME))) {
            Scan scan = new Scan();
            scan.addColumn(Bytes.toBytes("info"), Bytes.toBytes("id"));
            scan.addColumn(Bytes.toBytes("info"), Bytes.toBytes("other"));
            Calendar calendar = Calendar.getInstance();
            long start = calendar.getTimeInMillis();
            calendar.add(Calendar.DATE, +2);
            scan.setTimeRange(start, calendar.getTimeInMillis());
//            scan.setMaxVersions();
            ResultScanner resultScanner = table.getScanner(scan);
            for (Result r : resultScanner) {
                StringBuilder stringBuilder = new StringBuilder(new String(r.getRow()));
                for (Cell cell : r.rawCells()) {
                    stringBuilder.append(new String(CellUtil.cloneFamily(cell))).append(":")
                            .append(new String(CellUtil.cloneQualifier(cell))).append(":")
                            .append(new String(CellUtil.cloneValue(cell))).append(cell.getTimestamp());
                }
                System.out.println(stringBuilder);
            }
            resultScanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * jass是static的,在同一个进程中如果有多个组件使用(hbase,kafka,etc),则需要更新
     * jass文件内容中最后要以分号结尾
     */
    private static void refreshJaas(String filePath) {
        String exist = System.getProperty("java.security.auth.login.config");
        if (exist != null) {
            if (!exist.equals(filePath)) {
                System.setProperty("java.security.auth.login.config", filePath);
                javax.security.auth.login.Configuration.getConfiguration().refresh();
            }
        }
    }

    public static void main(String... args) throws IOException {
        java.nio.file.Path path = Paths.get(System.getProperty("user.dir"), "samples-hbase/src/main/resources");
        PropertyConfigurator.configure(TableInfoDemo.class.getResourceAsStream("/log4j.properties"));

        Configuration config = HBaseConfiguration.create();
        //Add any necessary configuration files (hbase-site.xml, core-site.xml)
        config.addResource(new Path(path.resolve("hbase-site.xml").toString()));
        config.addResource(new Path(path.resolve("core-site.xml").toString()));
        if (User.isHBaseSecurityEnabled(config)) {
//            System.setProperty("java.security.auth.login.config","ss.jass");
//            refreshJaas("xxx");
            System.setProperty("java.security.krb5.conf", "krb5.conf");
            UserGroupInformation.setConfiguration(config);
            UserGroupInformation.loginUserFromKeytab("hdfstest@HADOOP.COM",
                    System.getProperty("user.dir") + File.separator + "conf" + File.separator + "user.keytab");
        }
        createSchemaTables(config);
//        modifySchema(config);
//        putData(config);
//        scanWithTimeRange(config);
    }

}
