package org.pitest.voices.g2p.core.syllables;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


import static org.assertj.core.api.Assertions.assertThat;

class RulesSyllabiliserTest {
    RulesSyllabiliser underTest = new RulesSyllabiliser();

    @Test
    void handleApostrophes() {
        assertThat(underTest.toSyllables("henry's"))
                .containsExactly("hen", "rys");
    }

    @Test
    void apple() {
        assertThat(underTest.toSyllables("apple"))
                .containsExactly("ap", "ple");
    }

    @Test
    void thatIsOneSyllable() {
        assertThat(underTest.toSyllables("that"))
                .containsExactly("that");
    }

    @Test
    void debatable() {
        assertThat(underTest.toSyllables("debatable"))
                .containsExactly("de", "ba", "ta", "ble");
    }

    @Test
    void rate() {
        assertThat(underTest.toSyllables("rate"))
                .containsExactly("ra", "te");
    }

    @Test
    void dine() {
        assertThat(underTest.toSyllables("dine"))
                .containsExactly("di", "ne");
    }

    @Test
    void hello() {
        assertThat(underTest.toSyllables("hello"))
                .containsExactly("hel", "lo");
    }

    @ParameterizedTest
    @CsvSource({
            "the",
            "for",
            "this",
            "on;ly",
            "con;tact",
            "un;bound",
            "kin;dred",
            "top;ping",
            "un;mar;ried",
            "pel;vic",
            "sad;ness",
            "ef;fluent",
            "cu;cum;ber",
            "mul;ti;pli;ca;tion",
            "cam",
            "cot;ton",
            "por;tion",
            "firm;ness",
    })
    void matchesKnownWords(String expected) {
        // Words + synonyms here are sampled from an internet list
        // they are cherry-picked to be ones that match correctly, many
        // more are split differently. Intent of test is purely to catch
        // regression

        String actual = String.join(";", underTest.toSyllables(expected.replaceAll(";","")));
        assertThat(actual).isEqualTo(expected);
    }

}