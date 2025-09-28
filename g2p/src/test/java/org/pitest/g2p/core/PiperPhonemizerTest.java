package org.pitest.g2p.core;

import org.junit.jupiter.api.Test;
import org.pitest.g2p.core.expansions.NumberExpander;
import org.pitest.g2p.core.tracing.Trace;

import java.util.List;
import java.util.Map;


import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class PiperPhonemizerTest {

    Language lang = Language.en_GB;
    PiperPhonemizer underTest = new PiperPhonemizer(new EnglishModel(Dictionary.empty()), emptyList(), Trace.noTrace());

    @Test
    void addsStartMarker() {
        assertThat(underTest.toPhonemes(lang,"Hello world!")).startsWith("^");
    }

    @Test
    void addsEndMarker() {
        assertThat(underTest.toPhonemes(lang,"Hello world!")).endsWith("$");
    }

    @Test
    void emDashesPassThrough() {
        assertThat(underTest.toPhonemes(lang,"—")).contains("—");
    }

    @Test
    void handlesPossessives() {
        PiperPhonemizer underTest = new PiperPhonemizer(new EnglishModel(Dictionary.fromMap(Map.of("harry's", "x")))
                , emptyList(), Trace.noTrace());
        assertThat(underTest.toPhonemes(lang,"harry's")).contains("x");
    }

    @Test
    void handlesApostrophes() {
        PiperPhonemizer underTest = new PiperPhonemizer(new EnglishModel(Dictionary.fromMap(Map.of("didn't", "x")))
                , emptyList(), Trace.noTrace());
        assertThat(underTest.toPhonemes(lang,"didn't do it")).contains("x");
    }

    @Test
    void usesExpansions() {
        PiperPhonemizer underTest = new PiperPhonemizer(new EnglishModel(Dictionary.empty())
                , List.of(new NumberExpander()), Trace.noTrace());
        String result = String.join("", underTest.toPhonemes(lang,"The year is 2025."));
        assertThat(result).contains("twɛnˈti twɛnˈti fɪˈv");
    }

    @Test
    void handlesUppercase() {
        PiperPhonemizer underTest = new PiperPhonemizer(new EnglishModel(Dictionary.empty())
                , emptyList(), Trace.noTrace());
        String result = String.join("", underTest.toPhonemes(lang,"HELLO WORLD"));
        assertThat(result).contains("həlˈəʊ wɔɹld");
    }

    @Test
    void normalizesSmartQuotes() {
        String result = String.join("", underTest.toPhonemes(lang,"“Hello” world"));
        assertThat(result).contains("\"");
        assertThat(result).doesNotContain("“", "”");
    }

    @Test
    void removesSApostrophe() {
        String result = String.join("", underTest.toPhonemes(lang,"James' book"));
        assertThat(result).doesNotContain("'");
    }

    @Test
    void preservesCommaPunctuation() {
        String result = String.join("", underTest.toPhonemes(lang,"Hello, world"));
        assertThat(result).contains(",");
    }

    @Test
    void emptyInputHasOnlyMarkers() {
        List<String> result = underTest.toPhonemes(lang,"");
        assertThat(result).startsWith("^").endsWith("$");
        assertThat(result).hasSize(2);
    }

    @Test
    void parenthesesPassThrough() {
        String result = String.join("", underTest.toPhonemes(lang,"(Hello)"));
        assertThat(result).contains("(").contains(")");
    }

    @Test
    void hyphenAndEnDashPassThrough() {
        String result = String.join("", underTest.toPhonemes(lang,"pre-test – post"));
        assertThat(result).contains("-").contains("–");
    }

    @Test
    void preservesQuestionAndExclamation() {
        String result = String.join("", underTest.toPhonemes(lang,"Really? Wow!"));
        assertThat(result).contains("?").contains("!");
    }

    @Test
    void newlineBetweenWordsBecomesSpace() {
        List<String> result = underTest.toPhonemes(lang,"Hello\nworld");
        assertThat(result).contains(" ");
    }
}