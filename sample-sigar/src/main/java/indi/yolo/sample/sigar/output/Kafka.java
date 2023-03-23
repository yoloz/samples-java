package indi.yolo.sample.sigar.output;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Properties;


public class Kafka extends Output {

    private final Logger logger = LoggerFactory.getLogger(Kafka.class);

    private final String topic;
    private final String address;
    private KafkaProducer<String, byte[]> producer = null;


    Kafka(String address, String topic) {
        this.topic = topic;
        this.address = address;
        this.createProducer();
    }

    private void createProducer() {
        if (producer == null) {
            Properties props = new Properties();
            props.put("bootstrap.servers", address);
            props.put("key.serializer", StringSerializer.class.getName());
            props.put("value.serializer", ByteArraySerializer.class.getName());
            producer = new KafkaProducer<>(props);
        }
    }

    @Override
    public void apply(String key, byte[] value) {
        if (producer == null) this.createProducer();
        try {
            producer.send(new ProducerRecord<>(topic, key, value), new DefaultCallBack(key, value, logger));
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private class DefaultCallBack implements Callback {

        private final Logger logger;
        private final String key;
        private final byte[] value;


        DefaultCallBack(String key, byte[] value, Logger logger) {
            this.key = key;
            this.value = value;
            this.logger = logger;
        }

        /**
         * A callback method the user can implement to provide asynchronous handling of request completion. This method will
         * be called when the record sent to the server has been acknowledged. Exactly one of the arguments will be
         * non-null.
         *
         * @param metadata  The metadata for the record that was sent (i.e. the partition and offset). Null if an error
         *                  occurred.
         * @param exception The exception thrown during processing of this record. Null if no error occurred.
         *                  Possible thrown exceptions include:
         *                  <p>
         *                  Non-Retriable exceptions (fatal, the message will never be sent):
         *                  <p>
         *                  InvalidTopicException
         *                  OffsetMetadataTooLargeException
         *                  RecordBatchTooLargeException
         *                  RecordTooLargeException
         *                  UnknownServerException
         *                  <p>
         *                  Retriable exceptions (transient, may be covered by increasing #.retries):
         *                  <p>
         *                  CorruptRecordException
         *                  InvalidMetadataException
         *                  NotEnoughReplicasAfterAppendException
         *                  NotEnoughReplicasException
         *                  OffsetOutOfRangeException
         *                  TimeoutException
         */
        @Override
        public void onCompletion(RecordMetadata metadata, Exception exception) {
            if (metadata == null) logger.error(String.format("%s, %s", key,
                    new String(value, Charset.forName("UTF-8"))), exception);
        }
    }

    @Override
    public void close() {
        if (producer != null) {
            producer.close();
        }
        producer = null;
    }
}
