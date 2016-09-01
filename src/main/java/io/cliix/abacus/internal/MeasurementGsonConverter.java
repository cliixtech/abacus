package io.cliix.abacus.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import com.google.gson.Gson;
import com.squareup.tape.FileObjectQueue.Converter;

import io.cliix.abacus.Measurement;

public class MeasurementGsonConverter implements Converter<Measurement> {
    private final Gson gson = new Gson();

    @Override
    public Measurement from(byte[] bytes) {
        Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        return this.gson.fromJson(reader, Measurement.class);
    }

    @Override
    public void toStream(Measurement object, OutputStream bytes) throws IOException {
        Writer writer = new OutputStreamWriter(bytes);
        this.gson.toJson(object, Measurement.class, writer);
        writer.close();
    }
}