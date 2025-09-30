package org.pitest.voices;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OnnxValue;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.pitest.voices.audio.Audio;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Wraps an OrtSession. Each session contains a loaded model, so
 * is expensive.
 */
class VoiceSession implements AutoCloseable {

    private final OrtEnvironment env;
    private final OrtSession session;
    private final ModelConfig config;

    VoiceSession(OrtEnvironment env,
                 ModelConfig config,
                 OrtSession session) {
        this.env = env;
        this.session = session;
        this.config = config;
    }

    Long idForSymbol(String phoneme) {
        return config.phonemeIdMap().get(phoneme);
    }

    Audio sayPhonemes(int sid, long[] phoneme_ids, float gain, ModelParameters params) {
        long[][] shapedPhonemeIds = new long[][]{phoneme_ids};
        long[] phoneme_id_lengths = new long[]{phoneme_ids.length};
        try(var scales = OnnxTensor.createTensor(env, new float[]{params.noiseScale(),
                params.lengthScale(), params.noiseScaleW()});
            var input = OnnxTensor.createTensor(env, shapedPhonemeIds);
            var inputLengths = OnnxTensor.createTensor(env, phoneme_id_lengths)) {

            Map<String, OnnxTensor> inputsMap = new HashMap<>();
            inputsMap.put("scales",scales );
            inputsMap.put("input", input);
            inputsMap.put("input_lengths", inputLengths);
            if (sid != -1) {
                inputsMap.put("sid", OnnxTensor.createTensor(env, new long[]{sid}));
            }

            try (OrtSession.Result result = session.run(inputsMap)) {
                Optional<OnnxValue> v = result.get("output");
                if (v.isPresent()) {
                    OnnxValue value = v.get();
                    float[][][][] output = (float[][][][]) value.getValue();

                    return new Audio(output[0][0][0], 22050)
                            .withGain(gain);
                } else {
                    throw new RuntimeException("No output!");
                }
            }
        } catch (OrtException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        this.session.close();
    }


}