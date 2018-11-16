package com.unimas.aliyun.tablestore.demo;

import com.alicloud.openservices.tablestore.AsyncClient;
import com.alicloud.openservices.tablestore.ClientConfiguration;
import com.alicloud.openservices.tablestore.ClientException;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.TableStoreCallback;
import com.alicloud.openservices.tablestore.model.AlwaysRetryStrategy;
import com.alicloud.openservices.tablestore.model.BatchWriteRowRequest;
import com.alicloud.openservices.tablestore.model.BatchWriteRowResponse;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.CreateTableRequest;
import com.alicloud.openservices.tablestore.model.CreateTableResponse;
import com.alicloud.openservices.tablestore.model.GetRowRequest;
import com.alicloud.openservices.tablestore.model.GetRowResponse;
import com.alicloud.openservices.tablestore.model.ListTableResponse;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyBuilder;
import com.alicloud.openservices.tablestore.model.PrimaryKeySchema;
import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.PutRowRequest;
import com.alicloud.openservices.tablestore.model.PutRowResponse;
import com.alicloud.openservices.tablestore.model.RetryStrategy;
import com.alicloud.openservices.tablestore.model.Row;
import com.alicloud.openservices.tablestore.model.RowPutChange;
import com.alicloud.openservices.tablestore.model.RowUpdateChange;
import com.alicloud.openservices.tablestore.model.SingleRowQueryCriteria;
import com.alicloud.openservices.tablestore.model.TableMeta;
import com.alicloud.openservices.tablestore.model.TableOptions;
import com.alicloud.openservices.tablestore.model.UpdateRowRequest;
import com.alicloud.openservices.tablestore.model.UpdateRowResponse;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 *
 */
public class WriteDemo {

    //create client define configuration
    private String endPoint = "http://uni-test.cn-hangzhou.ots.aliyuncs.com";
    private String accessKeyId = "LTAIgEnMMHdqE5Mf";
    private String accessKeySecret = "x7T02hN98bW9OEtWt0g7Rosu4hMYx1";
    private String instanceName = "uni-test";
    private boolean sync = false;
    //custom define configuration about client
    private int connectTimeoutInMills = 0;
    private int socketTimeoutInMills = 0;
    private int maxRetryTimes = 0;
    private int maxRetryPauseInMillis = 0;

    //create table define configuration
    private String tableName = "test";
    //主键的名称及类型
    private Map<String, String> primaryKey = Collections.singletonMap("zjhm", "string");
    //数据的过期时间, 单位秒, -1代表永不过期. 假如设置过期时间为一年, 即为 365 * 24 * 3600
    private int timeToLive = 24 * 3600;
    // 保存的最大版本数, 设置为3即代表每列上最多保存3个最新的版本
    private int maxVersions = 3;


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
     * get async client
     *
     * @return 异步客户端 ({@link AsyncClient})
     */
    private AsyncClient getAsyncClient() {
        ClientConfiguration conf = this.customConfig();
        return new AsyncClient(endPoint, accessKeyId,
                accessKeySecret, instanceName, conf);
    }

    /**
     * create table
     * 建表后服务端需要将表的分片加载到某个节点上,因此需要等待几秒钟才能对表进行读写,否则会抛出异常.
     */
    void createTable() {
        TableMeta tableMeta = new TableMeta(tableName);
        this.primaryKey.forEach((k, v) -> {
            switch (v) {
                case "string":
                    tableMeta.addPrimaryKeyColumn(
                            new PrimaryKeySchema(k, PrimaryKeyType.STRING));
                    break;
                case "int":
                    tableMeta.addPrimaryKeyColumn(
                            new PrimaryKeySchema(k, PrimaryKeyType.INTEGER));
                    break;
                default:
                    tableMeta.addPrimaryKeyColumn(
                            new PrimaryKeySchema(k, PrimaryKeyType.BINARY));
                    break;
            }
        });
        TableOptions tableOptions = new TableOptions(timeToLive, maxVersions);
        CreateTableRequest request = new CreateTableRequest(tableMeta, tableOptions);
        // 设置读写预留值,若不设置则读写预留值均默认为0,容量型实例不支持预留吞吐量的设置.
//        request.setReservedThroughput(new ReservedThroughput(new CapacityUnit(1, 1)));
        if (sync) {
            SyncClient client = this.getSyncClient();
            CreateTableResponse response = client.createTable(request);
            System.out.println("create table: " + response.toString());
            client.shutdown();
        } else {
            AsyncClient client = this.getAsyncClient();
            try {
                CreateTableResponse response = client.createTable(request, null).get();
                System.out.println("create table: " + response.toString());
            } catch (InterruptedException e) {
                throw new ClientException(String.format(
                        "The thread was interrupted: %s", e.getMessage()));
            } catch (ExecutionException e) {
                throw new ClientException("The thread was aborted", e);
            }
            client.shutdown();
        }
    }

    /**
     * 列出表名
     * 需要创建客户端连接
     */
    void listTable() {
        ListTableResponse response;
        if (sync) {
            SyncClient client = this.getSyncClient();
            response = client.listTable();
            for (String tableName : response.getTableNames()) {
                System.out.printf("%s ", tableName);
            }
            client.shutdown();
        } else {
            AsyncClient client = this.getAsyncClient();
            try {
                response = client.listTable(null).get();
            } catch (InterruptedException e) {
                throw new ClientException(String.format(
                        "The thread was interrupted: %s", e.getMessage()));
            } catch (ExecutionException e) {
                throw new ClientException("The thread was aborted", e);
            }
            for (String tableName : response.getTableNames()) {
                System.out.printf("%s ", tableName);
            }
            client.shutdown();
        }
    }

    /**
     * 获取一行数据
     * 仅做此次写测试所用
     */
    void getRow(String pkValue) {
        // 构造主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKey.forEach((k, v) -> {
            switch (v) {
                case "string":
                    primaryKeyBuilder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromString(pkValue));
//                    primaryKeyBuilder.addPrimaryKeyColumn(k,
//                            PrimaryKeyValue.fromString("330324199010120538"));
                    break;
                case "int":
                    primaryKeyBuilder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromLong(330324199010120538l));
                    break;
                default:
                    primaryKeyBuilder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromBinary("330324199010120538"
                                    .getBytes(Charset.forName("utf-8"))));
                    break;
            }

        });
        PrimaryKey primaryKey = primaryKeyBuilder.build();
        // 读一行
        SingleRowQueryCriteria criteria = new SingleRowQueryCriteria(tableName, primaryKey);
        // 设置读取最新版本
        criteria.setMaxVersions(maxVersions);
        GetRowResponse getRowResponse;
        if (sync) {
            SyncClient client = this.getSyncClient();
            getRowResponse = client.getRow(new GetRowRequest(criteria));
            Row row = getRowResponse.getRow();
            System.out.println("读取完毕, 结果为: " + row.toString());
            client.shutdown();
        } else {
            AsyncClient client = this.getAsyncClient();
            try {
                getRowResponse = client.getRow(new GetRowRequest(criteria), null).get();
            } catch (InterruptedException e) {
                throw new ClientException(String.format(
                        "The thread was interrupted: %s", e.getMessage()));
            } catch (ExecutionException e) {
                throw new ClientException("The thread was aborted", e);
            }
            Row row = getRowResponse.getRow();
            System.out.println("读取完毕, 结果为: " + row.toString());
            client.shutdown();
        }

    }

    /**
     * 插入数据
     * 插入一行数据,如果原来该行已经存在,会覆盖原来的一行
     * 要实现多版本插入,需要在put一次请求中提交,通过手动设置时间戳,如下：
     * for (int i = 0; i < 10; i++) {
     * for (int j = 0; j < 3; j++) {
     * rowPutChange.addColumn(new Column("Col" + i, ColumnValue.fromLong(j), ts + j));
     * }
     * }
     */
    void putRow(String pkValue, long timestamp) {
        // 构造主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKey.forEach((k, v) -> {
            switch (v) {
                case "string":
                    primaryKeyBuilder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromString(pkValue));
//                    primaryKeyBuilder.addPrimaryKeyColumn(k,
//                            PrimaryKeyValue.fromString("330324199010120538"));
                    break;
                case "int":
                    primaryKeyBuilder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromLong(330324199010120538l));
                    break;
                default:
                    primaryKeyBuilder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromBinary("330324199010120538"
                                    .getBytes(Charset.forName("utf-8"))));
                    break;
            }

        });
        PrimaryKey primaryKey = primaryKeyBuilder.build();
        RowPutChange rowPutChange = new RowPutChange(tableName, primaryKey);
        //加入一些属性列
        for (int i = 0; i < 3; i++) {
            if (timestamp > 0)
                rowPutChange.addColumn(new Column("Col" + i, ColumnValue.fromLong(i), timestamp));
            else rowPutChange.addColumn(new Column("Col" + i, ColumnValue.fromLong(i)));
        }
        if (sync) {
            SyncClient client = this.getSyncClient();
            client.putRow(new PutRowRequest(rowPutChange));
            client.shutdown();
        } else {
            AsyncClient client = this.getAsyncClient();
            client.putRow(new PutRowRequest(rowPutChange),
                    new TableStoreCallback<PutRowRequest, PutRowResponse>() {
                        @Override
                        public void onCompleted(PutRowRequest req, PutRowResponse res) {
                            System.out.println("putRow success...........");
                            client.shutdown();
                        }

                        @Override
                        public void onFailed(PutRowRequest req, Exception ex) {
                            System.out.println("putRow fail..........." + ex.getMessage());
                            client.shutdown();
                        }
                    });
        }

    }

    /**
     * 插入数据
     * 要实现多版本插入,需要在put一次请求中提交,通过手动设置时间戳,如下：
     * for (int i = 0; i < 10; i++) {
     * for (int j = 0; j < 3; j++) {
     * rowPutChange.addColumn(new Column("Col" + i, ColumnValue.fromLong(j), ts + j));
     * }
     * }
     */
    void putRowWithMutiVersion(String pkValue) {
        // 构造主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKey.forEach((k, v) -> {
            switch (v) {
                case "string":
                    primaryKeyBuilder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromString(pkValue));
//                    primaryKeyBuilder.addPrimaryKeyColumn(k,
//                            PrimaryKeyValue.fromString("330324199010120538"));
                    break;
                case "int":
                    primaryKeyBuilder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromLong(330324199010120538l));
                    break;
                default:
                    primaryKeyBuilder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromBinary("330324199010120538"
                                    .getBytes(Charset.forName("utf-8"))));
                    break;
            }

        });
        PrimaryKey primaryKey = primaryKeyBuilder.build();
        RowPutChange rowPutChange = new RowPutChange(tableName, primaryKey);
        //加入一些属性列
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                long ts = System.currentTimeMillis() + (j * 1000);
                System.out.println(ts);
                rowPutChange.addColumn(new Column("Col" + i, ColumnValue.fromLong(i), ts));
            }
        }
        if (sync) {
            SyncClient client = this.getSyncClient();
            client.putRow(new PutRowRequest(rowPutChange));
            client.shutdown();
        } else {
            AsyncClient client = this.getAsyncClient();
            client.putRow(new PutRowRequest(rowPutChange),
                    new TableStoreCallback<PutRowRequest, PutRowResponse>() {
                        @Override
                        public void onCompleted(PutRowRequest req, PutRowResponse res) {
                            System.out.println("putRow success...........");
                            client.shutdown();
                        }

                        @Override
                        public void onFailed(PutRowRequest req, Exception ex) {
                            System.out.println("putRow fail..........." + ex.getMessage());
                            client.shutdown();
                        }
                    });
        }

    }

    /**
     * 插入数据
     */
    void putRowWithVersion(String pkValue, long ts, String... colName) {
        // 构造主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKey.forEach((k, v) -> {
            switch (v) {
                case "string":
                    primaryKeyBuilder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromString(pkValue));
//                    primaryKeyBuilder.addPrimaryKeyColumn(k,
//                            PrimaryKeyValue.fromString("330324199010120538"));
                    break;
                case "int":
                    primaryKeyBuilder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromLong(330324199010120538l));
                    break;
                default:
                    primaryKeyBuilder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromBinary("330324199010120538"
                                    .getBytes(Charset.forName("utf-8"))));
                    break;
            }

        });
        PrimaryKey primaryKey = primaryKeyBuilder.build();
        RowPutChange rowPutChange = new RowPutChange(tableName, primaryKey);
        //加入一些属性列
        for (String s : colName) {
            rowPutChange.addColumn(new Column(s, ColumnValue.fromString(s), ts));
        }
        if (sync) {
            SyncClient client = this.getSyncClient();
            client.putRow(new PutRowRequest(rowPutChange));
            client.shutdown();
        } else {
            AsyncClient client = this.getAsyncClient();
            client.putRow(new PutRowRequest(rowPutChange),
                    new TableStoreCallback<PutRowRequest, PutRowResponse>() {
                        @Override
                        public void onCompleted(PutRowRequest req, PutRowResponse res) {
                            System.out.println("putRow success...........");
                            client.shutdown();
                        }

                        @Override
                        public void onFailed(PutRowRequest req, Exception ex) {
                            System.out.println("putRow fail..........." + ex.getMessage());
                            client.shutdown();
                        }
                    });
        }

    }

    /**
     * 更新数据
     */
    void updateRow(String pkValue) {
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKey.forEach((k, v) -> {
            switch (v) {
                case "string":
                    primaryKeyBuilder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromString(pkValue));
//                    primaryKeyBuilder.addPrimaryKeyColumn(k,
//                            PrimaryKeyValue.fromString("330324199010120538"));
                    break;
                case "int":
                    primaryKeyBuilder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromLong(330324199010120538l));
                    break;
                default:
                    primaryKeyBuilder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromBinary("330324199010120538"
                                    .getBytes(Charset.forName("utf-8"))));
                    break;
            }

        });
        PrimaryKey primaryKey = primaryKeyBuilder.build();
        RowUpdateChange rowUpdateChange = new RowUpdateChange(tableName, primaryKey);
        for (int i = 0; i < 3; i++) {
            rowUpdateChange.put(new Column("Col" + i, ColumnValue.fromString("value" + i)));
        }
        if (sync) {
            SyncClient client = this.getSyncClient();
            client.updateRow(new UpdateRowRequest(rowUpdateChange));
            client.shutdown();
        } else {
            AsyncClient client = this.getAsyncClient();
            client.updateRow(new UpdateRowRequest(rowUpdateChange), new TableStoreCallback<UpdateRowRequest, UpdateRowResponse>() {
                @Override
                public void onCompleted(UpdateRowRequest req, UpdateRowResponse res) {
                    System.out.println("updateRow success...........");
                    client.shutdown();
                }

                @Override
                public void onFailed(UpdateRowRequest req, Exception ex) {
                    System.out.println("updateRow fail..........." + ex.getMessage());
                    client.shutdown();
                }
            });
        }
    }

    /**
     * 更新数据
     * 更新指定版本数据
     */
    void updateRowWithVersion(String pkValue, String colName, long ts) {
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKey.forEach((k, v) -> {
            switch (v) {
                case "string":
                    primaryKeyBuilder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromString(pkValue));
//                    primaryKeyBuilder.addPrimaryKeyColumn(k,
//                            PrimaryKeyValue.fromString("330324199010120538"));
                    break;
                case "int":
                    primaryKeyBuilder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromLong(330324199010120538l));
                    break;
                default:
                    primaryKeyBuilder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromBinary("330324199010120538"
                                    .getBytes(Charset.forName("utf-8"))));
                    break;
            }

        });
        PrimaryKey primaryKey = primaryKeyBuilder.build();
        RowUpdateChange rowUpdateChange = new RowUpdateChange(tableName, primaryKey);
        rowUpdateChange.put(new Column(colName, ColumnValue.fromString("valueUpdate"), ts));
        if (sync) {
            SyncClient client = this.getSyncClient();
            client.updateRow(new UpdateRowRequest(rowUpdateChange));
            client.shutdown();
        } else {
            AsyncClient client = this.getAsyncClient();
            client.updateRow(new UpdateRowRequest(rowUpdateChange), new TableStoreCallback<UpdateRowRequest, UpdateRowResponse>() {
                @Override
                public void onCompleted(UpdateRowRequest req, UpdateRowResponse res) {
                    System.out.println("updateRow success...........");
                    client.shutdown();
                }

                @Override
                public void onFailed(UpdateRowRequest req, Exception ex) {
                    System.out.println("updateRow fail..........." + ex.getMessage());
                    client.shutdown();
                }
            });
        }
    }

    /**
     * BatchWriteRow 接口可以在一次请求中进行批量的写入操作,写入操作包括 PutRow、UpdateRow 和 DeleteRow,
     * 也支持一次对多张表进行写入.
     * <p>
     * 调用 BatchWriteRow 接口时,需要特别注意的是检查返回值.因为是批量写入,可能存在部分行成功部分行失败的情况,
     * 此时失败行的 Index 及错误信息在返回的 BatchWriteRowResponse 中,而并不抛出异常.
     * 可通过 BatchWriteRowResponse 的 isAllSucceed 方法判断是否全部成功.若不检查,可能会忽略掉部分操作的失败.
     * 另一方面,BatchWriteRow 接口也是可能抛出异常的.比如,服务端检查到某些操作出现参数错误,可能会抛出参数错误的异常,
     * 在抛出异常的情况下该请求中所有的操作都未执行.
     */
    void batchWriteRow() {
        BatchWriteRowRequest batchWriteRowRequest = new BatchWriteRowRequest();
        // 构造rowPutChange1
        PrimaryKeyBuilder pk1Builder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKey.forEach((k, v) -> {
            switch (v) {
                case "string":
                    pk1Builder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromString("330324199010120542"));
                    break;
                case "int":
                    pk1Builder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromLong(330324199010120542l));
                    break;
                default:
                    pk1Builder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromBinary("330324199010120542"
                                    .getBytes(Charset.forName("utf-8"))));
                    break;
            }

        });
        RowPutChange rowPutChange1 = new RowPutChange(tableName, pk1Builder.build());
        // 添加一些列
        for (int i = 0; i < 3; i++) {
            rowPutChange1.addColumn(new Column("Col" + i, ColumnValue.fromLong(i)));
        }
        // 添加到batch操作中
        batchWriteRowRequest.addRowChange(rowPutChange1);
        // 构造rowPutChange2
        PrimaryKeyBuilder pk2Builder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKey.forEach((k, v) -> {
            switch (v) {
                case "string":
                    pk2Builder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromString("330324199010120541"));
                    break;
                case "int":
                    pk2Builder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromLong(330324199010120541l));
                    break;
                default:
                    pk2Builder.addPrimaryKeyColumn(k,
                            PrimaryKeyValue.fromBinary("330324199010120541"
                                    .getBytes(Charset.forName("utf-8"))));
                    break;
            }

        });
        RowPutChange rowPutChange2 = new RowPutChange(tableName, pk2Builder.build());
        // 添加一些列
        for (int i = 0; i < 3; i++) {
            rowPutChange2.addColumn(new Column("Col" + i, ColumnValue.fromLong(i)));
        }
        // 添加到batch操作中
        batchWriteRowRequest.addRowChange(rowPutChange2);
        if (sync) {
            SyncClient client = this.getSyncClient();
            BatchWriteRowResponse response = client.batchWriteRow(batchWriteRowRequest);
            System.out.println("是否全部成功:" + response.isAllSucceed());
            if (!response.isAllSucceed()) {
                for (BatchWriteRowResponse.RowResult rowResult : response.getFailedRows()) {
                    System.out.println("失败的行:" + batchWriteRowRequest.getRowChange(rowResult.getTableName(), rowResult.getIndex()).getPrimaryKey());
                    System.out.println("失败原因:" + rowResult.getError());
                }
                /**
                 * 可以通过createRequestForRetry方法再构造一个请求对失败的行进行重试.这里只给出构造重试请求的部分.
                 * 推荐的重试方法是使用SDK的自定义重试策略功能, 支持对batch操作的部分行错误进行重试. 设定重试策略后, 调用接口处即不需要增加重试代码.
                 */
//                BatchWriteRowRequest retryRequest = batchWriteRowRequest.createRequestForRetry(response.getFailedRows());
            }
            client.shutdown();
        } else {
            AsyncClient client = this.getAsyncClient();
            client.batchWriteRow(batchWriteRowRequest, new TableStoreCallback<BatchWriteRowRequest, BatchWriteRowResponse>() {
                @Override
                public void onCompleted(BatchWriteRowRequest req, BatchWriteRowResponse res) {
                    System.out.println("是否全部成功:" + res.isAllSucceed());
                    if (!res.isAllSucceed()) {
                        for (BatchWriteRowResponse.RowResult rowResult : res.getFailedRows()) {
                            System.out.println("失败的行:" + batchWriteRowRequest.getRowChange(rowResult.getTableName(), rowResult.getIndex()).getPrimaryKey());
                            System.out.println("失败原因:" + rowResult.getError());
                        }
                        /**
                         * 可以通过createRequestForRetry方法再构造一个请求对失败的行进行重试.这里只给出构造重试请求的部分.
                         * 推荐的重试方法是使用SDK的自定义重试策略功能, 支持对batch操作的部分行错误进行重试. 设定重试策略后, 调用接口处即不需要增加重试代码.
                         */
//                BatchWriteRowRequest retryRequest = batchWriteRowRequest.createRequestForRetry(response.getFailedRows());
                    }
                    client.shutdown();
                }

                @Override
                public void onFailed(BatchWriteRowRequest req, Exception ex) {
                    System.out.println("batchWriteFail: " + ex.getMessage());
                    client.shutdown();
                }
            });
        }


    }

    public static void main(String[] args) {
        WriteDemo writeDemo = new WriteDemo();
//        writeDemo.createTable();
//        writeDemo.listTable();
//
//        //put测试开始================
//        for (int i = 0; i < 3; i++) {
//            writeDemo.putRow("330324199010120538", 0);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        writeDemo.getRow("330324199010120538");
//        //结果直接被覆盖,不会生成多版本数据
//
//        for (int i = 0; i < 3; i++) {
//            long ts = System.currentTimeMillis();
//            System.out.println(ts);
//            writeDemo.putRow("330324199010120539", ts);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        writeDemo.getRow("330324199010120539");
//        //直接被覆盖,不会生成多版本数据
//
//        writeDemo.putRowWithMutiVersion("330324199010120540");
//        writeDemo.getRow("330324199010120540");
//        //一次put中传入多版本数据,可以获取多个版本数据
//        writeDemo.putRow("330324199010120540", 0);
//        writeDemo.getRow("330324199010120540");
//        //多个版本下一次put也是直接被覆盖(只剩最后一次put的数据)
//        writeDemo.getRow("330324199010120538");
//      /*  [PrimaryKey:]zjhm:330324199010120538
//                [Columns:](Name:Col0,Value:0,Timestamp:1504419513288)
//        (Name:Col1,Value:1,Timestamp:1504419513288)(Name:Col2,Value:2,Timestamp:1504419513288)*/
//        writeDemo.putRowWithVersion("330324199010120538", 1504419513288l, "Col1");
//        writeDemo.getRow("330324199010120538");
//      /*  [PrimaryKey:]zjhm:330324199010120538
//                [Columns:](Name:Col1,Value:Col1,Timestamp:1504419513288)*/
//        //put测试结束================
//
//        //update测试开始==============
//        writeDemo.putRowWithMutiVersion("330324199010120540");
//        writeDemo.getRow("330324199010120540");
///*        [PrimaryKey:]zjhm:330324199010120540
//                [Columns:](Name:Col0,Value:0,Timestamp:1504425894147)
//        (Name:Col0,Value:0,Timestamp:1504425893147)(Name:Col0,Value:0,Timestamp:1504425892143)
//        (Name:Col1,Value:1,Timestamp:1504425894147)(Name:Col1,Value:1,Timestamp:1504425893147)
//        (Name:Col1,Value:1,Timestamp:1504425892147)(Name:Col2,Value:2,Timestamp:1504425894148)
//        (Name:Col2,Value:2,Timestamp:1504425893148)(Name:Col2,Value:2,Timestamp:1504425892148)*/
//        writeDemo.updateRow("330324199010120540");
//        writeDemo.getRow("330324199010120540");
///*        [PrimaryKey:]zjhm:330324199010120540
//                [Columns:](Name:Col0,Value:value0,Timestamp:1504426008092)
//        (Name:Col0,Value:0,Timestamp:1504425894147)(Name:Col0,Value:0,Timestamp:1504425893147)
//        (Name:Col1,Value:value1,Timestamp:1504426008092)(Name:Col1,Value:1,Timestamp:1504425894147)
//        (Name:Col1,Value:1,Timestamp:1504425893147)(Name:Col2,Value:value2,Timestamp:1504426008092)
//        (Name:Col2,Value:2,Timestamp:1504425894148)(Name:Col2,Value:2,Timestamp:1504425893148)*/
//        //update更新版本满的数据时候,更新最新版本的数据
//
//        writeDemo.getRow("330324199010120539");
//      /*  [PrimaryKey:]zjhm:330324199010120539
//                [Columns:](Name:Col0,Value:0,Timestamp:1504426453502)
//        (Name:Col1,Value:1,Timestamp:1504426453502)(Name:Col2,Value:2,Timestamp:1504426453502)*/
//        writeDemo.updateRow("330324199010120539");
//        writeDemo.getRow("330324199010120539");
//      /*  [PrimaryKey:]zjhm:330324199010120539
//                [Columns:](Name:Col0,Value:value0,Timestamp:1504426558514)
//        (Name:Col0,Value:0,Timestamp:1504426453502)(Name:Col1,Value:value1,Timestamp:1504426558514)
//        (Name:Col1,Value:1,Timestamp:1504426453502)(Name:Col2,Value:value2,Timestamp:1504426558514)
//        (Name:Col2,Value:2,Timestamp:1504426453502)*/
//        //update更新版本未满的数据时候,插入更新的数据(即生成新版本数据,老版本数据仍在)
//
//        writeDemo.getRow("330324199010120540");
//       /* [PrimaryKey:]zjhm:330324199010120540
//                [Columns:](Name:Col0,Value:value0,Timestamp:1504426008092)
//        (Name:Col0,Value:0,Timestamp:1504425894147)(Name:Col0,Value:0,Timestamp:1504425893147)
//        (Name:Col1,Value:value1,Timestamp:1504426008092)(Name:Col1,Value:1,Timestamp:1504425894147)
//        (Name:Col1,Value:1,Timestamp:1504425893147)(Name:Col2,Value:value2,Timestamp:1504426008092)
//        (Name:Col2,Value:2,Timestamp:1504425894148)(Name:Col2,Value:2,Timestamp:1504425893148)*/
//        writeDemo.updateRowWithVersion("330324199010120540", "Col1", 1504425893147l);
//        writeDemo.getRow("330324199010120540");
//       /* [PrimaryKey:]zjhm:330324199010120540
//                [Columns:](Name:Col0,Value:value0,Timestamp:1504426008092)
//        (Name:Col0,Value:0,Timestamp:1504425894147)(Name:Col0,Value:0,Timestamp:1504425893147)
//        (Name:Col1,Value:value1,Timestamp:1504426008092)(Name:Col1,Value:1,Timestamp:1504425894147)
//        (Name:Col1,Value:valueUpdate,Timestamp:1504425893147)(Name:Col2,Value:value2,Timestamp:1504426008092)
//        (Name:Col2,Value:2,Timestamp:1504425894148)(Name:Col2,Value:2,Timestamp:1504425893148)*/
//        writeDemo.getRow("330324199010120539");
//       /* [PrimaryKey:]zjhm:330324199010120539
//                [Columns:](Name:Col0,Value:value0,Timestamp:1504426558514)
//        (Name:Col0,Value:0,Timestamp:1504426453502)(Name:Col1,Value:value1,Timestamp:1504426558514)
//        (Name:Col1,Value:1,Timestamp:1504426453502)(Name:Col2,Value:value2,Timestamp:1504426558514)
//        (Name:Col2,Value:2,Timestamp:1504426453502)*/
//        writeDemo.updateRowWithVersion("330324199010120539", "Col2", 1504426453502l);
//        writeDemo.getRow("330324199010120539");
//      /*  [PrimaryKey:]zjhm:330324199010120539
//                [Columns:](Name:Col0,Value:value0,Timestamp:1504426558514)
//        (Name:Col0,Value:0,Timestamp:1504426453502)(Name:Col1,Value:value1,Timestamp:1504426558514)
//        (Name:Col1,Value:1,Timestamp:1504426453502)(Name:Col2,Value:value2,Timestamp:1504426558514)
//        (Name:Col2,Value:valueUpdate,Timestamp:1504426453502)*/
//        //update更新指定属性列的具体版本数据
//        //update测试结束==============
//        //batchWrite测试开始==========
//        writeDemo.batchWriteRow();
//        writeDemo.getRow("330324199010120541");
//        writeDemo.getRow("330324199010120542");
//        //batchWrite测试结束==========
//
//        Double b = 0.24;
//        System.out.println(b.toString());
    }
}
