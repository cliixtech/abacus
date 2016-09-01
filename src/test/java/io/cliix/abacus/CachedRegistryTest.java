package io.cliix.abacus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.HashMap;
import java.util.Map;

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

    private final String DEFAULT_TAG_KEY = "source";
    private final String DEFAULT_TAG_VALUE = "unitTest";
    private MeasurementsCache cacheMock;
    private Registry registry;
    private Map<String, String> tags;

    @Before
    public void setUp() {
        this.cacheMock = mock(MeasurementsCache.class);
        this.tags = new HashMap<>();
        this.tags.put("source", "unitTest");
        this.registry = new CachedRegistry(this.cacheMock, this.tags);
        initMocks(this);
    }

    @Test
    public void addCounter_withTags_callCache() {
        String name = "some";
        Double value = 1.5d;
        String tagKey = "other";
        String tagValue = "anything";

        Map<String, String> myTags = new HashMap<>();
        myTags.put(tagKey, tagValue);

        this.registry.addMeasurement(name, value, myTags);

        verify(this.cacheMock).add(captor.capture());
        assertThat(name).isEqualTo(captor.getValue().getName());
        assertThat(tagValue).isEqualTo(captor.getValue().getTags().get(tagKey));
        assertThat(value).isEqualTo(captor.getValue().getValue());
    }

    @Test
    public void addCounter_withoutTags_callCache_defaultTags() {
        String name = "some";
        Double value = 1.5d;

        this.registry.addMeasurement(name, value);

        verify(this.cacheMock).add(captor.capture());
        assertThat(name).isEqualTo(captor.getValue().getName());
        assertThat(DEFAULT_TAG_VALUE).isEqualTo(captor.getValue().getTags().get(DEFAULT_TAG_KEY));
        assertThat(value).isEqualTo(captor.getValue().getValue());
    }
}
