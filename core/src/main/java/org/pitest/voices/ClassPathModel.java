package org.pitest.voices;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.pitest.voices.g2p.core.PiperPhonemizer;
import org.pitest.voices.g2p.core.tracing.Trace;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ClassPathModel implements Model {

    private final VoiceHandler handler;
    private final String resource;
    private final Language lang;
    private final int sid;
    private final float gain;


    public ClassPathModel(VoiceHandler handler, String resource, Language lang, float gain) {
        this(handler, resource, lang, -1, gain);
    }

    public ClassPathModel(VoiceHandler handler, String resource, Language lang, int sid, float gain) {
        this.handler = handler;
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
        return new ClassPathModel(handler, resource, lang, sid, gain);
    }

    @Override
    public float defaultGain() {
        return gain;
    }

    @Override
    public Voice createVoice(PiperPhonemizer phonemizer, Trace trace, VoiceSession session, float gain) {
        return handler.createVoice(this, phonemizer, trace, session, gain);
    }

    @Override
    public VoiceSession createSession(OrtEnvironment env, OrtSession.SessionOptions options, Path base) throws IOException, OrtException {
        return handler.createSession(this, env, options, base);
    }

}
