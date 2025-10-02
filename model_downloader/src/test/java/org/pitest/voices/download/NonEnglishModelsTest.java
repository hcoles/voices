package org.pitest.voices.download;

import org.junit.jupiter.api.Test;
import org.pitest.voices.ChorusConfig;
import org.pitest.voices.Language;
import org.pitest.voices.Model;
import org.pitest.voices.g2p.core.Dictionary;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pitest.voices.download.ModelsTest.ARBITRARY_SIZE;

class NonEnglishModelsTest {

    Path cache = ChorusConfig.chorusConfig(Dictionary.empty()).base();

    @Test
    void frFRSiwis() throws IOException {
        var model = NonEnglishModels.frFRSiwis();
        checkModel(model);
        assertThat(model.language()).isEqualTo(Language.fr_FR);
    }

    @Test
    void nlNLRonnie() throws IOException {
        var model = NonEnglishModels.nlNLRonnie();
        checkModel(model);
        assertThat(model.language()).isEqualTo(Language.nl_NL);
    }

    private void checkModel(Model model) throws IOException {
        assertThat(model.asBytes(cache)).hasSizeGreaterThan(ARBITRARY_SIZE);
        assertThat(model.resolveConfig(cache).sampleRate()).isEqualTo(22050L);
    }
}