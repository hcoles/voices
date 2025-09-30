package org.pitest.voices;

import org.pitest.g2p.core.Language;

import java.net.URL;
import java.util.List;

public class Models {


    public static Model coriHigh() {
        return sherpaModel("en_GB-cori-high", 2.0f);
    }

    public static Model albaMedium() {
        return sherpaModel("en_GB-alba-medium", 2.0f);
    }

    public static Model aru(int sid) {
        return sherpaModel("en_GB-aru-medium", sid,2.0f);
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

    private static Model sherpaModel(String name, float gain) {
        return sherpaModel(name, -1, gain);
    }

    private static Model sherpaModel(String name, int sid, float gain) {
        return new Model(name,
                "vits-piper-" + name,
                Language.en_GB,
                sid,
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

    static URL url(String url) {
        try {
            return new URL(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
