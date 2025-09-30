package org.pitest.voices;

import org.pitest.g2p.core.Language;
import org.pitest.g2p.util.Resource;

import java.io.IOException;
import java.nio.file.Path;

public class ClassPathModel implements Model {

    private final String resource;
    private final Language lang;
    private final float gain;

    public ClassPathModel(String resource, Language lang, float gain) {
        this.resource = resource;
        this.lang = lang;
        this.gain = gain;
    }

    @Override
    public byte[] byteBuffer(Path cacheBase) {
        return Resource.readAsBytes(resource);
    }

    @Override
    public ModelConfig resolveConfig(Path cacheBase) throws IOException {
        try (var is = Resource.readAsStream(resource)) {
            return ModelConfig.fromJson(is);
        }
    }

    @Override
    public String id() {
        return resource;
    }

    @Override
    public Language language() {
        return lang;
    }

    @Override
    public Model withLanguage(Language lang) {
        return new ClassPathModel(resource, lang, gain);
    }

    @Override
    public float defaultGain() {
        return gain;
    }

}
