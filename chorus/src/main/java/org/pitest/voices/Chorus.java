package org.pitest.voices;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.pitest.g2p.core.Dictionary;
import org.pitest.g2p.core.PiperPhonemizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
        var phonemizer = new PiperPhonemizer(conf.model(), conf.expansions(),  conf.trace());

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
            Path location = conf.base().resolve(model.location());
            if (!Files.exists(location.resolve(model.onnx()))) {
                Path tempLocation = model.fetch();
                Files.move(tempLocation, location);
            }

            return loadPiperModel(model, location);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private VoiceSession loadPiperModel(Model model, Path location) {
        String onnx = location.resolve(model.onnx()).toString();

        OrtEnvironment env = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();

        conf.cudaOptions().accept(options);

        Path json = location.resolve(model.json());
        try(var in = Files.newInputStream(json, StandardOpenOption.READ)) {
            ModelConfig config = ModelConfig.fromJson(in);
            OrtSession session = env.createSession(onnx, options);
            return new VoiceSession(env, config, session);
        } catch (IOException | OrtException e ) {
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

