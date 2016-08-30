package io.cliix.abacus;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import io.cliix.abacus.internal.MeasurementGsonConverter;

public class MeasurementGsonConverterTest {

    private MeasurementGsonConverter converter;

    @Before
    public void setUp() {
        this.converter = new MeasurementGsonConverter();
    }

    @Test
    public void convertMetric_bothWays() throws IOException {
        String name = "some";
        String source = "source";
        Double value = 1.5d;
        long time = Clock.now();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        Measurement metric = new Measurement();
        metric.setName(name);
        metric.setSource(source);
        metric.setTime(time);
        metric.setValue(value);
        converter.toStream(metric, stream);
        Measurement loaded = converter.from(stream.toByteArray());

        assertThat(name).isEqualTo(loaded.getName());
        assertThat(source).isEqualTo(loaded.getSource());
        assertThat(time).isEqualTo(loaded.getTime());
        assertThat(value).isEqualTo(loaded.getValue());

    }

}
