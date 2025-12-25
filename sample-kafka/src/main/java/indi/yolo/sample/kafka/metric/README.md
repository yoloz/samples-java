Kafka Http Metrics Reporter
==============================

This is a http metrics reporter for kafka using
Jetty with the metrics servlets (https://github.com/dropwizard/metrics) for exposing the metrics as JSON Objects.
Instead of retrieving metrics through JMX with JConsole it is now possible to retrieve the metrics with curl or some other http / rest client.
Code is used at kafka_2.11-0.10.1.1

Install On Broker
------------

1. Build the `kafka-http-metrics-reporter-0.11.0.0.jar` jar using `mvn package` or download [here](https://github.com/arnobroekhof/kafka-http-metrics-reporter/archive/kafka_2.12-0.11.0.0.zip) .
2. Copy the jar kafka-http-metrics-reporter-0.11.0.0.jar to the libs/
   directory of your kafka broker installation
3. Configure the broker (see the configuration section below)
4. Restart the broker

Configuration
------------

Edit the `server.properties` file of your installation, activate the reporter by setting:

```
    kafka.metrics.reporters=nl.techop.kafka.KafkaHttpMetricsReporter[,kafka.metrics.KafkaCSVMetricsReporter[,....]]
    kafka.http.metrics.reporter.enabled=true
    kafka.http.metrics.host=localhost
    kafka.http.metrics.port=8080
```

URL List
------------

| *url* | *description* |
|:-----:|:-------------:|
| /api/metrics | exposes the state of the metrics in a particular registry as a JSON object. |

Usage Examples
------------

### Curl

```bash
   curl -XGET -H "Content-type: application/json" -H "Accept: application/json" "http://localhost:8080/api/metrics"

```
