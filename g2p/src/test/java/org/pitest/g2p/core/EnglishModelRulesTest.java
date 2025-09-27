package org.pitest.g2p.core;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.pitest.g2p.core.pos.Pos;
import org.pitest.g2p.core.tracing.Trace;
import org.pitest.g2p.core.syllables.RulesSyllabiliser;


import static org.assertj.core.api.Assertions.assertThat;

class EnglishModelRulesTest {

    Language lang = Language.en_GB;
    EnglishModel underTest = new EnglishModel(Dictionary.empty(), new RulesSyllabiliser());


    @ParameterizedTest
    @CsvSource({
            "a, æ",
            "I, ɪ"
    })
    void singleLetterWords(String word, String expected) {
        checkWithoutDictionary(word, expected);
    }

    @ParameterizedTest
    @CsvSource({
            "cat, kæt",
            "bed, bɛd",
            "sit, sɪt",
            "cot, kɒt",
            "cut, kʌt"
    })
    void vowels(String word, String expected) {
        checkWithoutDictionary(word, expected);
    }

    @ParameterizedTest
    @CsvSource({
            "action, ʃən",
            "fraction, ʃən",
            "nation, ʃən",
            "station, ʃən",
            "faction, ʃən",
            "traction, ʃən"
    })
    void tionSuffix(String word, String expected) {
        checkEndsWith(word, expected);
    }

    @ParameterizedTest
    @CsvSource({
            "mansion, ʃən",
            "lesion, ʃən",
            "session, ʃən",
            "impression, ʃən"
    })
    void sionSuffix(String word, String expected) {
        checkEndsWith(word, expected);
    }

    @ParameterizedTest
    @CsvSource({
            "social, ʃəl",
            "commercial, ʃəl"
    })
    void cialSuffix(String word, String expected) {
        checkEndsWith(word, expected);
    }

    @ParameterizedTest
    @CsvSource({
            "potential, ʃəl",
            "partial, ʃəl"
    })
    void tialSuffix(String word, String expected) {
        checkEndsWith(word, expected);
    }

    @ParameterizedTest
    @CsvSource({
            "gorgeous, dʒəs",
            "advantageous, dʒəs"
    })
    void geousSuffix(String word, String expected) {
        checkEndsWith(word, expected);
    }

    @ParameterizedTest
    @CsvSource({
            "ambitious, ʃəs",
            "nutritious, ʃəs"
    })
    void tiousSuffix(String word, String expected) {
        checkEndsWith(word, expected);
    }

    @ParameterizedTest
    @CsvSource({
            "agreeable, əbəl",
            "capable, əbəl"
    })
    void ableSuffix(String word, String expected) {
        checkEndsWith(word, expected);
    }

    @ParameterizedTest
    @CsvSource({
            "visible, ɪblɪ",
            "responsible, ɪblɪ"
    })
    void ibleSuffix(String word, String expected) {
        checkEndsWith(word, expected);
    }

    @ParameterizedTest
    @CsvSource({
            "happiness, nəs",
            "sadness, nəs"
    })
    void nessSuffix(String word, String expected) {
        checkEndsWith(word, expected);
    }

    @ParameterizedTest
    @CsvSource({
            "agreement, mənt",
            "merriment, mənt"
    })
    void mentSuffix(String word, String expected) {
        checkEndsWith(word, expected);
    }


    @ParameterizedTest
    @CsvSource({
            "useless, ləs",
            "careless, ləs"
    })
    void lessSuffix(String word, String expected) {
        checkEndsWith(word, expected);
    }

    @ParameterizedTest
    @CsvSource({
            "helpful, fəl",
            "useful, fəl"
    })
    void fulSuffix(String word, String expected) {
        checkEndsWith(word, expected);
    }

    @ParameterizedTest
    @CsvSource({
            "quickly, li",
            "slowly, li"
    })
    void lySuffix(String word, String expected) {
        checkEndsWith(word, expected);
    }

    @ParameterizedTest
    @CsvSource({
            "gunner, ɚ",
            "runner, ɚ"
    })
    void erSuffix(String word, String expectedSuffix) {
        checkEndsWith(word, expectedSuffix);
    }

    private void checkEndsWith(String word, String expected) {
        String actual = underTest.predict(Trace.noTrace(), lang, word, Pos.VBD);
        assertThat(actual).endsWith(expected);
    }

    private void checkWithoutDictionary(String word, String expected) {
        String actual = underTest.predict(Trace.noTrace(), lang, word, Pos.VBD);
        assertThat(actual).isEqualTo(expected);
    }

    private static String ipaNoStress(String phonemes) {
        return phonemes.replaceAll("[ˈˌ]", "");
    }
}