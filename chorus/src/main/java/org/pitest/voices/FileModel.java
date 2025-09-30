package org.pitest.voices;

import org.pitest.g2p.core.Language;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;


public class FileModel implements Model {

    private final String name;
    private final String location;
    private final Language lang;

    private final ModelFetcher resolver;

    private final float gain;

    public FileModel(String name,
                 String location,
                 Language lang,
                 ModelFetcher resolver,
                 float gain) {
        this.name = name;
        this.location = location;
        this.lang = lang;
        this.resolver = resolver;
        this.gain = gain;
    }

    @Override
    public String id() {
        return name;
    }

    @Override
    public Language language() {
        return lang;
    }

    @Override
    public Model withLanguage(Language lang) {
        return new FileModel(name, location, lang, resolver, gain);
    }

    @Override
    public byte[] byteBuffer(Path cacheBase) throws IOException {
        return Files.readAllBytes(resolveFiles(cacheBase).resolve(onnx()));
    }

    @Override
    public ModelConfig resolveConfig(Path cacheBase) throws IOException {
        Path json = resolveFiles(cacheBase).resolve(onnx() + ".json");
        try(var in = Files.newInputStream(json, StandardOpenOption.READ)) {
            return ModelConfig.fromJson(in);
        }
    }

    @Override
    public float defaultGain() {
        return gain;
    }

    String onnx() {
        return name + ".onnx";
    }

    private Path resolveFiles(Path cacheBase) throws IOException {
        Path location = cacheBase.resolve(this.location);
        Path onnx = location.resolve(onnx());
        if (!Files.exists(onnx)) {
            Path tempLocation = fetch();
            Files.move(tempLocation, location);
        }
        return location;
    }

    private Path fetch() throws IOException {
        return resolver.fetch().resolve(location);
    }
}
