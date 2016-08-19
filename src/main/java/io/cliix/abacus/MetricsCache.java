package io.cliix.abacus;

import java.io.File;
import java.io.IOException;

import com.librato.metrics.Measurement;
import com.squareup.tape.FileObjectQueue;
import com.squareup.tape.ObjectQueue;
import com.squareup.tape.ObjectQueue.Listener;

public class MetricsCache implements Listener<Measurement> {

    private File cacheFile;
    private long maxSizeMb;
    private FileObjectQueue<Measurement> diskQ;

    public MetricsCache(File cacheFile, long maxSizeMb) throws IOException {
        this.cacheFile = cacheFile;
        this.maxSizeMb = maxSizeMb;
        this.diskQ = new FileObjectQueue<>(cacheFile, new MeasurementGsonConverter());
        this.diskQ.setListener(this);
    }

    public void add(Measurement entry) {
        this.diskQ.add(entry);
    }

    public Measurement peek() {
        return this.diskQ.peek();
    }

    public void remove() {
        this.diskQ.remove();
    }

    public int size() {
        return this.diskQ.size();
    }

    @Override
    public void onAdd(ObjectQueue<Measurement> queue, Measurement entry) {
        if (this.reachedCapacity()) {
            this.trimFile();
        }
    }

    private boolean reachedCapacity() {
        return (this.cacheFile.length() / 1024 / 1024 >= this.maxSizeMb);
    }

    private void trimFile() {
        this.diskQ.remove();
    }

    @Override
    public void onRemove(ObjectQueue<Measurement> queue) {
        // nothing to do here
    }

}
