package org.pitest.voices.piper;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OnnxValue;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.pitest.voices.ModelConfig;
import org.pitest.voices.ModelParameters;
import org.pitest.voices.VoiceSession;
import org.pitest.voices.audio.Audio;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Wraps an OrtSession. Each session contains a loaded model, so
 * is expensive.
 */
public class PiperVoiceSession implements VoiceSession {

    private final static float NOISE_SCALE = 0.667f;
    private final static float NOISE_SCALE_W = 0.9f;

    private final OrtEnvironment env;
    private final OrtSession session;
    private final ModelConfig config;

    PiperVoiceSession(OrtEnvironment env,
                 ModelConfig config,
                 OrtSession session) {
        this.env = env;
        this.session = session;
        this.config = config;
    }

    @Override
    public Long idForSymbol(String phoneme) {
        return config.phonemeIdMap().get(phoneme);
    }

    @Override
    public Audio sayPhonemes(int sid, long[] unpaddedIds, float gain, ModelParameters params) {

        long[] paddedIds = new long[unpaddedIds.length + 2];
        paddedIds[0] = idForSymbol("^");
        System.arraycopy(unpaddedIds, 0, paddedIds, 1, unpaddedIds.length);
        paddedIds[paddedIds.length - 1] = idForSymbol("$");


        long[][] shapedPhonemeIds = new long[][]{paddedIds};
        long[] phoneme_id_lengths = new long[]{paddedIds.length};

        // smaller length scale gives faster speed
        float lengthScale = 2.0f - params.speed();

        try(var scales = OnnxTensor.createTensor(env, new float[]{NOISE_SCALE,
                lengthScale, NOISE_SCALE_W});
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