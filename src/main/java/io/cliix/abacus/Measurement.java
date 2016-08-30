package io.cliix.abacus;

public class Measurement {
    private String name;
    private String source;
    private long time;
    private double value;

    public String getName() {
        return name;
    }

    public Measurement setName(String name) {
        this.name = name;
        return this;
    }

    public String getSource() {
        return source;
    }

    public Measurement setSource(String source) {
        this.source = source;
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
