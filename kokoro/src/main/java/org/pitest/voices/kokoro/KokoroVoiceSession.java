package org.pitest.voices.kokoro;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.pitest.voices.ModelConfig;
import org.pitest.voices.ModelParameters;
import org.pitest.voices.VoiceSession;
import org.pitest.voices.audio.Audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.LongBuffer;
import java.util.HashMap;
import java.util.Map;


public class KokoroVoiceSession implements VoiceSession {
    private final byte[] voiceBytes;
    private final OrtSession session;
    private final ModelConfig config;

    KokoroVoiceSession(byte[] voiceBytes,
                       ModelConfig config,
                       OrtSession session) {
        this.voiceBytes = voiceBytes;
        this.session = session;
        this.config = config;
    }

    @Override
    public Long idForSymbol(String phoneme) {
        return config.phonemeIdMap().get(phoneme);
    }

    @Override
    public Audio sayPhonemes(int sid, long[] phoneme_ids, float gain, ModelParameters params) {

       try {

           long[] padded = new long[phoneme_ids.length + 2];
           padded[0] = 0;
           System.arraycopy(phoneme_ids, 0, padded, 1, phoneme_ids.length);
           padded[padded.length - 1] = 0;

           long[] inputShape = new long[]{1, padded.length};
           OnnxTensor inputIds = OnnxTensor.createTensor(
                   OrtEnvironment.getEnvironment(),
                   LongBuffer.wrap(padded),
                   inputShape
           );

           FloatBuffer voiceBuf = ByteBuffer.wrap(voiceBytes)
                   .order(ByteOrder.LITTLE_ENDIAN)
                   .asFloatBuffer();

           int totalFloats = voiceBuf.remaining();
           int styleCount = totalFloats / 256;

           if (styleCount <= padded.length) {
               throw new RuntimeException("style does not contain enough rows");
           }

           float[] selectedStyle = new float[256];
           voiceBuf.position(padded.length * 256);
           voiceBuf.get(selectedStyle);

           long[] styleShape = new long[]{1, 256};
           OnnxTensor styleTensor = OnnxTensor.createTensor(
                   OrtEnvironment.getEnvironment(),
                   FloatBuffer.wrap(selectedStyle),
                   styleShape
           );

           float[] speedArr = new float[]{params.speed()};
           OnnxTensor speedTensor = OnnxTensor.createTensor(
                   OrtEnvironment.getEnvironment(),
                   FloatBuffer.wrap(speedArr),
                   new long[]{1}
           );


           Map<String, OnnxTensor> inputs = new HashMap<>();
           inputs.put("input_ids", inputIds);
           inputs.put("style", styleTensor);
           inputs.put("speed", speedTensor);

           try (OrtSession.Result result = session.run(inputs)) {
               float[][] audio = (float[][]) result.get(0).getValue();
               return new Audio(audio[0], 22050);
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
