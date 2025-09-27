package org.pitest.voices.openvoice;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import org.pitest.g2p.core.Dictionary;
import org.pitest.g2p.core.G2PModel;
import org.pitest.voices.G2PModelSupplier;
import org.pitest.voices.URLModelFetcher;

import java.nio.file.Path;
import java.util.function.Supplier;

public class OpenVoiceSupplier implements G2PModelSupplier {
    private final ONNXModel model;

    public OpenVoiceSupplier() {
        this(new ONNXModel(URLModelFetcher.fromString("https://huggingface.co/OpenVoiceOS/g2p-mbyt5-12l-ipa-childes-espeak-onnx/resolve/main/fdemelo_g2p-mbyt5-12l-ipa-childes-espeak.onnx"),
                "fdemelo_g2p-mbyt5-12l-ipa-childes-espeak"));
    }

    public OpenVoiceSupplier(ONNXModel model) {
        this.model = model;
    }

    @Override
    public G2PModel create(Supplier<OrtSession.SessionOptions> options, Dictionary dictionary, OrtEnvironment env, Path base) {
        try {
            Path onnx = model.resolve(base);
            var session = env.createSession(onnx.toString(), options.get());
            return new OpenVoiceModel(dictionary, session, env);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
