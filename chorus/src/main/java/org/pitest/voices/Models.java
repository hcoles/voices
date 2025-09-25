package org.pitest.voices;

import java.net.URL;
import java.util.List;

public class Models {


    public static Model coriHigh() {
        return sherpaModel("en_GB-cori-high", 2.0f);
    }

    public static Model albaMedium() {
        return sherpaModel("en_GB-alba-medium", 2.0f);
    }

    public static Model alanMedium() {
        return sherpaModel("en_GB-alan-medium", 0.8f);
    }

    public static Model jennyDiocoMedium() {
        return sherpaModel("en_GB-jenny_dioco-medium", 1.8f);
    }

    public static Model sweetbbakAmy() {
        return sherpaModel("en_GB-sweetbbak-amy", 0.8f);
    }

    public static Model northernEnglishMale() {
        return sherpaModel("en_GB-northern_english_male-medium", 1.1f);
    }

    static Model sherpaModel(String name, float gain) {
        return new Model(name,
                "vits-piper-" + name,
                new ModelDownloader(url("https://github.com/k2-fsa/sherpa-onnx/releases/download/tts-models/vits-piper-" + name + ".tar.bz2")),
                defaultPauses(), gain, ModelParameters.defaultParams()
        );
    }

    public static List<Pause> defaultPauses() {
        return List.of(new Pause("—", 3),
                new Pause("–", 2),
                new Pause(":", 2)
        );
    }

    private static URL url(String url) {
        try {
            return new URL(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
