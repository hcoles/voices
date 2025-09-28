package org.pitest.voices.openvoice;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.pitest.g2p.core.Dictionary;
import org.pitest.g2p.core.G2PModel;
import org.pitest.g2p.core.pos.Pos;
import org.pitest.g2p.core.tracing.Trace;
import org.pitest.g2p.core.Language;

public class OpenVoiceModel implements G2PModel {

    private final Dictionary dictionary;
    private final ByT5ONNXInference inference;

    public OpenVoiceModel(Dictionary dictionary, OrtSession session, OrtEnvironment env) {
        this.dictionary = dictionary;
        this.inference = new ByT5ONNXInference(env, session);
    }

    @Override
    public String predict(Trace trace, Language lang, String word, Pos pos) {
        try {
            trace.start(word, pos);
            var lookup = dictionary.get(word, pos);
            if (lookup.isPresent()) {
                String phoneme = lookup.get();
                trace.dictionaryHit(word, pos, phoneme);
                return phoneme;
            }
            return inference.infer(word, lang.tag());
        } catch (OrtException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        inference.close();
    }
}
