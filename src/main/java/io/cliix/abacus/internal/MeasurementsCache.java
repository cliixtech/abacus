package io.cliix.abacus.internal;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.tape.FileObjectQueue;
import com.squareup.tape.ObjectQueue;
import com.squareup.tape.ObjectQueue.Listener;

import io.cliix.abacus.Measurement;

public class MeasurementsCache implements InternalMonitoring {

    private static final Logger LOG = LoggerFactory.getLogger(MeasurementsCache.class);
    private final FileObjectQueue<Measurement> diskQ;
    private final Lock monitor = new ReentrantLock();
    private CacheSizeListener cacheSizeListener;

    public MeasurementsCache(File cacheFile, long cacheMaxEntries) throws IOException {
        this.diskQ = new FileObjectQueue<>(cacheFile, new MeasurementGsonConverter());
        this.cacheSizeListener = new CacheSizeListener(this, cacheMaxEntries);
        this.diskQ.setListener(this.cacheSizeListener);
    }

    public void add(Measurement entry) {
        this.monitor.lock();
        try {
            this.diskQ.add(entry);
        } finally {
            this.monitor.unlock();
        }
    }

    public Measurement peek() {
        this.monitor.lock();
        try {
            return this.diskQ.peek();
        } finally {
            this.monitor.unlock();
        }
    }

    public void remove() {
        this.monitor.lock();
        try {
            this.diskQ.remove();
        } finally {
            this.monitor.unlock();
        }
    }

    public int size() {
        this.monitor.lock();
        try {
            return this.diskQ.size();
        } finally {
            this.monitor.unlock();
        }
    }

    @Override
    public void setInternalMonitoring(InternalMetrics internalMetrics) {
        this.cacheSizeListener.setInternalMonitoring(internalMetrics);
    }

    private class CacheSizeListener implements Listener<Measurement>, InternalMonitoring {

        private MeasurementsCache cache;
        private long maxEntries;
        private InternalMetrics monitoring;

        public CacheSizeListener(MeasurementsCache metricsCache, long cacheMaxEntries) {
            this.cache = metricsCache;
            this.maxEntries = cacheMaxEntries;
        }

        @Override
        public void onAdd(ObjectQueue<Measurement> queue, Measurement entry) {
            if (this.isCacheBiggerThanItShould()) {
                LOG.info("Ops, cache reached its limits, removing oldest metric");
                this.trimCache();
            }
        }

        private boolean isCacheBiggerThanItShould() {
            return this.cache.size() > this.maxEntries;
        }

        private void trimCache() {
            for(int i = 0; i < 10; i++) {
                this.cache.remove();
            }
            this.monitoring.cacheOverload();
        }

        @Override
        public void onRemove(ObjectQueue<Measurement> queue) {
            // nothing to do here
        }

        @Override
        public void setInternalMonitoring(InternalMetrics internalMetrics) {
            this.monitoring = internalMetrics;
        }
    }
}
