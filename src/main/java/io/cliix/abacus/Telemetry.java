package io.cliix.abacus;

import java.util.concurrent.TimeUnit;

public interface Telemetry {

    public void publish();

    public void start(long delay, TimeUnit unit);

    public void stop();

}
