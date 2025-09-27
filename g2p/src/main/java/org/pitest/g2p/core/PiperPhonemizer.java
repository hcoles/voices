package org.pitest.g2p.core;

import opennlp.tools.postag.POSModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.pitest.g2p.core.pos.POSToken;
import org.pitest.g2p.core.pos.Pos;
import org.pitest.g2p.core.pos.SimplePOSTagger;
import org.pitest.g2p.core.tracing.Trace;
import org.pitest.g2p.util.Resource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PiperPhonemizer {

    private final static SimplePOSTagger tagger = makeTagger();

    private final G2PModel g2PModel;
    private final List<Expansion> expansions;
    private final Trace trace;

    public PiperPhonemizer(G2PModel g2PModel, List<Expansion> expansions, Trace trace) {
        this.g2PModel = g2PModel;
        this.expansions = expansions;
        this.trace = trace;
    }

    public List<String> toPhonemes(Language lang, String text) {
        Stream<String> wordPhonemes = phonemize(lang, text).stream()
                .flatMap(this::asChars);

        return Stream.concat(Stream.of("^"),
                        Stream.concat(wordPhonemes, Stream.of("$")))
                .collect(Collectors.toList());
    }


    /**
     * Tokenize a string into a list of phoneme strings
     * @param text Text to tokenize
     * @return List of phoneme, one for each word in the input text
     */
    public List<String> phonemize(Language lang, String text) {
        try {
            return processTokens(lang, text).stream()
                    .map(PhonemeToken::getPhoneme)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<PhonemeToken> processTokens(Language lang, String text) throws IOException {
        String expandedText = expandText(text);

        // Get tokens with or without positions
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(expandedText);
        List<POSToken> posResults = tagger.tagWords(tokens);

        List<PhonemeToken> results = new ArrayList<>();
        for (POSToken match : posResults) {
            String token = match.word();

            // preserve punctuation, but don't try to predict phonemes for it
            // if (isPunctuation(token)) {
            if (match.pos() == Pos.SYM) {
                results.add(new PhonemeToken(token, token));
                continue;
            }

            String phoneme = g2PModel.predict(trace, lang, token, match.pos());

            // reinsert spaces. makes it a little choppy
            // but prevents the occasional missing sounds on word endings.
            if (!results.isEmpty()) {
                results.add(new PhonemeToken(" ", " "));
            }

            results.add(new PhonemeToken(phoneme, token));
        }

        return results;
    }

    private static SimplePOSTagger makeTagger() {
        try (var s = Resource.readAsStream("/en-pos-maxent.bin")){
            POSModel posModel = new POSModel(s);
            return new SimplePOSTagger(posModel);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private String expandText(String text) {
        String expanded = expansions.stream()
                .reduce(text, (s, e) -> e.expand(s), (s1, s2) -> s2);
        return removeSmartQuotes(removeSApostrophe(expanded));
    }

    private String removeSApostrophe(String text) {
        return text.replace("s' ", "s ");
    }

    private static String removeSmartQuotes(String text) {
        return text.replaceAll("’", "'")
                .replaceAll("“", "\"")
                .replaceAll("”", "\"");
    }

    private Stream<String> asChars(String s) {
        return s.chars()
                .mapToObj(c -> "" + (char) c);
    }


    private static class PhonemeToken {

        private final String phoneme;
        private final String word;

        public PhonemeToken(String phoneme, String word) {
            this.phoneme = phoneme;
            this.word = word;
        }

        public String getPhoneme() {
            // make sure symbols pass through for further processing
            if (phoneme.isEmpty()) {
                return word;
            }
            return phoneme;
        }

        @Override
        public String toString() {
            return "PhonemeToken{" +
                    "phoneme='" + phoneme + '\'' +
                    ", word='" + word + '\'' +
                    '}';
        }
    }
}
