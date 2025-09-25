package org.pitest.g2p.core;

import org.pitest.g2p.core.pos.Pos;
import org.pitest.g2p.core.tracing.Trace;

public interface G2PModel {

    /**
     * Predict phonemes for a given word
     * 
     * @param word - Word to convert to phonemes
     * @param pos - Part of speech (optional, for homograph disambiguation)
     * @return Phoneme string in IPA format, or null if cannot process
     */
    String predict(Trace trace, String word, Pos pos);

}