* create a topic
> bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic test

* list topic
> bin/kafka-topics.sh --list --bootstrap-server localhost:9092

* send message
> bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic test

* start a consumer
> bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning

