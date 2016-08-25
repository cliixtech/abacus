package io.cliix.abacus;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.librato.metrics.BatchResult;
import com.librato.metrics.HttpPoster;
import com.librato.metrics.LibratoBatch;
import com.librato.metrics.OkHttpPoster;
import com.librato.metrics.Sanitizer;

public class MetricsPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(MetricsPublisher.class);
    private PublisherRunner runner;
    private Thread publishThread;

    public MetricsPublisher(String email, String apiToken, String source, MetricsCache cache, long period,
            TimeUnit unit) {
        PublishTask task = new PublishTask(email, apiToken, source, cache);
        this.runner = new PublisherRunner(task, period, unit);
        this.publishThread = new Thread(this.runner, "Abacus-MetricsPublisher");
        this.publishThread.setDaemon(true);
        LOG.info("Starting publish task to run periodically after {} {}", period, unit);
        this.publishThread.start();
    }

    public void shutdown() {
        this.runner.stop();
        this.publishThread.interrupt();
    }

    private static class PublisherRunner implements Runnable {
        private boolean run = true;
        private final long sleepMillis;
        private final PublishTask task;

        public PublisherRunner(PublishTask task, long period, TimeUnit unit) {
            this.task = task;
            this.sleepMillis = unit.toMillis(period);
        }

        public void stop() {
            this.run = false;
        }

        @Override
        public void run() {
            while (this.run) {
                try {
                    Thread.sleep(this.sleepMillis);
                    this.task.publish();
                } catch (Throwable e) {
                    LOG.error("Error on Abacus-MetricsPublisher thread", e);
                }
            }
        }
    }

    static class PublishTask {
        private static final String LIBRATO_API_URL = "https://metrics-api.librato.com/v1/metrics";
        private final MetricsCache cache;
        private final HttpPoster httpPoster;
        private final String source;

        public PublishTask(String email, String apiToken, String source, MetricsCache cache) {
            this(new OkHttpPoster(LIBRATO_API_URL, email, apiToken), source, cache);
        }

        protected PublishTask(HttpPoster httpPoster, String source, MetricsCache cache) {
            this.httpPoster = httpPoster;
            this.cache = cache;
            this.source = source;
        }

        public void publish() {
            LOG.info("Publisher running. {} metrics to publish.", this.cache.size());
            while (this.cache.size() > 0) {
                LibratoBatch batch = this.createMetricsBatch();
                boolean success = this.publishMetrics(batch);
                if (success) {
                    this.removeMetric();
                } else {
                    LOG.info("Error publishing metric. {} metrics left.", this.cache.size());
                    break;
                }
            }
            LOG.info("Publisher is going to sleep.");
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
