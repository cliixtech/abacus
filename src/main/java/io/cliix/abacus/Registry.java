package io.cliix.abacus;

import java.util.Map;

public interface Registry {

    void addMeasurement(String name, double value);

    void addMeasurement(String name, double value, Map<String, String> tags);

}