package org.pitest.voices.download;

import org.pitest.voices.Language;
import org.pitest.voices.Model;

import static org.pitest.voices.download.Models.url;
import static org.pitest.voices.piper.PiperHandler.piper;


public class NonEnglishModels {

    public static Model frFRSiwis() {
        return sherpaModel("fr_FR-siwis-medium", Language.fr_FR, 1.0f);
    }

    public static Model nlNLRonnie() {
        return sherpaModel("nl_NL-ronnie-medium", Language.nl_NL, 1.0f);
    }

    private static Model sherpaModel(String name, Language lang, float gain) {
        return new FileModel(piper(), name,
                "vits-piper-" + name,
                lang,
                -1,
                new ModelDownloader(url("https://github.com/k2-fsa/sherpa-onnx/releases/download/tts-models/vits-piper-" + name + ".tar.bz2"))
                , gain );
    }
}
