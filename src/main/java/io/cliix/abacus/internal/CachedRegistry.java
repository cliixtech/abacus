package io.cliix.abacus.internal;

import io.cliix.abacus.Clock;
import io.cliix.abacus.Measurement;
import io.cliix.abacus.Registry;

public class CachedRegistry implements Registry {

    private MeasurementsCache cache;
    private String source;

    public CachedRegistry(MeasurementsCache cache, String source) {
        this.cache = cache;
        this.source = source;
    }

    @Override
    public void addMeasurement(String name, double value) {
        this.addMeasurement(name, this.source, value);
    }

    @Override
    public void addMeasurement(String name, String source, double value) {
        Measurement entry = new Measurement().setName(name).setSource(source).setTime(Clock.now()).setValue(value);
        this.cache.add(entry);
    }
}
