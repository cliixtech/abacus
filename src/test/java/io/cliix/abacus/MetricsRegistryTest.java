package io.cliix.abacus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.librato.metrics.Measurement;

public class MetricsRegistryTest {

    @Captor
    ArgumentCaptor<Measurement> captor;

    @Before
    public void init() {
    }

    private MetricsCache cacheMock;
    private MetricsRegistry registry;

    @Before
    public void setUp() {
        this.cacheMock = mock(MetricsCache.class);
        this.registry = new MetricsRegistry(this.cacheMock);
        initMocks(this);
    }

    @Test
    public void addCounter_callCache() {
        String name = "some";
        Long value = 1l;

        this.registry.addCounterMeasurement(name, value);

        verify(this.cacheMock).add(captor.capture());
        assertThat(name).isEqualTo(captor.getValue().getName());
        assertThat(value).isEqualTo(captor.getValue().toMap().get("value"));
    }

    @Test
    public void addCounterWithPeriod_callCache() {
        String name = "some";
        Long value = 1l;
        Long period = 10l;

        this.registry.addCounterMeasurement(period, name, value);

        verify(this.cacheMock).add(captor.capture());
        assertThat(period).isEqualTo(captor.getValue().getPeriod());
        assertThat(name).isEqualTo(captor.getValue().getName());
        assertThat(value).isEqualTo(captor.getValue().toMap().get("value"));
    }

    @Test
    public void addGauge_callCache() {
        String name = "some";
        Long value = 1l;

        this.registry.addGaugeMeasurement(name, value);

        verify(this.cacheMock).add(captor.capture());
        assertThat(name).isEqualTo(captor.getValue().getName());
        assertThat(value).isEqualTo(captor.getValue().toMap().get("value"));
    }

    @Test
    public void addGaugerWithPeriod_callCache() {
        String name = "some";
        Long value = 1l;
        Long period = 10l;

        this.registry.addGaugeMeasurement(period, name, value);

        verify(this.cacheMock).add(captor.capture());
        assertThat(period).isEqualTo(captor.getValue().getPeriod());
        assertThat(name).isEqualTo(captor.getValue().getName());
        assertThat(value).isEqualTo(captor.getValue().toMap().get("value"));
    }
}
