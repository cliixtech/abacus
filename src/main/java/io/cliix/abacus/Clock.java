package io.cliix.abacus;

import java.util.concurrent.TimeUnit;

public class Clock {
    public static long now() {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }

    public static long minutesFromNow(Long secondsEpoch) {
        return TimeUnit.SECONDS.toMinutes(now() - secondsEpoch);
    }
}
