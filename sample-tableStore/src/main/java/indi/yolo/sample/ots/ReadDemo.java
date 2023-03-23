package indi.yolo.sample.ots;

import com.alicloud.openservices.tablestore.ClientConfiguration;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.AlwaysRetryStrategy;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.CreateTableRequest;
import com.alicloud.openservices.tablestore.model.CreateTableResponse;
import com.alicloud.openservices.tablestore.model.GetRangeRequest;
import com.alicloud.openservices.tablestore.model.GetRangeResponse;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyBuilder;
import com.alicloud.openservices.tablestore.model.PrimaryKeySchema;
import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.PutRowRequest;
import com.alicloud.openservices.tablestore.model.RangeIteratorParameter;
import com.alicloud.openservices.tablestore.model.RangeRowQueryCriteria;
import com.alicloud.openservices.tablestore.model.RetryStrategy;
import com.alicloud.openservices.tablestore.model.Row;
import com.alicloud.openservices.tablestore.model.RowPutChange;
import com.alicloud.openservices.tablestore.model.TableMeta;
import com.alicloud.openservices.tablestore.model.TableOptions;

import java.util.Arrays;
import java.util.Iterator;

/**
 *
 */
public class ReadDemo {

    //create client define configuration
    private String endPoint = "http://xxxxxxx.ots.aliyuncs.com";
    private String accessKeyId = "xxxxxxx";
    private String accessKeySecret = "xxxxxxxxxxxx";
    private String instanceName = "uni-test";
    //custom define configuration about client
    private int connectTimeoutInMills = 0;
    private int socketTimeoutInMills = 0;
    private int maxRetryTimes = 0;
    private int maxRetryPauseInMillis = 0;

    //create table define configuration
    private String tableName = "test";
    private String twoPkTableName = "test1";


    /**
     * about aliyun tablestore(ots)
     * user custom define configuration
     *
     * @return 配置信息（{@link ClientConfiguration}）
     */
    private ClientConfiguration customConfig() {
        ClientConfiguration conf = new ClientConfiguration();
        if (connectTimeoutInMills > 0) conf.setConnectionTimeoutInMillisecond(connectTimeoutInMills);
        if (socketTimeoutInMills > 0) conf.setSocketTimeoutInMillisecond(socketTimeoutInMills);
        if (maxRetryTimes > 0) {
            RetryStrategy retryStrategy = new AlwaysRetryStrategy(maxRetryTimes,
                    maxRetryPauseInMillis > 0 ? maxRetryPauseInMillis : 1000);
            conf.setRetryStrategy(retryStrategy);
        }
        return conf;
    }

    /**
     * get sync client
     *
     * @return 同步客户端 ({@link SyncClient})
     */
    private SyncClient getSyncClient() {
        ClientConfiguration conf = this.customConfig();
        return new SyncClient(endPoint, accessKeyId,
                accessKeySecret, instanceName, conf);
    }

    /**
     * 范围读
     *
     * @param startPkValue 起始pk
     * @param endPkValue   结束pk
     */
    private void getRange(String startPkValue, String endPkValue) {
        SyncClient client = this.getSyncClient();
        RangeRowQueryCriteria rangeRowQueryCriteria = new RangeRowQueryCriteria(tableName);
        // 设置起始主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        if (startPkValue != null && !startPkValue.isEmpty())
            primaryKeyBuilder.addPrimaryKeyColumn("zjhm", PrimaryKeyValue.fromString(startPkValue));
        else primaryKeyBuilder.addPrimaryKeyColumn("zjhm", PrimaryKeyValue.INF_MIN);
        rangeRowQueryCriteria.setInclusiveStartPrimaryKey(primaryKeyBuilder.build());
        // 设置结束主键
        primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        if (endPkValue != null && !endPkValue.isEmpty())
            primaryKeyBuilder.addPrimaryKeyColumn("zjhm", PrimaryKeyValue.fromString(endPkValue));
        else primaryKeyBuilder.addPrimaryKeyColumn("zjhm", PrimaryKeyValue.INF_MAX);
        rangeRowQueryCriteria.setExclusiveEndPrimaryKey(primaryKeyBuilder.build());
        rangeRowQueryCriteria.setMaxVersions(1);
        System.out.println("GetRange的结果为:");
        while (true) {
            GetRangeResponse getRangeResponse = client.getRange(new GetRangeRequest(rangeRowQueryCriteria));
            for (Row row : getRangeResponse.getRows()) {
                System.out.println(row);
            }
            // 若nextStartPrimaryKey不为null, 则继续读取.
            if (getRangeResponse.getNextStartPrimaryKey() != null) {
                rangeRowQueryCriteria.setInclusiveStartPrimaryKey(getRangeResponse.getNextStartPrimaryKey());
            } else {
                break;
            }
        }
        client.shutdown();
    }

    /**
     * 迭代读的底层实现是范围读
     *
     * @param startPkValue 起始pk
     * @param endPkValue   结束pk
     */
    private void getRangeByIterator(String startPkValue, String endPkValue) {
        SyncClient client = this.getSyncClient();
        RangeIteratorParameter rangeIteratorParameter = new RangeIteratorParameter(tableName);
        // 设置起始主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        if (startPkValue != null && !startPkValue.isEmpty())
            primaryKeyBuilder.addPrimaryKeyColumn("zjhm", PrimaryKeyValue.fromString(startPkValue));
        else primaryKeyBuilder.addPrimaryKeyColumn("zjhm", PrimaryKeyValue.INF_MIN);
        rangeIteratorParameter.setInclusiveStartPrimaryKey(primaryKeyBuilder.build());
        // 设置结束主键
        primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        if (endPkValue != null && !endPkValue.isEmpty())
            primaryKeyBuilder.addPrimaryKeyColumn("zjhm", PrimaryKeyValue.fromString(endPkValue));
        else primaryKeyBuilder.addPrimaryKeyColumn("zjhm", PrimaryKeyValue.INF_MAX);
        rangeIteratorParameter.setExclusiveEndPrimaryKey(primaryKeyBuilder.build());
        rangeIteratorParameter.setMaxVersions(1);
//        rangeIteratorParameter.setMaxCount(1);
        Iterator<Row> iterator = client.createRangeIterator(rangeIteratorParameter);
        System.out.println("使用Iterator进行GetRange的结果为:");
        while (iterator.hasNext()) {
            Row row = iterator.next();
//            System.out.println(Arrays.toString(row.getColumns()));
            System.out.println(row);
        }
        client.shutdown();
    }

    private void getRangeByIteratorTwoPk(String startPkValue, int startId, String endPkValue, int endId) {
        SyncClient client = this.getSyncClient();
        RangeIteratorParameter rangeIteratorParameter = new RangeIteratorParameter(twoPkTableName);
        // 设置起始主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKeyBuilder.addPrimaryKeyColumn("zjhm", PrimaryKeyValue.fromString(startPkValue));
        primaryKeyBuilder.addPrimaryKeyColumn("id", PrimaryKeyValue.fromLong(startId));
        rangeIteratorParameter.setInclusiveStartPrimaryKey(primaryKeyBuilder.build());
        // 设置结束主键
        if (endPkValue != null && !endPkValue.isEmpty()) {
            primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
            primaryKeyBuilder.addPrimaryKeyColumn("zjhm", PrimaryKeyValue.fromString(endPkValue));
            primaryKeyBuilder.addPrimaryKeyColumn("id", PrimaryKeyValue.fromLong(endId));
            rangeIteratorParameter.setExclusiveEndPrimaryKey(primaryKeyBuilder.build());
        }
        rangeIteratorParameter.setMaxVersions(1);
        Iterator<Row> iterator = client.createRangeIterator(rangeIteratorParameter);
        System.out.println("使用Iterator进行GetRange的结果为:");
        while (iterator.hasNext()) {
            Row row = iterator.next();
            row.getPrimaryKey();
            System.out.println("row========" + row);
            System.out.println("属性列======" + Arrays.toString(row.getColumns()));
            System.out.println("primaryKey======" + row.getPrimaryKey());
        }
        client.shutdown();
    }

    /**
     * 创建多pk表
     */
    void createTwoPkTableName() {
        TableMeta tableMeta = new TableMeta(twoPkTableName);
        tableMeta.addPrimaryKeyColumn(
                new PrimaryKeySchema("zjhm", PrimaryKeyType.STRING));
        tableMeta.addPrimaryKeyColumn(
                new PrimaryKeySchema("id", PrimaryKeyType.INTEGER));
        TableOptions tableOptions = new TableOptions(24 * 3600, 1);
        CreateTableRequest request = new CreateTableRequest(tableMeta, tableOptions);
        SyncClient client = this.getSyncClient();
        CreateTableResponse response = client.createTable(request);
        System.out.println("create table: " + response.toString());
        client.shutdown();
    }

    /**
     * 插入多主键数据
     *
     * @param client   SyncClient
     * @param pkValue1 pk1
     * @param pkValue2 pk2
     */
    void putTwoPkRow(SyncClient client, String pkValue1, int pkValue2) {
        // 构造主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKeyBuilder.addPrimaryKeyColumn("zjhm",
                PrimaryKeyValue.fromString(pkValue1));
        primaryKeyBuilder.addPrimaryKeyColumn("id",
                PrimaryKeyValue.fromLong(pkValue2));
        PrimaryKey primaryKey = primaryKeyBuilder.build();
        RowPutChange rowPutChange = new RowPutChange(twoPkTableName, primaryKey);
        //加入一些属性列
        for (int i = 0; i < 3; i++) {
            rowPutChange.addColumn(new Column("Col" + i, ColumnValue.fromLong(i)));
        }
        client.putRow(new PutRowRequest(rowPutChange));
    }

    public static void main(String[] args) {
//        ReadDemo readDemo = new ReadDemo();
//        WriteDemo writeDemo = new WriteDemo();
//        writeDemo.putRow("330324199010120538",0);
//        writeDemo.putRow("330324199010120539",0);
//        writeDemo.putRow("330324199010120540",0);
//        writeDemo.putRow("330324199010120541",0);
//        writeDemo.putRow("330324199010120542",0);
//        writeDemo.putRow("330324199010120543",0);
//        readDemo.getRangeByIterator("330324199010120538", "330324199010120545");
//        readDemo.getRangeByIterator(null, "");

//        readDemo.createTwoPkTableName();
//        try {
//            Thread.sleep(60 * 1000);
//        } catch (InterruptedException e) { //创建表后要等待一会才可使用
//            e.printStackTrace();
//        }
//        SyncClient syncClient = readDemo.getSyncClient();
//        readDemo.putTwoPkRow(syncClient, "330324199010120538", 1);
//        readDemo.putTwoPkRow(syncClient, "330324199010120539", 2);
//        readDemo.putTwoPkRow(syncClient, "330324199010120540", 3);
//        readDemo.putTwoPkRow(syncClient, "330324199010120541", 4);
//        readDemo.putTwoPkRow(syncClient, "330324199010120542", 5);
//        readDemo.putTwoPkRow(syncClient, "330324199010120543", 6);
//        syncClient.shutdown();
//        readDemo.getRangeByIteratorTwoPk("330324199010120538", 1,
//                "330324199010120545", 1);
//        readDemo.getRangeByIteratorTwoPk("330324199010120538", 1,
//                "330324199010120538", 8);
        //多pk值的时候,第一种情况取出了全部数据,第二种情况取出了一条数据,优先首个主键范围.
        //然而调用api的时候却需要对所有pk都要设置起始值.


    }

}
