package io.cliix.abacus;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Abacus {

    private MetricsRegistry registry;
    private MetricsPublisher publisher;

    public Abacus(MetricsRegistry registry, MetricsPublisher publisher) {
        this.registry = registry;
        this.publisher = publisher;
    }

    public void addCounterMeasurement(String name, Long value) {
        this.registry.addCounterMeasurement(name, value);
    }

    void addCounterMeasurement(Number period, String name, Long value) {
        this.registry.addCounterMeasurement(period, name, value);
    }

    void addGaugeMeasurement(String name, Number value) {
        this.registry.addGaugeMeasurement(name, value);
    }

    void addGaugeMeasurement(Number period, String name, Number value) {
        this.registry.addGaugeMeasurement(period, name, value);
    }

    void shutdown() {
        this.publisher.shutdown();
    }

    public static class Builder {
        private File file;
        private long maxCacheEntries;
        private String email;
        private String token;
        private long period;
        private TimeUnit unit;
        private String source;

        public Builder libratoEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder libratoToken(String token) {
            this.token = token;
            return this;
        }

        public Builder publishInterval(long interval, TimeUnit unit) {
            this.period = interval;
            this.unit = unit;
            return this;
        }

        public Builder cacheFile(File file) {
            this.file = file;
            return this;
        }

        public Builder cacheMaxEntries(long maxCacheEntries) {
            this.maxCacheEntries = maxCacheEntries;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Abacus build() throws IOException {
            MetricsCache cache = new MetricsCache(this.file, this.maxCacheEntries);
            MetricsRegistry registry = new MetricsRegistry(cache);
            MetricsPublisher publisher =
                    new MetricsPublisher(this.email, this.token, this.source, cache, this.period, this.unit);
            return new Abacus(registry, publisher);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String email = "danilo@cliix.io";
        String token = "0a3e9457dcc6df0a5ed5e494eb1c481ca0b779f632eec99e64d22edbfa95e622";
        File f = new File("/tmp/cache.abacus");

        Abacus abaco =
                new Abacus.Builder()
                        .cacheFile(f)
                        .source("TestPC")
                        .publishInterval(5, TimeUnit.SECONDS)
                        .libratoToken(token)
                        .libratoEmail(email)
                        .cacheMaxEntries(10000)
                        .build();
        abaco.addCounterMeasurement("lalala", 5l);
        Thread.sleep(10000);
    }
}