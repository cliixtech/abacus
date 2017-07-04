package io.cliix.abacus;

import java.util.Collections;
import java.util.Map;

public class Measurement {
    private String name;
    private Long time;
    private Double value;
    private Map<String, String> tags = Collections.emptyMap();

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

    public Measurement setTime(Long time) {
        this.time = time;
        return this;
    }

    public double getValue() {
        return value;
    }

    public Measurement setValue(Double value) {
        this.value = value;
        return this;
    }

    public boolean isValid() {
        return this.value != null && this.time != null && this.name != null;
    }
}
