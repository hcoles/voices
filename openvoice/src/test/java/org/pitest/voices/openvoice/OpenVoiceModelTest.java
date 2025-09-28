package org.pitest.voices.openvoice;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import org.junit.jupiter.api.Test;
import org.pitest.g2p.core.Dictionary;
import org.pitest.g2p.core.G2PModel;
import org.pitest.g2p.core.Language;
import org.pitest.g2p.core.pos.Pos;
import org.pitest.g2p.core.tracing.Trace;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pitest.voices.ChorusConfig.chorusConfig;

class OpenVoiceModelTest {

    @Test
    void hello() throws Exception {
        try(var underTest = makeModel()) {
            var actual = underTest.predict(Trace.noTrace(), Language.en_GB, "hello", Pos.OTHER);
            assertThat(actual).isEqualTo("hələʊ");
        }
    }

    @Test
    void bonjour() throws Exception {
        try(var underTest = makeModel()) {
            var actual = underTest.predict(Trace.noTrace(), Language.fr_FR, "bonjour", Pos.OTHER);
            assertThat(actual).isEqualTo("bɔ̃ʒuʁ");
        }
    }

    private static G2PModel makeModel() {
        OpenVoiceSupplier supplier = new OpenVoiceSupplier();
        Path p = chorusConfig(Dictionary.empty()).base();

        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        return supplier.create(() -> options, Dictionary.empty(), OrtEnvironment.getEnvironment(), p);
    }
}