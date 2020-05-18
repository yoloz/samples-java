package v_1;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

/**
 * 参照api整理而成,简单的测试过,
 * 一些情况(几百列,取出的一行数据能否获取到无数据的列等)实际使用的时候注意下,
 * 返回的列未有类型信息(cell中的数据是没有类型的,全部是字节码形式存储),如有需要外部得提供映射关系；
 * <p>
 * <p>
 */

public class TableInfoDemo {


    public static void main(String[] args) throws IOException {

        java.nio.file.Path path = Paths.get(System.getProperty("user.dir"), "samples-hbase/src/main/resources");
        PropertyConfigurator.configure(v_3.TableInfoDemo.class.getResourceAsStream("/log4j.properties"));

        List<String> tables = Lists.newArrayList();//数据库表信息
        Map<String, Map<String, List<String>>> allInfo = Maps.newHashMap();//所有信息

        Configuration config = HBaseConfiguration.create();
        //由于测试环境是hadoop nn模式,故而添加hdfs-site.xml
        config.addResource(new Path(path.resolve("hdfs-site.xml").toString()));
        config.addResource(new Path(path.resolve("hbase-site.xml").toString()));
        config.addResource(new Path(path.resolve("core-site.xml").toString()));

        try (Connection connect = ConnectionFactory.createConnection(config)) {

            Admin admin = connect.getAdmin();
            NamespaceDescriptor[] namespace = admin.listNamespaceDescriptors();

            for (NamespaceDescriptor descriptor : namespace) {

                if (descriptor.getName().equals(NamespaceDescriptor.SYSTEM_NAMESPACE_NAME_STR))
                    continue; //系统内建表,包括namespace和meta表
                HTableDescriptor[] tableDescriptors = admin.listTableDescriptorsByNamespace(descriptor.getName());

                for (HTableDescriptor tableDescriptor : tableDescriptors) {
                    TableName tableName = tableDescriptor.getTableName();
                    String tableNameStr = tableName.getNamespaceAsString() + TableName.NAMESPACE_DELIM +
                            tableName.getQualifierAsString();
                    tables.add(tableNameStr);
                    allInfo.put(tableNameStr, getColumns(connect, tableDescriptor));
                }
            }
        }

        System.out.println(new Gson().toJson(tables, new TypeToken<List<String>>() {
        }.getType()));

        System.out.println(new Gson().toJson(allInfo,
                new TypeToken<Map<String, Map<String, List<String>>>>() {
                }.getType()));
    }

    /**
     * 实际运用时在点击获取表结构的时候才执行
     *
     * @param connect          connection
     * @param hTableDescriptor tableDescriptor
     * @return {family:[col1,col2...]}
     * @throws IOException e
     */
    private static Map<String, List<String>> getColumns(Connection connect, HTableDescriptor hTableDescriptor) throws IOException {
        Scan scan = new Scan();
        //>=2.0可以scan.setOneRowLimit()取出一行数据
        scan.setSmall(true);
        scan.setMaxResultsPerColumnFamily(1);

        try (Table tableConnector = connect.getTable(hTableDescriptor.getTableName())) {

            ResultScanner resultScanner = tableConnector.getScanner(scan);
            Result result = resultScanner.next();
            if (result != null) {
                String rowKey = Bytes.toString(result.getRow());
                resultScanner.close();
                result = tableConnector.get(new Get(Bytes.toBytes(rowKey)));
                return translateByteToStr(result.getNoVersionMap());
            } else {
                Map<String, List<String>> resultM = Maps.newHashMap();
                //HColumnDescriptor @Deprecated remove it in 3.0
                HColumnDescriptor[] hColumnDescriptors = hTableDescriptor.getColumnFamilies();
                for (HColumnDescriptor columnDescriptor : hColumnDescriptors) {
                    resultM.put(columnDescriptor.getNameAsString(), Lists.newArrayListWithCapacity(0));
                }
                return resultM;
            }
        }
    }

    private static Map<String, List<String>> translateByteToStr(NavigableMap<byte[], NavigableMap<byte[], byte[]>> map) {
        Map<String, List<String>> result = Maps.newHashMapWithExpectedSize(map.size());
        map.forEach((k, v) -> {
            String family = Bytes.toString(k);
            result.put(family, Lists.newArrayListWithCapacity(v.size()));
            v.keySet().forEach(col -> result.get(family).add(Bytes.toString(col)));
        });
        return result;
    }
}
