package io.cliix.abacus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import io.cliix.abacus.internal.MeasurementsCache;
import io.cliix.abacus.internal.CachedRegistry;

public class CachedRegistryTest {

    @Captor
    ArgumentCaptor<Measurement> captor;

    @Before
    public void init() {
    }

    private MeasurementsCache cacheMock;
    private Registry registry;
    private String source;

    @Before
    public void setUp() {
        this.cacheMock = mock(MeasurementsCache.class);
        this.source = "test";
        this.registry = new CachedRegistry(this.cacheMock, this.source);
        initMocks(this);
    }

    @Test
    public void addCounter_callCache() {
        String name = "some";
        Double value = 1.5d;

        this.registry.addMeasurement(name, this.source, value);

        verify(this.cacheMock).add(captor.capture());
        assertThat(name).isEqualTo(captor.getValue().getName());
        assertThat(this.source).isEqualTo(captor.getValue().getSource());
        assertThat(value).isEqualTo(captor.getValue().getValue());
    }

    @Test
    public void addCounter_withSource_callCache() {
        String name = "some";
        Double value = 1.5d;
        String otherSource = "other";

        this.registry.addMeasurement(name, otherSource, value);

        verify(this.cacheMock).add(captor.capture());
        assertThat(name).isEqualTo(captor.getValue().getName());
        assertThat(otherSource).isEqualTo(captor.getValue().getSource());
        assertThat(value).isEqualTo(captor.getValue().getValue());
    }
}
