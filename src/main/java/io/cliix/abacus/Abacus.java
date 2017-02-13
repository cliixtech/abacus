package io.cliix.abacus;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.cliix.abacus.internal.CachedRegistry;
import io.cliix.abacus.internal.InfluxDBPublisher;
import io.cliix.abacus.internal.InternalMetrics;
import io.cliix.abacus.internal.MeasurementsCache;
import io.cliix.abacus.internal.PeriodicTelemetry;

public class Abacus implements Registry, Telemetry {

    private Registry registry;
    private Telemetry telemetry;

    public Abacus(Registry registry, Telemetry telemetry) {
        this.registry = registry;
        this.telemetry = telemetry;
    }

    @Override
    public void publish() {
        this.telemetry.publish();
    }

    @Override
    public void start(long delay, TimeUnit unit) {
        this.telemetry.start(delay, unit);
    }

    @Override
    public void stop() {
        this.telemetry.stop();
    }

    @Override
    public void addMeasurement(String name, double value) {
        this.registry.addMeasurement(name, value);
    }

    @Override
    public void addMeasurement(String name, double value, Map<String, String> tags) {
        this.registry.addMeasurement(name, value, tags);
    }

    public static class Builder {
        private File file;
        private long maxCacheEntries;
        private Publisher publisher;
        private Map<String, String> tags = Collections.emptyMap();

        public Builder cacheFile(File file) {
            this.file = file;
            return this;
        }

        public Builder cacheMaxEntries(long maxCacheEntries) {
            this.maxCacheEntries = maxCacheEntries;
            return this;
        }

        public Builder tags(Map<String, String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder withPublisher(Publisher publisher) {
            this.publisher = publisher;
            return this;
        }

        public Abacus build() throws IOException {
            MeasurementsCache cache = new MeasurementsCache(this.file, this.maxCacheEntries);
            Registry registry = new CachedRegistry(cache, this.tags);
            Telemetry telemetry = new PeriodicTelemetry(this.publisher, cache);

            InternalMetrics internalMetrics = new InternalMetrics(registry);
            this.publisher.setInternalMonitoring(internalMetrics);

            return new Abacus(registry, telemetry);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String url = args[0];
        String user = args[1];
        String pass = args[1];
        File f = new File("/tmp/cache.abacus");

        Publisher influx = new InfluxDBPublisher(url, user, pass, "abacus");
        Abacus abaco = new Abacus.Builder().cacheFile(f).cacheMaxEntries(10000).withPublisher(influx).build();
        abaco.addMeasurement("AbacusManualTesting", .1d);
        abaco.addMeasurement("AbacusManualTesting", .2d);
        abaco.addMeasurement("AbacusManualTesting", .5d);
        abaco.publish();
        Thread.sleep(10000);
    }
}