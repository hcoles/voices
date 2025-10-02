package org.pitest.voices.g2p.core.tracing;

import org.pitest.voices.g2p.core.pos.Pos;

import java.util.ArrayList;
import java.util.List;

/**
 * Log trace of phonemization process to standard out
 */
public class LoggingTrace implements Trace {

    private final List<String> trace = new ArrayList<>();

    private final String word;
    private final int level;

    public LoggingTrace() {
        this(0, "");
    }

    public LoggingTrace(int level, String word) {
        this.level = level;
        this.word = word;
    }

    @Override
    public Trace start(String word, Pos pos) {
        return new LoggingTrace(level + 1, word);
    }

    @Override
    public void result(String phonemes) {
        System.out.println(word + " -> " + phonemes + " (" + String.join(",", trace) + ")");
    }

    @Override
    public void dictionaryHit(String word, Pos pos, String s) {
        trace.add(String.format("Dictionary %s (%s) %s ", word, pos, s));
    }

    @Override
    public void morphology(String base) {
        trace.add(String.format("Morphology %s ", word));
    }

    @Override
    public void syllables(List<String> syllables) {
        trace.add(String.format("Syllables %s ", String.join(";", syllables)));
    }

    @Override
    public void unknownPhoneme(String phoneme) {
        trace.add(String.format("! unknown phoneme %s ", phoneme));
    }

    @Override
    public void phonemeRule(String pattern, String remaining) {
        trace.add(String.format("Rule %s %s ", pattern, remaining));
    }


}
