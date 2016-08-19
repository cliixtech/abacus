package io.cliix.abacus;

import com.librato.metrics.CounterMeasurement;
import com.librato.metrics.Measurement;
import com.librato.metrics.SingleValueGaugeMeasurement;

public class MetricsRegistry {

    private MetricsCache cache;

    public MetricsRegistry(MetricsCache cache) {
        this.cache = cache;
    }

    public void addCounterMeasurement(String name, Long value) {
        addMeasurement(CounterMeasurement.builder(name, value).build());
    }

    public void addCounterMeasurement(Number period, String name, Long value) {
        addMeasurement(CounterMeasurement.builder(name, value).setPeriod(period).build());
    }

    public void addGaugeMeasurement(String name, Number value) {
        this.addMeasurement(SingleValueGaugeMeasurement.builder(name, value).build());
    }

    public void addGaugeMeasurement(Number period, String name, Number value) {
        this.addMeasurement(SingleValueGaugeMeasurement.builder(name, value).setPeriod(period).build());
    }

    private void addMeasurement(Measurement measurement) {
        this.cache.add(measurement);
    }
}
