kafka生产者消费者简单用例
``` sh
./kafkaClientTest.sh ip:9092 topic msg #先写入后读取  
./kafkaClientTest.sh ip:9092 topic producer msg #写入消息
./kafkaClientTest.sh ip:9092 topic consumer #读取消息
```
> 读取消息earliest且一直循环,ctrl+c退出