package org.pitest.voices.piper;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.pitest.voices.Model;
import org.pitest.voices.ModelParameters;
import org.pitest.voices.Pause;
import org.pitest.voices.Voice;
import org.pitest.voices.VoiceHandler;
import org.pitest.voices.VoiceSession;
import org.pitest.voices.g2p.core.PiperPhonemizer;
import org.pitest.voices.g2p.core.tracing.Trace;

import java.io.IOException;
import java.nio.file.Path;

public class PiperHandler implements VoiceHandler {

    public static PiperHandler piper() {
        return new PiperHandler();
    }

    @Override
    public Voice createVoice(Model model, PiperPhonemizer phonemizer, Trace trace, VoiceSession session, float gain) {
        return new PiperVoice(model, phonemizer, trace, session, Pause.defaultPauses(), ModelParameters.defaultParams(), gain);
    }

    @Override
    public VoiceSession createSession(Model model, OrtEnvironment env, OrtSession.SessionOptions options, Path base) throws IOException, OrtException {
        var session = env.createSession(model.asBytes(base), options);
        return new PiperVoiceSession(env, model.resolveConfig(base), session);
    }


}
