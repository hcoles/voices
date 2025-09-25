package org.pitest.g2p.core;

import org.junit.jupiter.api.Test;
import org.pitest.g2p.core.expansions.NumberExpander;
import org.pitest.g2p.core.tracing.Trace;

import java.util.List;
import java.util.Map;


import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class PiperPhonemizerTest {

    PiperPhonemizer underTest = new PiperPhonemizer(new EnglishModel(Dictionary.empty()), emptyList(), Trace.noTrace());

    @Test
    void addsStartMarker() {
        assertThat(underTest.toPhonemes("Hello world!")).startsWith("^");
    }

    @Test
    void addsEndMarker() {
        assertThat(underTest.toPhonemes("Hello world!")).endsWith("$");
    }

    @Test
    void emDashesPassThrough() {
        assertThat(underTest.toPhonemes("—")).contains("—");
    }

    @Test
    void handlesPossessives() {
        PiperPhonemizer underTest = new PiperPhonemizer(new EnglishModel(Dictionary.fromMap(Map.of("harry's", "x")))
                , emptyList(), Trace.noTrace());
        assertThat(underTest.toPhonemes("harry's")).contains("x");
    }

    @Test
    void handlesApostrophes() {
        PiperPhonemizer underTest = new PiperPhonemizer(new EnglishModel(Dictionary.fromMap(Map.of("didn't", "x")))
                , emptyList(), Trace.noTrace());
        assertThat(underTest.toPhonemes("didn't do it")).contains("x");
    }

    @Test
    void usesExpansions() {
        PiperPhonemizer underTest = new PiperPhonemizer(new EnglishModel(Dictionary.empty())
                , List.of(new NumberExpander()), Trace.noTrace());
        String result = String.join("", underTest.toPhonemes("The year is 2025."));
        assertThat(result).contains("twɛnˈti twɛnˈti fɪˈv");
    }

    @Test
    void handlesUppercase() {
        PiperPhonemizer underTest = new PiperPhonemizer(new EnglishModel(Dictionary.empty())
                , emptyList(), Trace.noTrace());
        String result = String.join("", underTest.toPhonemes("HELLO WORLD"));
        assertThat(result).contains("həlˈəʊ wɔɹld");
    }

    @Test
    void normalizesSmartQuotes() {
        String result = String.join("", underTest.toPhonemes("“Hello” world"));
        assertThat(result).contains("\"");
        assertThat(result).doesNotContain("“", "”");
    }

    @Test
    void removesSApostrophe() {
        String result = String.join("", underTest.toPhonemes("James' book"));
        assertThat(result).doesNotContain("'");
    }

    @Test
    void preservesCommaPunctuation() {
        String result = String.join("", underTest.toPhonemes("Hello, world"));
        assertThat(result).contains(",");
    }

    @Test
    void emptyInputHasOnlyMarkers() {
        List<String> result = underTest.toPhonemes("");
        assertThat(result).startsWith("^").endsWith("$");
        assertThat(result).hasSize(2);
    }

    @Test
    void parenthesesPassThrough() {
        String result = String.join("", underTest.toPhonemes("(Hello)"));
        assertThat(result).contains("(").contains(")");
    }

    @Test
    void hyphenAndEnDashPassThrough() {
        String result = String.join("", underTest.toPhonemes("pre-test – post"));
        assertThat(result).contains("-").contains("–");
    }

    @Test
    void preservesQuestionAndExclamation() {
        String result = String.join("", underTest.toPhonemes("Really? Wow!"));
        assertThat(result).contains("?").contains("!");
    }

    @Test
    void newlineBetweenWordsBecomesSpace() {
        List<String> result = underTest.toPhonemes("Hello\nworld");
        assertThat(result).contains(" ");
    }
}