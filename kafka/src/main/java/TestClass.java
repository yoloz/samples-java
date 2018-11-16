import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class TestClass {


    public static void main(String[] args) {
        String address = "10.68.23.11:9092";
        String topic = "test2";
        TestClass test = new TestClass();
        test.new Write(address, topic).start();
        test.new Read(address, topic).start();
        try {
            Thread.sleep(5*60*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private class Write extends Thread {

        private final String address;
        private final String topic;

        Write(String address, String topic) {
            this.address = address;
            this.topic = topic;
        }

        /**
         * If this thread was constructed using a separate
         * <code>Runnable</code> run object, then that
         * <code>Runnable</code> object's <code>run</code> method is called;
         * otherwise, this method does nothing and returns.
         * <p>
         * Subclasses of <code>Thread</code> should override this method.
         *
         * @see #start()
         * @see #stop()
         */
        @Override
        public void run() {
            Properties props = KafkaProperties.getPLAINproperties();
            props.put("bootstrap.servers", address);
            props.put("key.serializer", StringSerializer.class.getName());
            props.put("value.serializer", ByteArraySerializer.class.getName());
            KafkaProducer<String, byte[]> producer = new KafkaProducer<>(props);
            String key = "/home/ylzhang/Pictures/应急处置03.png";
            try {
                byte[] value = Files.readAllBytes(Paths.get(key));
                producer.send(new ProducerRecord<>(topic, key, value)).get();
            } catch (IOException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println("write finish......");
        }
    }

    private class Read extends Thread {

        private final String address;
        private final String topic;

        Read(String address, String topic) {
            this.address = address;
            this.topic = topic;
        }

        /**
         * If this thread was constructed using a separate
         * <code>Runnable</code> run object, then that
         * <code>Runnable</code> object's <code>run</code> method is called;
         * otherwise, this method does nothing and returns.
         * <p>
         * Subclasses of <code>Thread</code> should override this method.
         *
         * @see #start()
         * @see #stop()
         */
        @Override
        public void run() {
            Properties props = KafkaProperties.getPLAINproperties();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, address);
            props.put(ConsumerConfig.GROUP_ID_CONFIG, topic);
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
            KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(props);
            consumer.subscribe(Collections.singletonList(topic));
            ConsumerRecords<String, byte[]> records = consumer.poll(1000);
            for (ConsumerRecord<String, byte[]> record : records) {
                System.out.println("Received message: (" + record.key() + ") at offset " + record.offset());
                try {
                    Path pf = Paths.get(record.key()).getParent().resolve(System.currentTimeMillis() + ".png");
                    System.out.println("value write into " + pf.toString());
                    Files.write(pf, record.value());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
