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

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Producer extends Thread {
    private final KafkaProducer<Integer, String> producer;
    private final String topic;
    private final Boolean isAsync;

    Producer(String topic, Boolean isAsync) {
        Properties props = KafkaProperties.getPLAINproperties();
        props.put("bootstrap.servers", KafkaProperties.KAFKA_SERVER_URL + ":" + KafkaProperties.KAFKA_SERVER_PORT);
        props.put("client.id", "DemoProducer");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
        this.topic = topic;
        this.isAsync = isAsync;
    }

    private Producer(String topic, String address) {
        Properties props = KafkaProperties.getPLAINproperties();
        props.put("bootstrap.servers", address);
        props.put("client.id", "DemoProducer");
        props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
        this.topic = topic;
        this.isAsync = false;
    }

//    public void run() {
//        int messageNo = 1;
//        while (true) {
//            String messageStr = "Message_" + messageNo;
//            long startTime = System.currentTimeMillis();
//            if (isAsync) { // Send asynchronously
//                producer.send(new ProducerRecord<>(topic,
//                        messageNo,
//                        messageStr), new DemoCallBack(startTime, messageNo, messageStr));
//            } else { // Send synchronously
//                try {
//                    producer.send(new ProducerRecord<>(topic,
//                            messageNo,
//                            messageStr)).get();
//                    System.out.println("Sent message: (" + messageNo + ", " + messageStr + ")");
//                } catch (InterruptedException | ExecutionException e) {
//                    e.printStackTrace();
//                }
//            }
//            ++messageNo;
//        }
//    }

    /**
     * 每次执行30秒
     * <p>
     * key:counter
     * value:name[msg_'key']time[yyyy-MM-dd HH:mm:ss]
     */
    public void run() {
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int key = 7;
        long timeDiff = 0;
        long startTime = System.currentTimeMillis();
        final String threadName = Thread.currentThread().getName();
        while (timeDiff < 30_000) {
            StringBuilder stringBuilder = new StringBuilder("{")
                    .append("\"name\":\"").append("msg_").append(key).append("\",")
                    .append("\"time\":\"");
            long now = System.currentTimeMillis();
            stringBuilder.append(simpleDateFormat.format(new Date(now))).append("\"}");
            try {
                String msg = stringBuilder.toString();
                producer.send(new ProducerRecord<>(topic, key, msg)).get();
                System.out.printf("%s send message %s\n",
                        threadName,
                        msg);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            timeDiff = now - startTime;
            ++key;
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Producer producerThread = new Producer("test1", "10.68.23.11:9092");
        producerThread.start();
    }
}

class DemoCallBack implements Callback {

    private final long startTime;
    private final int key;
    private final String message;

    public DemoCallBack(long startTime, int key, String message) {
        this.startTime = startTime;
        this.key = key;
        this.message = message;
    }

    /**
     * A callback method the user can implement to provide asynchronous handling of request completion. This method will
     * be called when the record sent to the server has been acknowledged. Exactly one of the arguments will be
     * non-null.
     *
     * @param metadata  The metadata for the record that was sent (i.e. the partition and offset). Null if an error
     *                  occurred.
     * @param exception The exception thrown during processing of this record. Null if no error occurred.
     */
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (metadata != null) {
            System.out.println(
                    "message(" + key + ", " + message + ") sent to partition(" + metadata.partition() +
                            "), " +
                            "offset(" + metadata.offset() + ") in " + elapsedTime + " ms");
        } else {
            exception.printStackTrace();
        }
    }
}
