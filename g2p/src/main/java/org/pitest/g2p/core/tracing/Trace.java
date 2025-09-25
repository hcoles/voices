package org.pitest.g2p.core.tracing;

import org.pitest.g2p.core.pos.Pos;

import java.util.List;

/**
 * Trace the process of converting words to phonemes.
 * Provides methods to log various stages and outcomes of the conversion process.
 */
public interface Trace {
    Trace start(String word, Pos pos);

    void result(String phonemes);

    void dictionaryHit(String word, Pos pos, String s);

    void morphology(String base);

    void phonemeRule(String pattern, String remaining);

    void syllables(List<String> syllables);

    void unknownPhoneme(String phoneme);

    static Trace noTrace() {
        return new Trace() {
            @Override
            public Trace start(String word, Pos pos) {
                return this;
            }

            @Override
            public void result(String phonemes) {

            }

            @Override
            public void dictionaryHit(String word, Pos pos, String s) {

            }

            @Override
            public void morphology(String base) {

            }

            @Override
            public void phonemeRule(String pattern, String remaining) {

            }

            @Override
            public void syllables(List<String> syllables) {

            }

            @Override
            public void unknownPhoneme(String phoneme) {

            }


        };
    }


}
