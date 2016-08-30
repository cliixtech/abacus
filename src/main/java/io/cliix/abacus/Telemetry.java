package io.cliix.abacus;

import java.util.concurrent.TimeUnit;

public interface Telemetry {

    void publish();

    void start(long delay, TimeUnit unit);

    void stop();

}