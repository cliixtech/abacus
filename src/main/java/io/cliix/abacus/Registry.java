package io.cliix.abacus;

public interface Registry {

    void addMeasurement(String name, double value);

    void addMeasurement(String name, String source, double value);

}