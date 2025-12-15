package org.pitest.voices.kokoro;

import org.pitest.voices.Language;
import org.pitest.voices.Model;

import static org.pitest.voices.kokoro.KokoroHandler.kokoro;

public class KokoroModels {

    public static Model af() {
        return model("/models/kokoro/af.bin");
    }

    public static Model afBella() {
        return model("/models/kokoro/af_bella.bin");
    }

    public static Model afNicole() {
        return model("/models/kokoro/af_nicole.bin");
    }

    public static Model afSarah() {
        return model("/models/kokoro/af_sarah.bin");
    }

    public static Model afSky() {
        return model("/models/kokoro/af_sky.bin");
    }

    public static Model amAdam() {
        return model("/models/kokoro/am_adam.bin");
    }

    public static Model amMichael() {
        return model("/models/kokoro/am_michael.bin");
    }

    public static Model bfEmma() {
        return model("/models/kokoro/bf_emma.bin");
    }

    public static Model bfIsabella() {
        return model("/models/kokoro/bf_isabella.bin");
    }

    public static Model bmGeorge() {
        return model("/models/kokoro/bm_george.bin");
    }

    public static Model bmLewis() {
        return model("/models/kokoro/bm_lewis.bin");
    }

    private static Model model(String resource) {
        return new KokoroClasspathModel(kokoro(),resource,
                Language.en_GB,
                2.0f);
    }
}
