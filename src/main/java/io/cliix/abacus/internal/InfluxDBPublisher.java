package io.cliix.abacus.internal;

import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cliix.abacus.Clock;
import io.cliix.abacus.Measurement;
import io.cliix.abacus.Publisher;

public class InfluxDBPublisher implements Publisher {
    private static final Logger LOG = LoggerFactory.getLogger(InfluxDBPublisher.class);
    private String dbName;
    private String url;
    private String user;
    private String password;
    private InternalMetrics monitoring;

    public InfluxDBPublisher(String influxUrl, String user, String password, String dbName) {
        this.url = influxUrl;
        this.user = user;
        this.password = password;
        this.dbName = dbName;
    }

    private InfluxDB influx() {
        return InfluxDBFactory.connect(this.url, this.user, this.password);
    }

    @Override
    public void setInternalMonitoring(InternalMetrics monitoring) {
        this.monitoring = monitoring;
    }

    @Override
    public void publish(MeasurementsCache cache) {
        long cacheSize = cache.size();
        this.monitoring.cacheSize(cacheSize);
        LOG.info("Publisher running. {} metrics to publish.", cacheSize);
        long startTime = Clock.now();
        try {
            while (cache.size() > 0) {
                BatchPoints batch = this.createMetricsBatch(cache.peek());
                this.publishMetrics(batch);
                cache.remove();
            }
            this.monitoring.publishSuccess();
        } catch (Exception e) {
            LOG.error("Error publishing metrics", e);
            this.monitoring.publishFailure();
        } finally {
            this.monitoring.publishTime(Clock.millisSince(startTime));
        }
        LOG.info("Publisher is going to sleep.");
    }

    private BatchPoints createMetricsBatch(Measurement measurement) {
        BatchPoints batch =
                BatchPoints
                        .database(dbName)
                        .tag("async", "true")
                        .retentionPolicy("default")
                        .consistency(ConsistencyLevel.ALL)
                        .build();
        Point point = Point
                .measurement(measurement.getName())
                .time(measurement.getTime(), TimeUnit.MILLISECONDS)
                .tag("source", measurement.getSource())
                .addField("value", measurement.getValue())
                .build();
        batch.point(point);
        return batch;
    }

    private void publishMetrics(BatchPoints batch) {
        this.influx().write(batch);
    }

}
