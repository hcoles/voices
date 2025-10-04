package org.pitest.voices.g2p.core.dictionary;

import org.pitest.voices.g2p.core.Dictionary;
import org.pitest.voices.g2p.core.pos.Pos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyMap;
import static org.pitest.voices.g2p.core.dictionary.MapDictionary.fromList;

public class Dictionaries {
    public static Dictionary empty() {
        return new Dictionary() {
            @Override
            public boolean containsWord(String word) {
                return false;
            }

            @Override
            public Set<String> words() {
                return Set.of();
            }

            @Override
            public Optional<String> get(String word, Pos pos) {
                return Optional.empty();
            }
        };
    }

    public static Dictionary englishHomographs() {

        // homographs generally as pairs of verb form
        // and an untagged form to use by default.
        List<String> lines = new ArrayList<>();
        lines.add("abuse=ɐbjˈuːz|VB|VBP");
        lines.add("abuse=ɐbjˈuːs");

        lines.add("analyses=ɐnˈaləsˌiːz|NN");
        lines.add("analyses=ˈanɐlˌaɪzɪz");

        lines.add("appropriate=ɐpɹˈəʊpɹɪˌeɪt|VB|VBP");
        lines.add("appropriate=ɐpɹˈəʊpɹɪət");

        lines.add("appropriate=ɐpɹˈəʊpɹɪˌeɪt|VB|VBP");
        lines.add("appropriate=ɐpɹˈəʊpɹɪət");

        lines.add("articulate=ɑːtˈɪkjʊlɪt|JJ");
        lines.add("articulate=ɑːtˈɪkjʊlˌeɪt");

        lines.add("bow=bˌaʊ|VB|VBP");
        lines.add("bow=bˈəʊ");

        lines.add("bows=bˌaʊz|VB|VBP");
        lines.add("bows=bˈəʊz");

        lines.add("close=klˈəʊz|VB|VBP");
        lines.add("close=klˈəʊs");

        lines.add("desert=dˈəʊnt|VB|VBP");
        lines.add("desert=dˈɛzət");

        lines.add("dogged=dˈɒɡd|VB|VBP|VBD");
        lines.add("dogged=dˈɒɡɪd");

        lines.add("entrance=ɛntɹˈans|NN");
        lines.add("entrance=ˈɛntɹəns");

        lines.add("excuse=ɛkskjˈuːs|NN");
        lines.add("entrance=ɛkskjˈuːz");

        lines.add("house=hˈaʊz|VB");
        lines.add("house=hˈaʊs");

        lines.add("invalid=ɪnvəlɪd|NN");
        lines.add("invalid=ɪnvˈalɪd");

        lines.add("intimate=ˈɪntɪmət|JJ");
        lines.add("intimate=ˈɪntɪmeɪt");

        lines.add("lead=lˈɛd|NN"); // hand crafted, for once the verb is the default form
        lines.add("lead=lˈiːd");

        lines.add("learned=lˈɜːnɪd|JJ");
        lines.add("learned=lˈɜːnd");

        lines.add("live=lˈɪv|VB|VBP");
        lines.add("live=lˈaɪv");

        lines.add("lives=lˈɪvz|VB|VBP");
        lines.add("lives=lˈaɪvz");

        lines.add("moped=m'əʊpt|VBD|VBP");
        lines.add("moped=mˈəʊpɛd");

        lines.add("object=ɒbdʒˈɛkt|VBP|VBD");
        lines.add("object=ˈɒbdʒɛkt");

        lines.add("produce=pɹˈɒdjuːs|NN");
        lines.add("produce=pɹədjˈuːs");

        lines.add("read=ɹˈɛd|VBD"); // past tense only
        lines.add("read=ɹˈiːd");

        lines.add("record=ɹɪkˈɔːd|VB|VBP|VBD");
        lines.add("record=ɹˈɛkɔːd");

        lines.add("refuse=ɹˈɛfjuːs|NN");
        lines.add("refuse=ɹɪfjˈuːz");

        lines.add("rebel=ɹɪbˈɛl|VB|VBP");
        lines.add("rebel=ɹˈɛbəl");

        lines.add("rebels=ɹɪbˈɛlz|VB|VBP");
        lines.add("rebels=ɹˈɛbəlz");

        lines.add("separate=sˈɛpɹət|JJ");
        lines.add("separate=sˈɛpəɹˌeɪt");

        lines.add("sow=s'aʊ|NN");
        lines.add("sow=s'oʊ");

        lines.add("sows=s'aʊz|NN");
        lines.add("sows=s'oʊz");

        lines.add("tarry=tˈɑːɹi|JJ");
        lines.add("tarry=tˈaɹi");

        // noun form never seems to be detected but added anyway
        lines.add("tear=tˈiə|NN");
        lines.add("tear=tˈeə");

        lines.add("use=j'uːz|VB|VBP");
        lines.add("use=jˈuːs");

        lines.add("wind=wˈaɪnd|VBP|VB");
        lines.add("wind=wˈɪnd");

        lines.add("winds=wˈaɪndz|VBP|VB");
        lines.add("winds=wˈɪndz");

        lines.add("wound=wˈaʊnd|VBD"); // past tense only
        lines.add("wound=wˈuːnd");

        return fromList(lines);
    }

    public static Dictionary fromMap(Map<String, String> map) {
        return new MapDictionary(map, emptyMap());
    }

    public static Dictionary fromFile(Path path) throws IOException {
        return fromList(Files.readAllLines(path, StandardCharsets.UTF_8));
    }

}
