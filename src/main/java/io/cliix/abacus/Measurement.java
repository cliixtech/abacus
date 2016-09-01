package io.cliix.abacus;

import java.util.Map;

public class Measurement {
    private String name;
    private long time;
    private double value;
    private Map<String, String> tags;

    public String getName() {
        return name;
    }

    public Measurement setName(String name) {
        this.name = name;
        return this;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public Measurement setTags(Map<String, String> tags) {
        this.tags = tags;
        return this;
    }

    public long getTime() {
        return time;
    }

    public Measurement setTime(long time) {
        this.time = time;
        return this;
    }

    public double getValue() {
        return value;
    }

    public Measurement setValue(double value) {
        this.value = value;
        return this;
    }
}
