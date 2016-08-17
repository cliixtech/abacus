package io.cliix.abacus;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.librato.metrics.Measurement;
import com.squareup.tape.FileObjectQueue.Converter;

public class MeasurementGsonConverter implements Converter<Measurement> {
    private final Gson gson;

    public MeasurementGsonConverter() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Measurement.class, new MeasurementAdapter());
        this.gson = builder.create();
    }

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

    private class MeasurementAdapter implements JsonSerializer<Measurement>, JsonDeserializer<Measurement> {

        private static final String TYPE = "TYPE";
        private static final String DATA = "DATA";

        @Override
        public JsonElement serialize(Measurement src, Type typeOfSrc, JsonSerializationContext context) {

            JsonObject retValue = new JsonObject();
            String className = src.getClass().getName();
            retValue.addProperty(TYPE, className);
            JsonElement elem = context.serialize(src);
            retValue.add(DATA, elem);
            return retValue;
        }

        @Override
        public Measurement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonPrimitive prim = (JsonPrimitive) jsonObject.get(TYPE);
            String className = prim.getAsString();

            Class<?> klass = null;
            try {
                klass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new JsonParseException(e.getMessage());
            }
            return context.deserialize(jsonObject.get(DATA), klass);
        }
    }
}