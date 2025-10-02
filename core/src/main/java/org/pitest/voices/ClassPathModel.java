package org.pitest.voices;

import java.io.IOException;
import java.nio.file.Path;

public class ClassPathModel implements Model {

    private final String resource;
    private final Language lang;
    private final int sid;
    private final float gain;


    public ClassPathModel(String resource, Language lang, float gain) {
        this(resource, lang, -1, gain);
    }

    public ClassPathModel(String resource, Language lang, int sid, float gain) {
        this.resource = resource;
        this.lang = lang;
        this.sid = sid;
        this.gain = gain;
    }

    @Override
    public byte[] asBytes(Path cacheBase) {
        return Resource.readAsBytes(resource);
    }

    @Override
    public ModelConfig resolveConfig(Path cacheBase) throws IOException {
        try (var is = Resource.readAsStream(resource + ".json")) {
            return ModelConfig.fromJson(is);
        }
    }

    @Override
    public String id() {
        return resource;
    }

    @Override
    public int sid() {
        return sid;
    }

    @Override
    public Language language() {
        return lang;
    }

    @Override
    public Model withLanguage(Language lang) {
        return new ClassPathModel(resource, lang, sid, gain);
    }

    @Override
    public float defaultGain() {
        return gain;
    }

}
