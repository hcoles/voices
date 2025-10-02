package org.pitest.voices.download;

import org.junit.jupiter.api.Test;
import org.pitest.voices.ChorusConfig;
import org.pitest.voices.Language;
import org.pitest.voices.Model;
import org.pitest.voices.g2p.core.Dictionary;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ModelsTest {

    static final int ARBITRARY_SIZE = 1000;
    Path cache = ChorusConfig.chorusConfig(Dictionary.empty()).base();

    @Test
    void coriHigh() throws IOException {
        var model = Models.coriHigh();
        checkModel(model);
    }

    @Test
    void albaMedium() throws IOException {
        var model = Models.albaMedium();
        checkModel(model);
    }

    @Test
    void aruMedium() throws IOException {
        var model = Models.aruMedium(1);
        checkModel(model);
    }

    @Test
    void alanMedium() throws IOException {
        var model = Models.alanMedium();
        checkModel(model);
    }

    @Test
    void jennyDiocoMedium() throws IOException {
        var model = Models.jennyDiocoMedium();
        checkModel(model);
    }

    @Test
    void sweetbbakAmy() throws IOException {
        var model = Models.sweetbbakAmy();
        checkModel(model);
    }

    @Test
    void northernEnglishMale() throws IOException {
        var model = Models.northernEnglishMale();
        checkModel(model);
    }

    private void checkModel(Model model) throws IOException {
        assertThat(model.asBytes(cache)).hasSizeGreaterThan(ARBITRARY_SIZE);
        assertThat(model.resolveConfig(cache).sampleRate()).isEqualTo(22050L);
        assertThat(model.language()).isEqualTo(Language.en_GB);
    }
}