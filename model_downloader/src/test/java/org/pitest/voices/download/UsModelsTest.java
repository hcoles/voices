package org.pitest.voices.download;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.pitest.voices.ChorusConfig;
import org.pitest.voices.Language;
import org.pitest.voices.Model;
import org.pitest.voices.g2p.core.Dictionary;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pitest.voices.download.ModelsTest.ARBITRARY_SIZE;

@Disabled("don't keep downloading models on each commit")
class UsModelsTest {
    Path cache = ChorusConfig.chorusConfig(Dictionary.empty()).base();

    @Test
    void amyMedium() throws IOException {
        var model = UsModels.amyMedium();
        checkModel(model);
    }

    @Test
    void bryceMedium() throws IOException {
        var model = UsModels.bryceMedium();
        checkModel(model);
    }

    @Test
    void hfcFemaleMedium() throws IOException {
        var model = UsModels.hfcFemaleMedium();
        checkModel(model);
    }

    @Test
    void hfcMaleMedium() throws IOException {
        var model = UsModels.hfcMaleMedium();
        checkModel(model);
    }

    @Test
    void joeMedium() throws IOException {
        var model = UsModels.joeMedium();
        checkModel(model);
    }

    @Test
    void johnMedium() throws IOException {
        var model = UsModels.johnMedium();
        checkModel(model);
    }

    @Test
    void kristinMedium() throws IOException {
        var model = UsModels.kristinMedium();
        checkModel(model);
    }

    @Test
    void kusalMedium() throws IOException {
        var model = UsModels.kusalMedium();
        checkModel(model);
    }

    @Test
    void lessacHigh() throws IOException {
        var model = UsModels.lessacHigh();
        checkModel(model);
    }

    @Test
    void normanMedium() throws IOException {
        var model = UsModels.normanMedium();
        checkModel(model);
    }

    @Test
    void ryanHigh() throws IOException {
        var model = UsModels.ryanHigh();
        checkModel(model);
    }

    @Test
    void samMedium() throws IOException {
        var model = UsModels.samMedium();
        checkModel(model);
    }

    private void checkModel(Model model) throws IOException {
        assertThat(model.asBytes(cache)).hasSizeGreaterThan(ARBITRARY_SIZE);
        assertThat(model.resolveConfig(cache).sampleRate()).isEqualTo(22050L);
        assertThat(model.language()).isEqualTo(Language.en_US);
    }
}