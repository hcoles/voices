package org.pitest.voices.kokoro;

import org.junit.jupiter.api.Test;
import org.pitest.voices.Language;
import org.pitest.voices.Model;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class KokoroModelsTest {

    @Test
    void af() {
        modelResolves(KokoroModels.af());
    }

    @Test
    void afBella() {
        modelResolves(KokoroModels.afBella());
    }

    @Test
    void afNicole() {
        modelResolves(KokoroModels.afNicole());
    }

    @Test
    void afSarah() {
        modelResolves(KokoroModels.afSarah());
    }

    @Test
    void afSky() {
        modelResolves(KokoroModels.afSky());
    }

    @Test
    void amAdam() {
        modelResolves(KokoroModels.amAdam());
    }

    @Test
    void amMichael() {
        modelResolves(KokoroModels.amMichael());
    }

    @Test
    void bfEmma() {
        modelResolves(KokoroModels.bfEmma());
    }

    @Test
    void bfIsabella() {
        modelResolves(KokoroModels.bfIsabella());
    }

    @Test
    void bmGeorge() {
        modelResolves(KokoroModels.bmGeorge());
    }

    @Test
    void bmLewis() {
        modelResolves(KokoroModels.bmLewis());
    }


    private void modelResolves(Model underTest) {
        try {
            assertThat(underTest.language()).isEqualTo(Language.en_GB);
            assertThat(underTest.asBytes(unused())).hasSizeGreaterThan(50000);
            assertThat(underTest.resolveConfig(unused()).sampleRate()).isEqualTo(22050L);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    private Path unused() {
        return null;
    }
}