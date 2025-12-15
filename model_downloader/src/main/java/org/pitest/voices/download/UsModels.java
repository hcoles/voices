package org.pitest.voices.download;


import org.pitest.voices.Language;
import org.pitest.voices.Model;

import static org.pitest.voices.download.Models.url;
import static org.pitest.voices.piper.PiperHandler.piper;

public class UsModels {

    public static Model amyMedium() {
        return sherpaModel("en_US-amy-medium", 1.0f);
    }

    public static Model bryceMedium() {
        return sherpaModel("en_US-bryce-medium", 1.0f);
    }

    public static Model hfcFemaleMedium() {
        return sherpaModel("en_US-hfc_female-medium", 1.0f);
    }

    public static Model hfcMaleMedium() {
        return sherpaModel("en_US-hfc_male-medium", 1.0f);
    }

    public static Model joeMedium() {
        return sherpaModel("en_US-joe-medium", 1.0f);
    }

    public static Model johnMedium() {
        return sherpaModel("en_US-john-medium", 1.0f);
    }

    public static Model kristinMedium() {
        return sherpaModel("en_US-kristin-medium", 1.0f);
    }

    public static Model kusalMedium() {
        return sherpaModel("en_US-kusal-medium", 1.0f);
    }

    public static Model lessacHigh() {
        return sherpaModel("en_US-lessac-high", 1.0f);
    }

    public static Model normanMedium() {
        return sherpaModel("en_US-norman-medium", 1.0f);
    }

    public static Model ryanHigh() {
        return sherpaModel("en_US-ryan-high", 1.0f);
    }

    public static Model samMedium() {
        return sherpaModel("en_US-sam-medium", 1.0f);
    }

    private static Model sherpaModel(String name, float gain) {
        return new FileModel(piper(), name,
                "vits-piper-" + name,
                Language.en_US,
                new ModelDownloader(url("https://github.com/k2-fsa/sherpa-onnx/releases/download/tts-models/vits-piper-" + name + ".tar.bz2")),
                gain);
    }
}
