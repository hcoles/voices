package org.pitest.voices;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.pitest.voices.g2p.core.Dictionary;
import org.pitest.voices.g2p.core.G2PModel;
import org.pitest.voices.g2p.core.PiperPhonemizer;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Heavyweight class that holds model resources. For best performance
 * instantiate once per application, but note the class is not thread
 * safe.
 */
public class Chorus implements AutoCloseable {

    private final ChorusConfig conf;

    private final Map<String, VoiceSession> voices = new ConcurrentHashMap<>();

    // lazily initialised
    private G2PModel g2p;

    // lazily initialised, held only to allow closing
    private OrtSession.SessionOptions opts;

    public Chorus(Dictionary dictionary) {
        this(new ChorusConfig(dictionary));
    }

    public Chorus(ChorusConfig conf) {
        this.conf = conf;
    }

    public Voice voice(Model model) {
        var session = voices.computeIfAbsent(model.id(), n -> loadVoice(model));
        var phonemizer = new PiperPhonemizer(g2p(), conf.expansions(), conf.trace());

        return model.createVoice(phonemizer, conf.trace(),
                session,
                model.defaultGain());
    }

    private G2PModel g2p() {
        if (g2p != null) {
            return g2p;
        }
        g2p = conf.model().create(this::configureSession, conf.dictionary(), OrtEnvironment.getEnvironment(), conf.base());
        return g2p;
    }

    private VoiceSession loadVoice(Model model) {
        try {
            Files.createDirectories(conf.base());
            return loadPiperModel(model);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private VoiceSession loadPiperModel(Model model) {
        OrtEnvironment env = OrtEnvironment.getEnvironment();
        try {
            var options = configureSession();
            return model.createSession(env, options, conf.base());
        } catch (IOException | OrtException e) {
            throw new RuntimeException(e);
        }
    }

    private OrtSession.SessionOptions configureSession() {
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        conf.cudaOptions().accept(options);
        return options;
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

        if (g2p != null) {
            try {
                g2p.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (opts != null) {
            try {
                opts.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

}

