package org.pitest.g2p.core.syllables;

import org.pitest.g2p.core.WordToSyllables;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.pitest.g2p.util.English.CONSONANTS;
import static org.pitest.g2p.util.English.VOWELS;

/**
 * Converts words to syllables via a set of hard coded rules.
 */
public class RulesSyllabiliser implements WordToSyllables {

    // Valid English onsets (consonant clusters that can start a syllable)
    private static final Set<String> VALID_ONSETS = Set.of(
            "b", "bl", "br", "c", "ch", "cl", "cr", "d", "dr", "dw", "f", "fl", "fr", "g", "gl", "gr", "gu", "h", "j",
            "k", "kl", "kn", "kr", "l", "m", "n", "p", "ph", "pl", "pr", "ps", "qu", "r", "rh", "s", "sc", "sch", "scr",
            "sh", "sk", "sl", "sm", "sn", "sp", "sph", "spl", "spr", "st", "str", "sv", "sw", "t", "th", "thr", "tr",
            "ts", "tw", "v", "w", "wh", "wr", "x", "y", "z"
    );

    @Override
    public List<String> toSyllables(String word) {

        // A more linguistically informed syllabification algorithm based on Maximal Onset Principle.
        // This is a complex problem, and this implementation is a heuristic approach.

        // 0. Pre-handle exceptions and very short words
        if (word.length() <= 3) {
            return List.of(word);
        }

        char[] chars = word.toLowerCase().toCharArray();
        List<String> syllables = new ArrayList<>();
        StringBuilder currentSyllable = new StringBuilder();

        // 2. Iterate through the word, identifying vowel and consonant clusters.
        int i = 0;
        while (i < chars.length) {
            int i_before = i;
            // Find a vowel cluster (nucleus)
            StringBuilder nucleus = new StringBuilder();
            while (i < chars.length && VOWELS.contains(String.valueOf(chars[i]))) {
                nucleus.append(chars[i]);
                i++;
            }

            // Find the following consonant cluster (coda + next onset)
            StringBuilder consonants = new StringBuilder();
            while (i < chars.length && CONSONANTS.contains(String.valueOf(chars[i]))) {
                consonants.append(chars[i]);
                i++;
            }

            // If 'i' has not advanced, it means we hit a character that is neither
            // a vowel nor a consonant (like an apostrophe).
            if (i == i_before) {
                // Skip apostrophes and other non-alphabetic characters for syllabification
                if (chars[i] == '\'') {
                    // Just skip the apostrophe, don't add it to any syllable
                    i++;
                    continue;
                }
                // Append the character to the current syllable and advance the pointer.
                if (!syllables.isEmpty() && currentSyllable.length() == 0) {
                    syllables.set(syllables.size() - 1, syllables.get(syllables.size() - 1) + chars[i]);
                } else {
                    currentSyllable.append(chars[i]);
                }
                i++;
                continue;
            }

            if (nucleus.length() > 0) { // Found a vowel nucleus
                if (consonants.length() == 0) { // Word ends in a vowel
                    currentSyllable.append(nucleus);
                    syllables.add(currentSyllable.toString());
                    currentSyllable = new StringBuilder();
                } else if (consonants.length() == 1) { // VCV pattern, consonant starts next syllable
                    currentSyllable.append(nucleus);
                    syllables.add(currentSyllable.toString());
                    currentSyllable = new StringBuilder(consonants);
                } else { // VCCV, VCCCV, etc. patterns
                    int splitPoint = 0;
                    while (splitPoint < consonants.length()) {
                        String onsetCandidate = consonants.substring(splitPoint);
                        if (VALID_ONSETS.contains(onsetCandidate)) {
                            break;
                        }
                        splitPoint++;
                    }

                    String coda = consonants.substring(0, splitPoint);
                    String nextOnset = consonants.substring(splitPoint);

                    currentSyllable.append(nucleus).append(coda);
                    syllables.add(currentSyllable.toString());
                    currentSyllable = new StringBuilder(nextOnset);
                }
            } else { // Word starts with a consonant cluster
                currentSyllable.append(consonants);
            }
        }

        if (currentSyllable.length() > 0) {
            syllables.add(currentSyllable.toString());
        }

        // Post-processing: Merge any leftover single-consonant syllables into the previous one.
        // This can happen with words like "apple" -> ap-ple, where current logic might give a-p-ple
        for (int j = syllables.size() - 1; j > 0; j--) {
            boolean allConsonants = syllables.get(j).chars()
                    .allMatch(c -> CONSONANTS.contains(String.valueOf((char) c)));

            if (allConsonants) {
                syllables.set(j - 1, syllables.get(j - 1) + syllables.get(j));
                syllables.remove(j);
            }
        }

        return syllables.stream()
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.toList());
    }
}
