package org.pitest.voices;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.pitest.g2p.core.Dictionary;
import org.pitest.g2p.core.PiperPhonemizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Heavyweight class that holds model resources. For best performance
 * instantiate once per application.
 */
public class Chorus implements AutoCloseable {

    private final ChorusConfig conf;

    private final Map<String, VoiceSession> voices = new ConcurrentHashMap<>();

    public Chorus(Dictionary dictionary) {
        this(new ChorusConfig(dictionary));
    }

    public Chorus(ChorusConfig conf) {
        this.conf = conf;
    }

    public Voice voice(Model model) {
        var session = voices.computeIfAbsent(model.id(), n -> loadVoice(model));
        var phonemizer = new PiperPhonemizer(conf.model(), conf.expansions(), conf.trace());

        return new PiperVoice(model,
                phonemizer,
                conf.trace(),
                session,
                model.defaultPauses(),
                model.defaultParams(),
                model.defaultGain());
    }

    private VoiceSession loadVoice(Model model) {
        try {
            Files.createDirectories(conf.base());
            return loadPiperModel(model, model.resolve(conf.base()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private VoiceSession loadPiperModel(Model model, Path onnx) {
        OrtEnvironment env = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        conf.cudaOptions().accept(options);
        try {
            OrtSession session = env.createSession(onnx.toString(), options);
            return new VoiceSession(env, model.resolveConfig(conf.base()), session);
        } catch (IOException | OrtException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        voices.values().forEach(v -> {
            try {
                v.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

}

