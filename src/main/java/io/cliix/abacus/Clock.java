package io.cliix.abacus;

public class Clock {
    public static long now() {
        return System.currentTimeMillis();
    }

    public static long millisSince(long startTime) {
        return now() - startTime;
    }
}
