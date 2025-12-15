package org.pitest.voices.kokoro;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.pitest.voices.Model;
import org.pitest.voices.ModelParameters;
import org.pitest.voices.Pause;
import org.pitest.voices.Resource;
import org.pitest.voices.Stresses;
import org.pitest.voices.Voice;
import org.pitest.voices.VoiceHandler;
import org.pitest.voices.VoiceSession;
import org.pitest.voices.g2p.core.PiperPhonemizer;
import org.pitest.voices.g2p.core.tracing.Trace;

import java.io.IOException;
import java.nio.file.Path;

public class KokoroHandler implements VoiceHandler {

    public static KokoroHandler kokoro() {
        return new KokoroHandler();
    }

    @Override
    public Voice createVoice(Model model, PiperPhonemizer phonemizer, Trace trace, VoiceSession session, float gain) {
        var params = ModelParameters.defaultParams()
                .withStress(Stresses.NO_STRESS);
        return new KokoroVoice(model, phonemizer, trace, session, Pause.defaultPauses(), params, gain);
    }

    @Override
    public VoiceSession createSession(Model model, OrtEnvironment env, OrtSession.SessionOptions options, Path base) throws IOException, OrtException {
        var kokoroModel = Resource.readAsBytes("/models/kokoro_model.onnx");
        OrtSession session = env.createSession(kokoroModel, options);
        byte[] voiceBytes = model.asBytes(base);

        return new KokoroVoiceSession(voiceBytes, model.resolveConfig(base), session);
    }
}
