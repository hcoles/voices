package org.pitest.voices.openvoice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ByT5TokenizerTest {

    ByT5Tokenizer underTest = new ByT5Tokenizer();

    @Test
    void hello() {
        var actual = underTest.encode("hello");
        assertThat(actual).contains(107, 104, 111, 111, 114);
    }

    @Test
    void roundTrip() {
        var encoded = underTest.encode("round the ragged rock");
        var decoded = underTest.decode(encoded);
        assertThat(decoded).isEqualTo("round the ragged rock");
    }

    @Test
    void emptyString() {
        var encoded = underTest.encode("");
        assertThat(encoded).isEmpty();
        var decoded = underTest.decode(encoded);
        assertThat(decoded).isEmpty();
    }

    @Test
    void decodeIgnoresSpecialTokens() {
        var a = underTest.encode("A"); // 1 char => length 1
        var b = underTest.encode("B");
        int[] withSpecials = new int[] {
                underTest.padTokenId(), // should be ignored
                a[0],
                underTest.eosTokenId(), // should be ignored
                b[0],
                ByT5Tokenizer.UNK_TOKEN_ID // should be ignored
        };
        var decoded = underTest.decode(withSpecials);
        assertThat(decoded).isEqualTo("AB");
    }


}