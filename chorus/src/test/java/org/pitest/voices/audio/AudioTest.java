package org.pitest.voices.audio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class AudioTest {

    @Test
    void savesToPath(@TempDir Path dir) {
        Audio a = Audio.silence(1);
        Path path = dir.resolve("test.wav");
        a.save(path);
        assertThat(path).exists();
        // we don't have code for reading wavs, so just check we wrote something
        assertThat(path.toFile().length()).isGreaterThan(0);
    }

    @Test
    void appends() {
        Audio a = Audio.silence(1);
        Audio b = Audio.silence(2);

        var combined = a.append(b);
        assertThat(combined.getSamples().length)
                .isEqualTo(a.getSamples().length + b.getSamples().length);
    }
}