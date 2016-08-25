package io.cliix.abacus;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.librato.metrics.Measurement;
import com.squareup.tape.FileObjectQueue;
import com.squareup.tape.ObjectQueue;
import com.squareup.tape.ObjectQueue.Listener;

public class MetricsCache {

    private static final Logger LOG = LoggerFactory.getLogger(MetricsCache.class);
    private final FileObjectQueue<Measurement> diskQ;
    private final Lock monitor = new ReentrantLock();

    public MetricsCache(File cacheFile, long cacheMaxEntries) throws IOException {
        this.diskQ = new FileObjectQueue<>(cacheFile, new MeasurementGsonConverter());
        this.diskQ.setListener(new CacheSizeListener(this, cacheMaxEntries));
    }

    public void add(Measurement entry) {
        LOG.debug("Adding metric {}", entry);
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

    private class CacheSizeListener implements Listener<Measurement> {

        private MetricsCache cache;
        private long maxEntries;

        public CacheSizeListener(MetricsCache metricsCache, long cacheMaxEntries) {
            this.cache = metricsCache;
            this.maxEntries = cacheMaxEntries;
        }

        @Override
        public void onAdd(ObjectQueue<Measurement> queue, Measurement entry) {
            if (this.isCacheBiggerThanItShould()) {
                LOG.debug("Ops, cache is too big, removing oldest metric");
                this.trimCache();
            }
        }

        private boolean isCacheBiggerThanItShould() {
            return this.cache.size() > this.maxEntries;
        }

        private void trimCache() {
            this.cache.remove();
        }

        @Override
        public void onRemove(ObjectQueue<Measurement> queue) {
            // nothing to do here
        }
    }
}
