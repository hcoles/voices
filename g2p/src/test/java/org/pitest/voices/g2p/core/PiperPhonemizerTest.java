package org.pitest.voices.g2p.core;

import org.junit.jupiter.api.Test;
import org.pitest.voices.Language;
import org.pitest.voices.g2p.core.dictionary.Dictionaries;
import org.pitest.voices.g2p.core.expansions.NumberExpander;
import org.pitest.voices.g2p.core.tracing.Trace;

import java.util.List;
import java.util.Map;


import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class PiperPhonemizerTest {

    Language lang = Language.en_GB;
    PiperPhonemizer underTest = new PiperPhonemizer(new EnglishModel(Dictionaries.empty()), emptyList(), Trace.noTrace());

    @Test
    void emDashesPassThrough() {
        assertThat(underTest.phonemize(lang,"—")).contains("—");
    }

    @Test
    void handlesPossessives() {
        PiperPhonemizer underTest = new PiperPhonemizer(new EnglishModel(Dictionaries.fromMap(Map.of("harry's", "x")))
                , emptyList(), Trace.noTrace());
        assertThat(underTest.phonemize(lang,"harry's")).contains("x");
    }

    @Test
    void handlesApostrophes() {
        PiperPhonemizer underTest = new PiperPhonemizer(new EnglishModel(Dictionaries.fromMap(Map.of("didn't", "x")))
                , emptyList(), Trace.noTrace());
        assertThat(underTest.phonemize(lang,"didn't do it")).contains("x");
    }

    @Test
    void usesExpansions() {
        PiperPhonemizer underTest = new PiperPhonemizer(new EnglishModel(Dictionaries.empty())
                , List.of(new NumberExpander()), Trace.noTrace());
        String result = String.join("", underTest.phonemize(lang,"The year is 2025."));
        assertThat(result).contains("twɛnˈti twɛnˈti fɪˈv");
    }

    @Test
    void handlesUppercase() {
        PiperPhonemizer underTest = new PiperPhonemizer(new EnglishModel(Dictionaries.empty())
                , emptyList(), Trace.noTrace());
        String result = String.join("", underTest.phonemize(lang,"HELLO WORLD"));
        assertThat(result).contains("həlˈəʊ wɔɹld");
    }

    @Test
    void normalizesSmartQuotes() {
        String result = String.join("", underTest.phonemize(lang,"“Hello” world"));
        assertThat(result).contains("\"");
        assertThat(result).doesNotContain("“", "”");
    }

    @Test
    void removesSApostrophe() {
        String result = String.join("", underTest.phonemize(lang,"James' book"));
        assertThat(result).doesNotContain("'");
    }

    @Test
    void preservesCommaPunctuation() {
        String result = String.join("", underTest.phonemize(lang,"Hello, world"));
        assertThat(result).contains(",");
    }

    @Test
    void emptyInputGivesEmptyOutput() {
        List<String> result = underTest.phonemize(lang,"");
        assertThat(result).isEmpty();
    }

    @Test
    void parenthesesPassThrough() {
        String result = String.join("", underTest.phonemize(lang,"(Hello)"));
        assertThat(result).contains("(").contains(")");
    }

    @Test
    void hyphenAndEnDashPassThrough() {
        String result = String.join("", underTest.phonemize(lang,"pre-test – post"));
        assertThat(result).contains("-").contains("–");
    }

    @Test
    void preservesQuestionAndExclamation() {
        String result = String.join("", underTest.phonemize(lang,"Really? Wow!"));
        assertThat(result).contains("?").contains("!");
    }

    @Test
    void newlineBetweenWordsBecomesSpace() {
        List<String> result = underTest.phonemize(lang,"Hello\nworld");
        assertThat(result).contains(" ");
    }
}