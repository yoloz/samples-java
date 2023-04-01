## how to code UDF

Answer come from newBing

1. To write a UDF function in Hive, you need to create a Java class for the User Defined Function which
   extends `org.apache.hadoop.hive.ql.exec.UDF` and implements more than one `evaluate()` methods¹.
2. Then you need to package your Java class into a JAR file (you can use Maven) and add your JAR to Hive CLI¹.
3. Finally, you can create a temporary function in Hive which points to your Java class¹.

You can find more information about writing custom Hive UDFs and UDAFs in this
[article](https://dzone.com/articles/writing-custom-hive-udf-andudaf)¹.

[Managing Apache Hive User-Defined Functions | 6.3.x - Cloudera.](https://docs.cloudera.com/documentation/enterprise/latest/topics/cm_mc_hive_udf.html)

[Java user-defined function (UDF) with Apache Hive Azure HDInsight.](https://learn.microsoft.com/en-us/azure/hdinsight/hadoop/apache-hadoop-hive-java-udf)

[Hive UDFs - Databricks.](https://kb.databricks.com/en_US/data/hive-udf)

## how to code GenericUDF

Answer come from newBing

To code a GenericUDF, you need to extend the `GenericUDF` class and override two most important methods: `initialize`
and `evaluate`².

The `initialize()` method describes the structure itself and defines data types inside. The `evaluate()` method fills up
the structure with actual values. You don't need any special classes to return, struct<> in Hive is just an array of
objects in Java².

In generic UDFs, all objects are passed around using the Object type. Hive is structured this way so that all code
handling records and cells is generic, and to avoid the costs of instantiating and deserializing objects when it's not
needed. Therefore, all interaction with the data passed in to UDFs is done via ObjectInspectors³.

[How to return Struct from Hive UDF? - Stack Overflow.](https://stackoverflow.com/questions/26026027/how-to-return-struct-from-hive-udf)
[A Complete Guide to Writing Hive UDF - Dataiku.](https://blog.dataiku.com/2013/05/01/a-complete-guide-to-writing-hive-udf)                                
[Hive UDF - Generic UDF for all Primitive Type - Stack Overflow.](https://stackoverflow.com/questions/61194914/hive-udf-generic-udf-for-all-primitive-type)

> - initialize()方法会被输入的每个参数调用，并最终传入到一个ObjectInspector对象中。
    这个方法的目标是确定参数的返回类型。如果传入的方法类型是不合法的，抛出异常、returnOIResolver是一个内置的类，通过获取非null值的变量的类型并使用这个数据类型来确定返回值类型。
> - 方法evaluate的输入是一个DeferredObject对象数组，而initialize方法中创建的returnOIResolver对象用于从DeferredObjects对象中获取到控制。
    在这种情况下，这个函数会返回第1个非null值.
> - getDisplayString(),其用于Hadoop task内部，在使用到这个函数时来展示调试信息

## 临时与永久函数

Hive自定义函数分为临时与永久函数，顾名思义，分别是临时使用和永久有效使用的意思。

### 临时函数

临时函数，关闭会话就结束了生命周期，下次要想使用，需要重新注册。

```bash
zhds@apache250:/opt/hive-2.1.1$ bin/beeline
Beeline version 2.1.1 by Apache Hive
beeline> !connect jdbc:hive2://localhost:10000/default
#...
0: jdbc:hive2://localhost:10000> add jar /home/zhds/hive-udf-1.0-SNAPSHOT.jar;
No rows affected (0.091 seconds)
0: jdbc:hive2://localhost:10000> list jars;
+---------------------------------------+--+
|               resource                |
+---------------------------------------+--+
| /home/zhds/hive-udf-1.0-SNAPSHOT.jar  |
+---------------------------------------+--+
1 row selected (0.085 seconds)
#临时注册UDF函数（hive会话生效）
0: jdbc:hive2://localhost:10000> CREATE TEMPORARY FUNCTION aesencrypt AS 'indi.yolo.sample.hive.udf.generic.AESEncrypt';
No rows affected (0.163 seconds)
0: jdbc:hive2://localhost:10000> select id,aesencrypt(name,'123') as name,hobby,address from test001;
+-----+-----------------------------------+--------+---------------+--+
| id  |               name                | hobby  |    address    |
+-----+-----------------------------------+--------+---------------+--+
| 1   | D9DCF5C07F6FC7EF72F8BC45060719CE  | book   | beijing       |
| 2   | 6B514FACA01B803F95F49392C5373FFA  | tv     | nanjing       |
| 3   | 91047E888B5DE4F65A3701D33AA7FE44  | music  | heilongjiang  |
+-----+-----------------------------------+--------+---------------+--+
3 rows selected (0.307 seconds)
0: jdbc:hive2://localhost:10000> select * from test001;
+-------------+---------------+----------------+------------------+--+
| test001.id  | test001.name  | test001.hobby  | test001.address  |
+-------------+---------------+----------------+------------------+--+
| 1           | xiaoming      | book           | beijing          |
| 2           | lilei         | tv             | nanjing          |
| 3           | lihua         | music          | heilongjiang     |
+-------------+---------------+----------------+------------------+--+
3 rows selected (0.308 seconds)
0: jdbc:hive2://localhost:10000> create table test002(id int,name string,hobby string,address string) row format delimited fields terminated by ',';
No rows affected (0.445 seconds)
# 需要修改hive为本地模式,否则overwrite一直卡住
0: jdbc:hive2://localhost:10000> set hive.exec.mode.local.auto=true;
0: jdbc:hive2://localhost:10000> insert overwrite table test002 select id,aesencrypt(name,'123') as name,hobby,address from test001;
WARNING: Hive-on-MR is deprecated in Hive 2 and may not be available in the future versions. Consider using a different execution engine (i.e. spark, tez) or using Hive 1.X releases.
No rows affected (2.622 seconds)
0: jdbc:hive2://localhost:10000> select * from test002;
+-------------+-----------------------------------+----------------+------------------+--+
| test002.id  |           test002.name            | test002.hobby  | test002.address  |
+-------------+-----------------------------------+----------------+------------------+--+
| 1           | D9DCF5C07F6FC7EF72F8BC45060719CE  | book           | beijing          |
| 2           | 6B514FACA01B803F95F49392C5373FFA  | tv             | nanjing          |
| 3           | 91047E888B5DE4F65A3701D33AA7FE44  | music          | heilongjiang     |
+-------------+-----------------------------------+----------------+------------------+--+
3 rows selected (0.231 seconds)
# 删除临时函数
0: jdbc:hive2://localhost:10000> drop temporary function default.aesencrypt;
No rows affected (0.026 seconds)
0: jdbc:hive2://localhost:10000> select id,aesencrypt(name,'123'),hobby,address from test001;
Error: Error while compiling statement: FAILED: SemanticException [Error 10011]: Invalid function aesencrypt (state=42000,code=10011)

```

### 永久函数

永久函数一旦注册，可以在hive cli，远程连接hiveserver2等地方永久使用，步骤为：

* 先上传jar包到hdfs：`hdfs dfs -put /home/zhds/hive-udf-1.0-SNAPSHOT.jar /input`
* 永久注册:
  `CREATE FUNCTION aesencrypt AS 'indi.yolo.sample.hive.udf.generic.AESEncrypt' USING JAR 'hdfs://apache250:9000/input/hive-udf-1.0-SNAPSHOT.jar';`

> 注意：指定jar包路径需要是hdfs路径,/etc/hosts里配置机器名和ip；
> 关闭`hive cli`再打开以及其他机器使用jdbc连接均可操作此函数。

* 删除永久函数：`drop function default.aesencrypt;`

新增的永久函数，比如在hive cli命令行注册的，可能会在beeline或者hiveserver2远程连接时，提示不存在该函数。
解决办法是，在无法使用UDF的HiveServer2上，执行`reload function`命令，将MetaStore中新增的UDF信息同步到HiveServer2内存中。