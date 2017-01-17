package io.cliix.abacus.internal;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cliix.abacus.Publisher;
import io.cliix.abacus.Telemetry;

public class PeriodicTelemetry implements Telemetry {

    private static final Logger LOG = LoggerFactory.getLogger(PeriodicTelemetry.class);
    private TelemetryRunner runner;
    private Thread publishThread;
    private final Publisher publisher;
    private MeasurementsCache cache;

    public PeriodicTelemetry(Publisher publisher, MeasurementsCache cache) {
        this.publisher = publisher;
        this.cache = cache;
    }

    @Override
    public void publish() {
        this.publisher.publish(this.cache);
    }

    @Override
    public void start(long delay, TimeUnit unit) {
        if (this.publishThread == null) {
            this.runner = new TelemetryRunner(this, delay, unit);
            this.publishThread = new Thread(this.runner, "Abacus-TelemetryRunner");
            this.publishThread.setDaemon(true);
            this.publishThread.start();
            LOG.info("Starting publish task to run periodically after {} {}", delay, unit);
        }
    }

    @Override
    public void stop() {
        if (this.publishThread != null) {
            this.runner.stop();
            this.publishThread.interrupt();
            this.publishThread = null;
        }
    }

    private static class TelemetryRunner implements Runnable {
        private boolean run = true;
        private final long sleepMillis;
        private Telemetry telemetry;

        public TelemetryRunner(Telemetry telemetry, long period, TimeUnit unit) {
            this.telemetry = telemetry;
            this.sleepMillis = unit.toMillis(period);
        }

        public void stop() {
            this.run = false;
        }

        @Override
        public void run() {
            while (this.run) {
                try {
                    this.telemetry.publish();
                    Thread.sleep(this.sleepMillis);
                } catch (Throwable e) {
                    LOG.error("Ops, an error occurred o Abacus-TelemetryRunner thread.", e);
                }
            }
        }
    }

}
