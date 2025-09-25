package org.pitest.g2p.core;

import org.pitest.g2p.core.pos.Pos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyMap;
import static org.pitest.g2p.util.Resource.read;

public class Dictionary {

    private final Map<String, String> map;
    private final Map<POSVariant, String> posHomographs;

    private Dictionary(Map<String, String> map, Map<POSVariant, String> posHomographs) {
        this.map = map;
        this.posHomographs = posHomographs;
    }

    public static Dictionary empty() {
        return new Dictionary(emptyMap(), emptyMap());
    }


    public static Dictionary fromMap(Map<String, String> map) {
        return new Dictionary(map, emptyMap());
    }

    public static Dictionary fromFile(Path path) throws IOException {
        return fromList(Files.readAllLines(path, StandardCharsets.UTF_8));
    }

    public static Dictionary fromList(List<String> entries) {
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
        return new Dictionary(map, posHomographs);
    }
    
    public boolean containsWord(String word) {
        return map.containsKey(word);
    }

    public Set<String> words() {
        return map.keySet();
    }

    public Optional<String> get(String word, Pos pos) {
        POSVariant v = new POSVariant(pos, word);
        String specialised = posHomographs.get(v);
        if (specialised != null) {
            return Optional.of(specialised);
        }

        return Optional.ofNullable(map.get(word));
    }

    public Dictionary differences(Dictionary other) {
        Map<String, String> differences = new HashMap<>();
        for (var entry : other.map.entrySet()) {
            String ours = map.get(entry.getKey());
            if (ours == null || !ours.equals(entry.getValue())) {
                differences.put(entry.getKey(), entry.getValue());
            }
        }

        return new Dictionary(differences, emptyMap());
    }

    public static final class POSVariant {
        private final Pos pos;
        private final String word;

        public POSVariant(Pos pos, String word) {
            this.pos = pos;
            this.word = word;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof POSVariant)) return false;
            POSVariant that = (POSVariant) o;
            return Objects.equals(pos, that.pos) && Objects.equals(word, that.word);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos, word);
        }
    }

    @Override
    public String toString() {
        return "Dictionary{" +
                "map=" + map.size() +
                ", posHomographs=" + posHomographs.size() +
                '}';
    }
}

