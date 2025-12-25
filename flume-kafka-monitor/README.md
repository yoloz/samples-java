>通过循环访问flume的http请求和kafka的jmx请求获取运行数据

flume-http.properties,kafka-jmx.properties文件是默认的模板,详细信息里面有说明。

调用如下：
```
./flume-kafka-monitor.sh template.properties
```
>kafka 开启JMX：
>>```JMX_PORT=9999 bin/kafka-server-start.sh config/server.properties ```
>>或者修改kafka-server-start.sh,在前面加上``` export JMX_PORT=9999```