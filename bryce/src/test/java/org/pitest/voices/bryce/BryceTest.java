package org.pitest.voices.bryce;

import org.junit.jupiter.api.Test;
import org.pitest.voices.Language;
import org.pitest.voices.Model;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class BryceTest {

    Model underTest = Bryce.bryceMedium();

    @Test
    void speaksUSEnglish() {
        assertThat(underTest.language()).isEqualTo(Language.en_US);
    }

    @Test
    void loadsResources() throws IOException {
        assertThat(underTest.asBytes(unused())).hasSizeGreaterThan(50000);
        assertThat(underTest.resolveConfig(unused()).sampleRate()).isEqualTo(22050L);
    }

    private Path unused() {
        return null;
    }
}