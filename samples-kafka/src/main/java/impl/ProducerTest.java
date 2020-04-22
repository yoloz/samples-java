package impl;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerTest {
    private int key;
    private final KafkaProducer<Integer, String> producer;
    private final String topic;

    public ProducerTest(Properties props, String host, String topic) {
        props.put("bootstrap.servers", host);
        props.put("key.serializer", IntegerSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        producer = new KafkaProducer<>(props);
        this.topic = topic;
    }

    public void write(String msg) {
        key += 1;
        try {
            long startTime = System.currentTimeMillis();
            RecordMetadata metadata = producer.send(new ProducerRecord<>(topic, key, msg)).get();
            System.out.println("message(" + key + ", " + msg + ") sent to partition(" + metadata.partition() +
                    "), " + "offset(" + metadata.offset() + ") in " +
                    (System.currentTimeMillis() - startTime) + " ms");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
//        producer.send(new ProducerRecord<>(topic, key, msg), new DemoCallBack(key, msg));
    }

    /**
     * 外部调用关闭要在发送成功之后,否则数据可能没发送成功
     */
    static class DemoCallBack implements Callback {

        private final long startTime;
        private final int key;
        private final String message;

        public DemoCallBack(int key, String message) {
            this.startTime = System.currentTimeMillis();
            this.key = key;
            this.message = message;
        }

        /**
         * A callback method the user can implement to provide asynchronous handling of request completion. This method will
         * be called when the record sent to the server has been acknowledged. When exception is not null in the callback,
         * metadata will contain the special -1 value for all fields except for topicPartition, which will be valid.
         *
         * @param metadata The metadata for the record that was sent (i.e. the partition and offset). An empty metadata
         *                 with -1 value for all fields except for topicPartition will be returned if an error occurred.
         * @param e        The exception thrown during processing of this record. Null if no error occurred.
         */
        public void onCompletion(RecordMetadata metadata, Exception e) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (metadata != null) {
                System.out.println(
                        "message(" + key + ", " + message + ") sent to partition(" + metadata.partition() +
                                "), " +
                                "offset(" + metadata.offset() + ") in " + elapsedTime + " ms");
            } else {
                System.out.println("==message(" + key + ", " + message + ") sent fail[" + e.getMessage() + "]");
            }
        }
    }
}
