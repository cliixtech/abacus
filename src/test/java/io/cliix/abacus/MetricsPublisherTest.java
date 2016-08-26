package io.cliix.abacus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.librato.metrics.HttpPoster;
import com.librato.metrics.HttpPoster.Response;
import com.librato.metrics.Measurement;
import com.librato.metrics.SingleValueGaugeMeasurement;

import io.cliix.abacus.MetricsPublisher.PublishTask;

public class MetricsPublisherTest {

    @Captor
    ArgumentCaptor<String> captor;

    private PublishTask publisher;
    private MetricsCache cache;
    private HttpPoster poster;

    @Before
    public void setUp() {
        this.poster = mock(HttpPoster.class);
        this.cache = mock(MetricsCache.class);
        this.publisher = new MetricsPublisher.PublishTask(this.poster, "test", cache);
        initMocks(this);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void run_postsBatchWithMetrics()
            throws IOException, InterruptedException, ExecutionException, TimeoutException {
        String expectedPayload =
                "{\"counters\":[],\"gauges\":[{\"period\":10,\"name\":\"metric\",\"value\":1}],\"source\":\"test\"}";
        when(this.cache.size()).thenReturn(2).thenReturn(2).thenReturn(1).thenReturn(0);
        Measurement m = SingleValueGaugeMeasurement.builder("metric", 1l).setPeriod(10l).build();
        when(this.cache.peek()).thenReturn(m);
        Response response = mock(Response.class);
        Future<Response> future = mock(Future.class);
        when(future.get(any(Long.class), any(TimeUnit.class))).thenReturn(response);
        when(this.poster.post(any(String.class), any(String.class))).thenReturn(future);
        when(response.getStatusCode()).thenReturn(200);

        this.publisher.publish();

        verify(this.poster, times(2)).post(any(String.class), captor.capture());
        assertThat(expectedPayload).isEqualTo(captor.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void run_removeMetricOnPostSuccess()
            throws IOException, InterruptedException, ExecutionException, TimeoutException {
        when(this.cache.size()).thenReturn(1).thenReturn(1).thenReturn(0);
        Measurement m = SingleValueGaugeMeasurement.builder("metric", 1l).setPeriod(10l).build();
        when(this.cache.peek()).thenReturn(m);
        Response response = mock(Response.class);
        Future<Response> future = mock(Future.class);
        when(future.get(any(Long.class), any(TimeUnit.class))).thenReturn(response);
        when(this.poster.post(any(String.class), any(String.class))).thenReturn(future);
        when(response.getStatusCode()).thenReturn(200);

        this.publisher.publish();

        verify(this.cache).remove();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void run_dontRemoveMetricOnPostFailed()
            throws IOException, InterruptedException, ExecutionException, TimeoutException {
        when(this.cache.size()).thenReturn(1).thenReturn(1);
        Measurement m = SingleValueGaugeMeasurement.builder("metric", 1l).setPeriod(10l).build();
        when(this.cache.peek()).thenReturn(m);
        Response response = mock(Response.class);
        Future<Response> future = mock(Future.class);
        when(future.get(any(Long.class), any(TimeUnit.class))).thenReturn(response);
        when(this.poster.post(any(String.class), any(String.class))).thenReturn(future);
        when(response.getStatusCode()).thenReturn(400);

        this.publisher.publish();

        verify(this.cache, never()).remove();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void run_adjustOldMetric() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        when(this.cache.size()).thenReturn(1).thenReturn(0);
        long now = Clock.now();
        long measureTime = now - (180 * 60);
        Measurement m =
                SingleValueGaugeMeasurement.builder("metric", 1l).setMeasureTime(measureTime).setPeriod(10l).build();
        long adjustedTime = measureTime + (70 * 60); // 70 (delta) = 180 minutes (delay) - 110 minutes (adjustment)
        String expectedPayload =
                "{\"counters\":[],\"gauges\":[{\"period\":10,\"measure_time\":" + adjustedTime
                        + ",\"name\":\"metric\",\"value\":1}],\"source\":\"test\"}";
        when(this.cache.size()).thenReturn(1).thenReturn(1).thenReturn(0);
        when(this.cache.peek()).thenReturn(m);
        Response response = mock(Response.class);
        Future<Response> future = mock(Future.class);
        when(future.get(any(Long.class), any(TimeUnit.class))).thenReturn(response);
        when(this.poster.post(any(String.class), any(String.class))).thenReturn(future);
        when(response.getStatusCode()).thenReturn(200);

        this.publisher.publish();

        verify(this.poster, times(1)).post(any(String.class), captor.capture());
        assertThat(expectedPayload).isEqualTo(captor.getValue());
    }
}
