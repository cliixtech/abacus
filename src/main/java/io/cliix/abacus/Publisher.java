package io.cliix.abacus;

import io.cliix.abacus.internal.InternalMonitoring;
import io.cliix.abacus.internal.MeasurementsCache;

public interface Publisher extends InternalMonitoring {
    void publish(MeasurementsCache cache);
}
