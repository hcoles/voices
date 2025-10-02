package org.pitest.voices.openvoice;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import org.pitest.voices.g2p.core.Dictionary;
import org.pitest.voices.g2p.core.G2PModel;
import org.pitest.voices.Resource;
import org.pitest.voices.G2PModelSupplier;

import java.nio.file.Path;
import java.util.function.Supplier;

public class OpenVoiceSupplier implements G2PModelSupplier {

    @Override
    public G2PModel create(Supplier<OrtSession.SessionOptions> options, Dictionary dictionary, OrtEnvironment env, Path base) {
        try {
            var model = Resource.readAsBytes("/models/fdemelo_g2p-mbyt5-12l-ipa-childes-espeak.onnx");
            var opts = options.get();
            var session = env.createSession(model, opts);
            return new OpenVoiceModel(dictionary, session, env);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
