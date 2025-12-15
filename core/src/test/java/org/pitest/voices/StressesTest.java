package org.pitest.voices;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StressesTest {

    @Test
    void withStressKeepStressMarkers() {
        assertThat(Stresses.KEEP_STRESS.apply("aˈ")).isEqualTo("aˈ");
    }

    @Test
    void noStressRemovesAllStressMarkers() {
        assertThat(Stresses.NO_STRESS.apply("aˈ")).isEqualTo("a");
    }
}