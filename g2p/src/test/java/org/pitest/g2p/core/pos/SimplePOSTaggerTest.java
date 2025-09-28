package org.pitest.g2p.core.pos;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pitest.g2p.core.pos.SimplePOSTagger.makeTagger;

class SimplePOSTaggerTest {

    SimplePOSTagger underTest = makeTagger();

    @Test
    void tagsVerbs() {
        var actual = underTest.tagWords(arrayOf("I", "walked", "there"));
        assertThat(actual).contains(new POSToken("walked", Pos.VBD));
    }

    @Test
    void tagsNouns() {
        var actual = underTest.tagWords(arrayOf("The", "cat", "hissed"));
        assertThat(actual).contains(new POSToken("cat", Pos.NN));
    }

    @Test
    void tagsEmdashAsSymbol() {
        var actual = underTest.tagWords(arrayOf( "—"));
        assertThat(actual).contains(new POSToken("—", Pos.SYM));
    }

    @Test
    void tagsEndashAsSymbol() {
        var actual = underTest.tagWords(arrayOf( "-"));
        assertThat(actual).contains(new POSToken("-", Pos.SYM));
    }

    @Test
    void tagsElipsisAsSymbol() {
        var actual = underTest.tagWords(arrayOf( "…"));
        assertThat(actual).contains(new POSToken("…", Pos.SYM));
    }

    @Test
    void tagsBacktickAsSymbol() {
        var actual = underTest.tagWords(arrayOf( "`"));
        assertThat(actual).contains(new POSToken("`", Pos.SYM));
    }

    @Test
    void mapsDeterminerToVerbGroup() {
        var actual = underTest.tagWords(arrayOf("The", "dog", "slept"));
        assertThat(actual).contains(new POSToken("The", Pos.VB));
    }

    @Test
    void mapsPrepositionToVerbGroup() {
        var actual = underTest.tagWords(arrayOf("We", "sat", "in", "silence"));
        assertThat(actual).contains(new POSToken("in", Pos.VB));
    }

    @Test
    void mapsProperNounToNounGroup() {
        var actual = underTest.tagWords(arrayOf("Sally", "smiled"));
        assertThat(actual).contains(new POSToken("Sally", Pos.NN));
    }

    @Test
    void mapsGerundToVbp() {
        var actual = underTest.tagWords(arrayOf("We", "are", "running"));
        assertThat(actual).contains(new POSToken("running", Pos.VBP));
    }

    @Test
    void mapsThirdPersonVerbToVbp() {
        var actual = underTest.tagWords(arrayOf("She", "walks", "home"));
        assertThat(actual).contains(new POSToken("walks", Pos.VBP));
    }

    @Test
    void gluesContractionRetainingPos() {
        var actual = underTest.tagWords(arrayOf("it", "'", "s", "ok"));
        assertThat(actual).contains(new POSToken("it's", Pos.OTHER));
    }

    @Test
    void gluesPossessiveRetainingPos() {
        var actual = underTest.tagWords(arrayOf("John", "'", "s", "hat"));
        assertThat(actual).contains(new POSToken("John's", Pos.NN));
    }

    private String[] arrayOf(String... words) {
        return words;
    }
}