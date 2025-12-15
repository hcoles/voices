package org.pitest.voices;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.pitest.voices.g2p.core.PiperPhonemizer;
import org.pitest.voices.g2p.core.tracing.Trace;

import java.io.IOException;
import java.nio.file.Path;

public interface VoiceHandler {

    Voice createVoice(Model model,
                      PiperPhonemizer phonemizer,
                      Trace trace,
                      VoiceSession session,
                      float gain);

    VoiceSession createSession(Model model, OrtEnvironment env, OrtSession.SessionOptions options, Path base) throws IOException, OrtException;
}
