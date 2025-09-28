package org.pitest.g2p.core;

import org.pitest.g2p.core.pos.Pos;
import org.pitest.g2p.core.tracing.Trace;

public interface G2PModel extends AutoCloseable {

    /**
     * Predict phonemes for a given word
     * @param trace - Trace to track lookup process
     * @param lang  - Target language
     * @param word - Word to convert to phonemes
     * @param pos - Part of speech (optional, for homograph disambiguation)
     * @return Phoneme string in IPA format
     */
    String predict(Trace trace, Language lang, String word, Pos pos);

}