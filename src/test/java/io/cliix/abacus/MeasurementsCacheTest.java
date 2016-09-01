package io.cliix.abacus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.cliix.abacus.internal.InternalMetrics;
import io.cliix.abacus.internal.MeasurementsCache;

public class MeasurementsCacheTest {

    private File file;
    private MeasurementsCache cache;
    private int maxEntries;
    private InternalMetrics internalMonitor;

    @Before
    public void setUp() throws IOException {
        this.file = new File(System.getProperty("java.io.tmpdir"), "abacus-test.txt");
        if (this.file.exists())
            this.file.delete();
        this.maxEntries = 100;
        this.internalMonitor = mock(InternalMetrics.class);
        this.cache = new MeasurementsCache(this.file, this.maxEntries);
        this.cache.setInternalMonitoring(this.internalMonitor);
    }

    @After
    public void teardown() throws IOException {
        if (this.file.exists())
            this.file.delete();
    }

    @Test
    public void cacheAdd_trimsFile_toMaxCacheSize() {
        Measurement m = new Measurement().setName("name").setValue(1d);
        for (int i = 0; i < 150; i++) {
            this.cache.add(m);
        }
        assertThat(this.cache.size()).isEqualTo(this.maxEntries);
    }

    @Test
    public void cacheAdd_trimsFile_registerMetric() {
        Measurement m = new Measurement().setName("name").setValue(1d);
        for (int i = 0; i < 101; i++) {
            this.cache.add(m);
        }
        verify(this.internalMonitor).cacheOverload();
    }
}
