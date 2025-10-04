package org.pitest.voices.g2p.core;

import org.pitest.voices.g2p.core.dictionary.CombinedDictionary;
import org.pitest.voices.g2p.core.dictionary.MapDictionary;
import org.pitest.voices.g2p.core.dictionary.POSVariant;
import org.pitest.voices.g2p.core.pos.Pos;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyMap;

public interface Dictionary {

    boolean containsWord(String word);

    Set<String> words();

    Optional<String> get(String word, Pos pos);

    default Dictionary withAdditions(Dictionary useFirst) {
        return new CombinedDictionary(this, useFirst);
    }
    
}

