package io.cliix.abacus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.librato.metrics.BatchResult;
import com.librato.metrics.DefaultHttpPoster;
import com.librato.metrics.HttpPoster;
import com.librato.metrics.LibratoBatch;
import com.librato.metrics.Sanitizer;

public class MetricsPublisher {

    private final ScheduledExecutorService executor;
    private final PublishTask task;

    public MetricsPublisher(String email, String apiToken, String source, MetricsCache cache, long period,
            TimeUnit unit) {
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.task = new PublishTask(email, apiToken, source, cache);
        this.executor.schedule(this.task, period, unit);
    }

    public void shutdown() {
        this.executor.shutdown();
    }

    static class PublishTask implements Runnable {

        private static final String LIBRATO_API_URL = "https://metrics-api.librato.com/v1/metrics";
        private final MetricsCache cache;
        private final HttpPoster httpPoster;
        private final String source;

        public PublishTask(String email, String apiToken, String source, MetricsCache cache) {
            this(new DefaultHttpPoster(LIBRATO_API_URL, email, apiToken), source, cache);
        }

        protected PublishTask(HttpPoster httpPoster, String source, MetricsCache cache) {
            this.httpPoster = httpPoster;
            this.cache = cache;
            this.source = source;
        }

        @Override
        public void run() {
            while (this.cache.size() > 0) {
                LibratoBatch batch = this.createMetricsBatch();
                boolean success = this.publishMetrics(batch);
                if (success) {
                    this.removeMetric();
                } else {
                    break;
                }
            }
        }

        private void removeMetric() {
            this.cache.remove();
        }

        private LibratoBatch createMetricsBatch() {
            LibratoBatch batch =
                    new LibratoBatch(1, Sanitizer.LAST_PASS, 30, TimeUnit.SECONDS, "abacus", this.httpPoster);
            batch.addMeasurement(this.cache.peek());
            return batch;
        }

        private boolean publishMetrics(LibratoBatch batch) {
            BatchResult result = batch.post(this.source);
            return result.success();
        }
    }
}
