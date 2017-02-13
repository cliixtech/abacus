package io.cliix.abacus.internal;

import io.cliix.abacus.Registry;

public class InternalMetrics {

    private static final String CACHE_SIZE_METRIC = "abacus.cache.size";
    private static final String PUBLISH_TIME_MS = "abacus.publish.durationMs";
    private static final String PUBLISH_SUCCESS = "abacus.publish.fail";
    private static final String PUBLISH_FAILURE = "abacus.publish.success";
    private Registry registry;

    public InternalMetrics(Registry registry) {
        this.registry = registry;
    }

    public void cacheSize(long cacheSize) {
        this.registry.addMeasurement(CACHE_SIZE_METRIC, cacheSize);
    }

    public void publishTime(long timeMs) {
        this.registry.addMeasurement(PUBLISH_TIME_MS, timeMs);
    }

    public void publishFailure() {
        this.registry.addMeasurement(PUBLISH_FAILURE, 1);
    }

    public void publishSuccess() {
        this.registry.addMeasurement(PUBLISH_SUCCESS, 1);
    }
}
