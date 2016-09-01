package io.cliix.abacus.internal;

import java.util.Map;

import io.cliix.abacus.Clock;
import io.cliix.abacus.Measurement;
import io.cliix.abacus.Registry;

public class CachedRegistry implements Registry {

    private MeasurementsCache cache;
    private Map<String, String> tags;

    public CachedRegistry(MeasurementsCache cache, Map<String, String> tags) {
        this.cache = cache;
        this.tags = tags;
    }

    @Override
    public void addMeasurement(String name, double value) {
        this.addMeasurement(name, value, this.tags);
    }

    @Override
    public void addMeasurement(String name, double value, Map<String, String> tags) {
        Measurement entry = new Measurement().setName(name).setTags(tags).setTime(Clock.now()).setValue(value);
        this.cache.add(entry);
    }
}
