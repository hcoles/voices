package org.pitest.voices.kokoro;

import org.pitest.voices.ClassPathModel;
import org.pitest.voices.Language;
import org.pitest.voices.ModelConfig;
import org.pitest.voices.Resource;
import org.pitest.voices.VoiceHandler;

import java.io.IOException;
import java.nio.file.Path;

class KokoroClasspathModel extends ClassPathModel {
    KokoroClasspathModel(VoiceHandler handler, String resource, Language lang, float gain) {
        super(handler, resource, lang, gain);
    }

    @Override
    public ModelConfig resolveConfig(Path cacheBase) throws IOException {
        try (var is = Resource.readAsStream("/models/kokoro/kokoro_config.json")) {
            return ModelConfig.fromJson(is);
        }
    }

}
