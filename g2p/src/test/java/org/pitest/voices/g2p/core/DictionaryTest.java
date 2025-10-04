package org.pitest.voices.g2p.core;

import org.junit.jupiter.api.Test;
import org.pitest.voices.g2p.core.dictionary.Dictionaries;
import org.pitest.voices.g2p.core.pos.Pos;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DictionaryTest {

    @Test
    void emptyDictionaryIsEmpty() {
        Dictionary underTest = Dictionaries.empty();
        assertThat(underTest.words()).isEmpty();
    }

    @Test
    void homographDictionaryContainsSomeHomographs() {
        Dictionary underTest = Dictionaries.englishHomographs();
        assertThat(underTest.get("read", Pos.VBD))
                .isNotEqualTo(underTest.get("read", Pos.VB));
    }

    @Test
    void withAdditionsTakesPrecedenceOverExistingWords() {
        Dictionary base = Dictionaries.fromMap(Map.of("word", "x"));
        Dictionary extra = Dictionaries.fromMap(Map.of("word", "y"));

        Dictionary combined = base.withAdditions(extra);

        assertThat(combined.get("word", Pos.OTHER)).contains("y");
    }

    @Test
    void withAdditionsAddsWords() {
        Dictionary base = Dictionaries.fromMap(Map.of("one", "x", "two", "y"));
        Dictionary extra = Dictionaries.fromMap(Map.of("three", "a", "four", "b"));

        Dictionary combined = base.withAdditions(extra);

        assertThat(combined.words()).containsAll(List.of("one", "two", "three", "four"));
    }

    @Test
    void usesHomographsFromAdditionDictionary() {
        Dictionary homographs = Dictionaries.englishHomographs();
        Dictionary combined = Dictionaries.empty().withAdditions(homographs);

        assertThat(combined.words()).containsAll(homographs.words());

        assertThat(combined.get("read", Pos.VBD))
                .isNotEqualTo(combined.get("read", Pos.VB));
    }

}