package org.pitest.voices.openvoice;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


class ByT5ONNXInference implements AutoCloseable {

    private final OrtEnvironment env;
    private final OrtSession session;
    private final ByT5Tokenizer tokenizer = new ByT5Tokenizer();

    ByT5ONNXInference(OrtEnvironment env, OrtSession session) {
        this.env = env;
        this.session = session;
    }

    String infer(String text, String lang) throws OrtException {
        String inputText = "<" + lang + ">: " + text;

        int[] inputIds = tokenizer.encode(inputText);
        int[] attentionMask = new int[inputIds.length];
        Arrays.fill(attentionMask, 1);

        int padTokenId = tokenizer.padTokenId();
        int eosTokenId = tokenizer.eosTokenId();
        List<Integer> generatedIds = new ArrayList<>();
        List<Integer> decoderInputIds = new ArrayList<>();
        decoderInputIds.add(padTokenId);

        int maxLength = 512;

        // Greedy decoding loop
        for (int step = 0; step < maxLength; step++) {
            try (OnnxTensor decoderInputTensor = toLongTensor(
                    decoderInputIds.stream().mapToInt(i -> i).toArray(),
                    1,
                    decoderInputIds.size()
            )) {

                OnnxTensor inputIdsTensor = toLongTensor(inputIds, 1, inputIds.length);
                OnnxTensor attentionMaskTensor = toLongTensor(attentionMask, 1, attentionMask.length);

                Map<String, OnnxTensor> inputs = Map.of(
                        "input_ids", inputIdsTensor,
                        "attention_mask", attentionMaskTensor,
                        "decoder_input_ids", decoderInputTensor);

                try (OrtSession.Result results = session.run(inputs)) {
                    float[][][] logits = (float[][][]) results.get(0).getValue();
                    float[] nextTokenLogits = logits[0][logits[0].length - 1];

                    // Greedy selection
                    int nextTokenId = argMax(nextTokenLogits);
                    generatedIds.add(nextTokenId);

                    if (nextTokenId == eosTokenId) {
                        break;
                    }

                    decoderInputIds.add(nextTokenId);
                }

            }
        }

        return tokenizer.decode(generatedIds.stream().mapToInt(i -> i).toArray());
    }

    private OnnxTensor toLongTensor(int[] data, int dim0, int dim1) throws OrtException {
        long[] shape = new long[]{dim0, dim1};
        long[] longData = Arrays.stream(data).asLongStream().toArray();
        LongBuffer buffer = LongBuffer.wrap(longData);
        return OnnxTensor.createTensor(env, buffer, shape);
    }

    private int argMax(float[] array) {
        int maxIdx = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIdx]) maxIdx = i;
        }
        return maxIdx;
    }


    public void close() throws OrtException {
        this.session.close();
    }
}
