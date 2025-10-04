package org.pitest.voices.g2p.core.dictionary;

import org.pitest.voices.g2p.core.Dictionary;
import org.pitest.voices.g2p.core.pos.Pos;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CombinedDictionary implements Dictionary {

    private final Dictionary base;
    private final Dictionary useFirst;

    public CombinedDictionary(Dictionary base, Dictionary useFirst) {
        this.base = base;
        this.useFirst = useFirst;
    }

    @Override
    public boolean containsWord(String word) {
        return base.containsWord(word) || useFirst.containsWord(word);
    }

    @Override
    public Set<String> words() {
        var combined = new HashSet<>(base.words());
        combined.addAll(useFirst.words());
        return combined;
    }

    @Override
    public Optional<String> get(String word, Pos pos) {
        return useFirst.get(word, pos)
                .or(() -> base.get(word, pos));
    }
}
