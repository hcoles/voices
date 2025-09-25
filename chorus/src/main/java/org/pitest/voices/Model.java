package org.pitest.voices;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Model {

    private final String name;
    private final String location;
    private final ModelFetcher resolver;

    private final List<Pause> pauses;
    private final float gain;

    private final ModelParameters params;

    public Model(String name,
                 String location,
                 ModelFetcher resolver,
                 List<Pause> pauses,
                 float gain,
                 ModelParameters params) {
        this.name = name;
        this.location = location;
        this.resolver = resolver;
        this.pauses = pauses;
        this.gain = gain;
        this.params = params;
    }

    public String name() {
        return name;
    }

    public String location() {
        return location;
    }

    public Path fetch() throws IOException {
        return resolver.fetch().resolve(location);
    }

    public List<Pause> defaultPauses() {
        return pauses;
    }

    String onnx() {
        return name + ".onnx";
    }

    String json() {
        return onnx() + ".json";
    }

    String id() {
        return name;
    }

    public float defaultGain() {
        return gain;
    }

    public ModelParameters defaultParams() {
        return params;
    }

}
