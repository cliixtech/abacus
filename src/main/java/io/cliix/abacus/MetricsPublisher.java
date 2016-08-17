package io.cliix.abacus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.librato.metrics.LibratoBatch;

public class MetricsPublisher {

    private ScheduledExecutorService executor;
    private PublishTask task;

    public MetricsPublisher(String email, String apiToken, MetricsCache cache, long period,
            TimeUnit unit) {
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.task = new PublishTask(email, apiToken, cache);
        executor.schedule(this.task, period, unit);
    }

    public void shutdown() {
        this.executor.shutdown();
    }

    private class PublishTask implements Runnable {

        private String email;
        private String apiToken;
        private MetricsCache cache;

        public PublishTask(String email, String apiToken, MetricsCache cache) {
            this.email = email;
            this.apiToken = apiToken;
            this.cache = cache;
        }

        @Override
        public void run() {
            LibratoBatch batch = this.createMetricsBatch();
            this.publishMetrics(batch);
            this.cleanCache();
        }

        private LibratoBatch createMetricsBatch() {
            return null;
        }

        private boolean publishMetrics(LibratoBatch batch) {
            return false;
        }

        private void cleanCache() {
            
        }
    }
}
