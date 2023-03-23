package indi.yolo.sample.sigar.output;


public abstract class Output {

    /**
     * @param msg type,kafkaAddress,kafkaTopic
     * @return Output
     */
    public static Output getOutput(String... msg) {
        switch (msg[0]) {
            case "null":
                return new Null();
            case "kafka":
                return new Kafka(msg[1], msg[2]);
            default:
                return new Console();
        }
    }

    public abstract void apply(String key, byte[] value);

    public void close() {
    }
}
