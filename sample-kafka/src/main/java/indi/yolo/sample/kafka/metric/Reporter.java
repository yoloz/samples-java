package indi.yolo.sample.kafka.metric;

import kafka.metrics.KafkaMetricsConfig;
import kafka.metrics.KafkaMetricsReporter;
import kafka.metrics.KafkaMetricsReporterMBean;
import kafka.utils.VerifiableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * a jetty server for http get
 */
public class Reporter implements KafkaMetricsReporter, KafkaMetricsReporterMBean {

    private static Logger logger = LoggerFactory.getLogger(Reporter.class);
    private boolean initialized = false;
    private boolean running = false;
    private boolean enabled = false;

    private HttpServer httpServer = null;

    @Override
    public void init(VerifiableProperties verifiableProperties) {

        if (!initialized) {
            KafkaMetricsConfig metricsConfig = new KafkaMetricsConfig(verifiableProperties);
            String bindAddress = verifiableProperties.getProperty("kafka.http.metrics.host");
            if (bindAddress == null || bindAddress.isEmpty()) bindAddress = this.getLocalHost();
            int port = 12584;
            try {
                port = Integer.parseInt(verifiableProperties.getProperty("kafka.http.metrics.port"));
            } catch (NumberFormatException ignored) {
            }
            enabled = Boolean.parseBoolean(verifiableProperties.getProperty("kafka.http.metrics.reporter.enabled"));
            httpServer = new HttpServer(bindAddress, port);
            initialized = true;
            startReporter(metricsConfig.pollingIntervalSecs());
        } else {
            logger.error("Kafka Http Metrics Reporter already initialized");
        }
    }

    @Override
    public synchronized void startReporter(long pollingPeriodSecs) {
        if (initialized && !running && enabled) {
            httpServer.start();
            running = true;
        } else {
            if (!enabled) {
                logger.info("Kafka Http Metrics Reporter disabled");
            } else if (running) {
                logger.error("Kafka Http Metrics Reporter already running");
            }
        }
    }

    @Override
    public synchronized void stopReporter() {
        if (initialized && running) {
            httpServer.stop();
        }
    }

    @Override
    public String getMBeanName() {
        return "kafka:type=report.kafka.metric.http.Reporter";
    }

    private String getLocalHost() {
        String address = "localhost";
        try {
            address = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ignored) {
        }
        return address;
    }
}
