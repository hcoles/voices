package org.pitest.voices.g2p.core;

import java.util.List;

public interface WordToSyllables {
    List<String> toSyllables(String word);
}
