package org.pitest.voices.g2p.core.dictionary;

import org.pitest.voices.g2p.core.Dictionary;
import org.pitest.voices.g2p.core.pos.Pos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public class MapDictionary implements Dictionary {

    private final Map<String, String> map;
    private final Map<POSVariant, String> posHomographs;

    MapDictionary(Map<String, String> map, Map<POSVariant, String> posHomographs) {
        this.map = map;
        this.posHomographs = posHomographs;
    }

    @Override
    public boolean containsWord(String word) {
        return map.containsKey(word);
    }

    @Override
    public Set<String> words() {
        return map.keySet();
    }

    @Override
    public Optional<String> get(String word, Pos pos) {
        POSVariant v = new POSVariant(pos, word);
        String specialised = posHomographs.get(v);
        if (specialised != null) {
            return Optional.of(specialised);
        }

        return Optional.ofNullable(map.get(word));
    }

    public static MapDictionary fromList(List<String> entries) {
        final Map<String, String> map = new HashMap<>();
        final Map<POSVariant, String> posHomographs = new HashMap<>();
        for (String line : entries) {
            if (line.startsWith("#")) {
                continue;
            }
            String[] parts = line.split("=");
            String word = parts[0];
            if (parts[1].contains("|")) {
                String[] posParts = parts[1].split("\\|");
                String ipa = posParts[0];
                for (int i = 1; i < posParts.length; i++) {
                    Pos pos = Pos.valueOf(posParts[i]);
                    POSVariant v = new POSVariant(pos, word);
                    posHomographs.put(v, ipa);
                }

            } else {
                map.put(word, parts[1]);
            }

        }
        return new MapDictionary(map, posHomographs);
    }

    @Override
    public String toString() {
        return "Dictionary{" +
                "map=" + map.size() +
                ", posHomographs=" + posHomographs.size() +
                '}';
    }
}