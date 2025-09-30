package org.pitest.voices;

import org.pitest.g2p.core.Language;

import static org.pitest.voices.Models.url;

public class NonEnglishModels {

    public static Model frFRSiwis() {
        return sherpaModel("fr_FR-siwis-medium", Language.fr_FR, 1.0f);
    }

    public static Model nlNLRonnie() {
        return sherpaModel("nl_NL-ronnie-medium", Language.nl_NL, 1.0f);
    }

    private static Model sherpaModel(String name, Language lang, float gain) {
        return new FileModel(name,
                "vits-piper-" + name,
                lang,
                new ModelDownloader(url("https://github.com/k2-fsa/sherpa-onnx/releases/download/tts-models/vits-piper-" + name + ".tar.bz2"))
                , gain );
    }
}
