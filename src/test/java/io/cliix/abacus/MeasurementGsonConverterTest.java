package io.cliix.abacus;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.librato.metrics.CounterMeasurement;
import com.librato.metrics.SingleValueGaugeMeasurement;

public class MeasurementGsonConverterTest {

    private MeasurementGsonConverter converter;

    @Before
    public void setUp() {
        this.converter = new MeasurementGsonConverter();
    }

    @Test
    public void convertGaugeMetric_bothWays() throws IOException {
        String name = "some";
        Long value = 1l;
        Number period = 10l;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        SingleValueGaugeMeasurement gauge = SingleValueGaugeMeasurement.builder(name, value).setPeriod(period).build();
        converter.toStream(gauge, stream);
        SingleValueGaugeMeasurement loaded = (SingleValueGaugeMeasurement) converter.from(stream.toByteArray());

        assertThat(name).isEqualTo(loaded.getName());
        assertThat(value).isEqualTo(loaded.toMap().get("value").longValue());
        assertThat(period).isEqualTo(loaded.getPeriod().longValue());
    }

    @Test
    public void convertCounterMetric_bothWays() throws IOException {
        String name = "some";
        Long value = 1l;
        Number period = 10l;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        CounterMeasurement gauge = CounterMeasurement.builder(name, value).setPeriod(period).build();
        converter.toStream(gauge, stream);
        CounterMeasurement loaded = (CounterMeasurement) converter.from(stream.toByteArray());

        assertThat(name).isEqualTo(loaded.getName());
        assertThat(value).isEqualTo(loaded.toMap().get("value").longValue());
        assertThat(period).isEqualTo(loaded.getPeriod().longValue());
    }
}
