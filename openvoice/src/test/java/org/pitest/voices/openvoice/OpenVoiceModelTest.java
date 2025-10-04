package org.pitest.voices.openvoice;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import org.junit.jupiter.api.Test;
import org.pitest.voices.Language;
import org.pitest.voices.g2p.core.Dictionary;
import org.pitest.voices.g2p.core.G2PModel;
import org.pitest.voices.g2p.core.dictionary.Dictionaries;
import org.pitest.voices.g2p.core.pos.Pos;
import org.pitest.voices.g2p.core.tracing.Trace;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pitest.voices.ChorusConfig.chorusConfig;

class OpenVoiceModelTest {

    @Test
    void hello() throws Exception {
        try(var underTest = makeModel(Dictionaries.empty())) {
            var actual = underTest.predict(Trace.noTrace(), Language.en_GB, "hello", Pos.OTHER);
            assertThat(actual).isEqualTo("hələʊ");
        }
    }

    @Test
    void bonjour() throws Exception {
        try(var underTest = makeModel(Dictionaries.empty())) {
            var actual = underTest.predict(Trace.noTrace(), Language.fr_FR, "bonjour", Pos.OTHER);
            assertThat(actual).isEqualTo("bɔ̃ʒuʁ");
        }
    }

    @Test
    void delegatesToDictionary() throws Exception {
        try(var underTest = makeModel(Dictionaries.fromMap(Map.of("goose", "xx")))) {
            var actual = underTest.predict(Trace.noTrace(), Language.en_US, "goose", Pos.OTHER);
            assertThat(actual).isEqualTo("xx");
        }
    }

    private static G2PModel makeModel(Dictionary dictionary) {
        OpenVoiceSupplier supplier = new OpenVoiceSupplier();
        Path p = chorusConfig(Dictionaries.empty()).base();

        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        return supplier.create(() -> options, dictionary, OrtEnvironment.getEnvironment(), p);
    }
}