package org.pitest.voices.g2p.core;

import org.pitest.voices.Language;
import org.pitest.voices.g2p.core.pos.Pos;
import org.pitest.voices.g2p.core.tracing.Trace;
import org.pitest.voices.g2p.core.syllables.RulesSyllabiliser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.pitest.voices.g2p.util.English.CONSONANTS;
import static org.pitest.voices.g2p.util.English.VOWELS;

/**
 * Phonemization model for English. Largely uses a dictionary, but falls back to a rules
 * based model when there is a dictionary miss. Rules generally produce
 * poor results, so a large dictionary is necessary.
 *
 * Logic is based on the MIT licensed typescript project at https://github.com/hans00/phonemize.
 *
 */
public class EnglishModel implements G2PModel {

    private static final List<SuffixRule> SUFFIX_RULES = Arrays.asList(
            // [pattern, IPA, attracts_stress]
            new SuffixRule("^tion$", "ʃən", false),        // -tion is always unstressed
            new SuffixRule("^sion$", "ʃən", false),    //* ʒən    // -sion is always unstressed
            new SuffixRule("^cial$", "ʃəl", false),        // -cial (commercial, social)
            new SuffixRule("^tial$", "ʃəl", false),        // -tial (potential, partial)
            new SuffixRule("^ture$", "tʃɚ", false),        // -ture (future, nature)
            new SuffixRule("^sure$", "ʒɚ", false),         // -sure (measure, pleasure)
            new SuffixRule("^geous$", "dʒəs", false),      // -geous (gorgeous, advantageous)
            new SuffixRule("^cious$", "ʃəs", false),       // -cious (delicious, precious)
            new SuffixRule("^tious$", "ʃəs", false),       // -tious (ambitious, nutritious)
            new SuffixRule("^eous$", "iəs", false),        // -eous (aneous, miscellaneous)
            new SuffixRule("^ous$", "əs", false),          // -ous (famous, nervous)

            new SuffixRule("^ious$", "iəs", false),        // -ious (various, serious)
            new SuffixRule("^uous$", "juəs", false),       // -uous (continuous, ambiguous)
            new SuffixRule("^able$", "əbəl", false),       // -able
            new SuffixRule("^ible$", "əbəl", false),       // -ible

            new SuffixRule("^ance$", "əns", false),        // -ance (dominance, balance)
            new SuffixRule("^ence$", "əns", false),        // -ence (presence, silence)
            new SuffixRule("^ness$", "nəs", false),        // -ness
            new SuffixRule("^ment$", "mənt", false),       // -ment

            new SuffixRule("^less$", "ləs", false),        // -less
            new SuffixRule("^ful$", "fəl", false),         // -ful
            new SuffixRule("^ly$", "li", false),           // -ly
            new SuffixRule("^er$", "ɚ", false),            // -er (comparative, agentive)

            new SuffixRule("^ers$", "ɚz", false),          // -ers (plural of -er)
            new SuffixRule("^est$", "əst", false),         // -est (superlative)
            new SuffixRule("^ing$", "ɪŋ", false),          // -ing
            new SuffixRule("^ed$", "d", false),            // -ed (past tense base)
            new SuffixRule("^es$", "z", false),            // -es (plural/3rd person)
            new SuffixRule("^s$", "z", false),             // -s (plural/3rd person)
            new SuffixRule("^age$", "ɪdʒ", false),         // -age (package, marriage)
            new SuffixRule("^ive$", "ɪv", false),          // -ive (active, passive)
            new SuffixRule("^ism$", "ɪzəm", false),        // -ism
            new SuffixRule("^ist$", "ɪst", false),         // -ist
            new SuffixRule("^ity$", "əti", false),         // -ity
            new SuffixRule("^al$", "əl", false),           // -al (normal, final)
            new SuffixRule("^ic$", "ɪk", true),            // -ic attracts stress (economic, systemic)
            new SuffixRule("^ics$", "ɪks", true),          // -ics attracts stress (mathematics, politics)
            new SuffixRule("^lity$", "ləti", false),       // -lity (quality, reality)
            new SuffixRule("^ity$", "əti", false),         // -ity (other cases)
            new SuffixRule("^ty$", "ti", false),           // -ty (empty, sixty)
            new SuffixRule("^ary$", "ɛri", false),         // -ary (library, military)
            new SuffixRule("^ory$", "ɔri", false),         // -ory (history, category)
            new SuffixRule("^ery$", "ɛri", false),         // -ery (bakery, gallery)
            new SuffixRule("^ry$", "ɹɪ", false),           // ** piper edit ri -ry (hungry, angry)
            new SuffixRule("^y$", "i", false),             // -y
            new SuffixRule("^le$", "əl", false),         // -le (simple, table)

            new SuffixRule("^re$", "ɔ:", false)      ,      // ** piper addition
            new SuffixRule("^lo$", "əʊ", false)            // ** piper addition
    );

    private static final List<PhonemeRule> PHONEME_RULES = Arrays.asList(
            // Silent letter combinations
            new PhonemeRule("^pn", "n"),                   // pneumonia, pneumatic
            new PhonemeRule("^ps", "s"),                   // psychology, psalm
            new PhonemeRule("^pt", "t"),                   // pterodactyl, ptomaine
            new PhonemeRule("^kn", "n"),                   // knee, knife, know
            new PhonemeRule("^gn", "n"),                   // gnome, gnat, gnu
            new PhonemeRule("^wr", "ɹ"),                   // write, wrong, wrist
            new PhonemeRule("^mb$", "m"),                  // thumb, lamb, comb (word-final)
            new PhonemeRule("^ght", "t"),                  // right, might, fight
            new PhonemeRule("^gh$", ""),                   // silent gh at word end (though, bough)
            new PhonemeRule("^gh", "ɡ"),                   // ghost, ghetto (at start)
            new PhonemeRule("^lm", "m"),                   // palm, calm, psalm

            // Improved digraph handling
            new PhonemeRule("^tsch", "tʃ"),                // German loanwords
            new PhonemeRule("^sch", "sk"),                 // schema, schematic (not German)
            new PhonemeRule("^she", "ʃi"),                 // she (irregular vowel)
            new PhonemeRule("^he", "hə"),  // * hi                // he (irregular vowel)
            new PhonemeRule("^ch", "tʃ"),                  // chair, church, much
            new PhonemeRule("^ck", "k"),                   // back, pick, truck
            new PhonemeRule("^ggi", "ɡi"),                 // double g before i (buggie) - prevent soft g
            new PhonemeRule("^gge", "ɡe"),                 // double g before e (trigger) - prevent soft g
            new PhonemeRule("^ggy", "ɡi"),                 // double g before y (muggy) - prevent soft g
            new PhonemeRule("^gg", "ɡ"),                   // double g -> single g (buggy, trigger)
            new PhonemeRule("^dg", "dʒ"),                  // bridge, judge, edge
            new PhonemeRule("^ph", "f"),                   // phone, graph, elephant
            new PhonemeRule("^sh", "ʃ"),                   // shoe, fish, wash
            new PhonemeRule("^thr", "θɹ"),                 // th + r cluster is always voiceless: through, three
            new PhonemeRule("^th(?=ink)", "θ"),            // voiceless: think, thinking
            new PhonemeRule("^th(?=ing$)", "θ"),           // voiceless: thing (complete word)
            new PhonemeRule("^th(?=ick)", "θ"),            // voiceless: thick, thicker
            new PhonemeRule("^th(?=orn)", "θ"),            // voiceless: thorn, thorny
            new PhonemeRule("^th(?=rough)", "θ"),          // voiceless: through (already handled above)
            new PhonemeRule("^the", "ðə"),                 // the (definite article)
            new PhonemeRule("^th(?=[aeiou])", "ð"),        // voiced before vowels: this, that, they
            new PhonemeRule("^th", "θ"),                   // voiceless (default): path, math
            new PhonemeRule("^tch", "tʃ"),                 // watch, match, catch
            new PhonemeRule("^wh", "w"),                   // what, where, when
            new PhonemeRule("^qu", "kw"),                  // queen, quick, quote
            new PhonemeRule("^ng", "ŋ"),                   // sing, ring, king

            // Improved vowel teams with better quality distinctions
            new PhonemeRule("^oo", "uː"),                  // boot, moon, cool, moose (long u)
            new PhonemeRule("^ou", "aʊ"),                  // house, about, cloud
            new PhonemeRule("^ow(?=[snmk])", "aʊ"),        // cow, down, brown (before consonants)
            new PhonemeRule("^ow", "oʊ"),                  // show, blow, know (at word end typically)
            new PhonemeRule("^oy", "ɔɪ"),                  // boy, toy, joy
            new PhonemeRule("^oi", "ɔɪ"),                  // coin, join, voice
            new PhonemeRule("^au", "ɔ"),                   // caught, sauce, because
            new PhonemeRule("^aw", "ɔ"),                   // saw, law, draw
            new PhonemeRule("^ay", "eɪ"),                  // day, say, way
            new PhonemeRule("^ai", "eɪ"),                  // rain, main, paid
            new PhonemeRule("^ea", "i"),                   // read, seat, beat (default long)
            new PhonemeRule("^ee", "i"),                   // see, tree, free
            new PhonemeRule("^ie", "i"),                   // piece, field, believe
            new PhonemeRule("^ei", "eɪ"),                  // vein, weight, eight
            new PhonemeRule("^ey", "eɪ"),                  // they, grey, key (at end)
            new PhonemeRule("^ight", "aɪt"),               // night, right, knight (i+ght)
            new PhonemeRule("^oa", "oʊ"),                  // boat, coat, road
            new PhonemeRule("^ross", "ɹoʊs"),              // gross -> groʊs
            new PhonemeRule("^oss", "ɔs"),                 // cross, loss (short o)
            new PhonemeRule("^eu", "ju"),                  // feud, neuter, Europe
            new PhonemeRule("^ew", "u"),                   // few, new, threw
            new PhonemeRule("^ue", "u"),                   // true, blue, glue (at end)
            new PhonemeRule("^ui", "u"),                   // fruit, suit, cruise

            // R-controlled vowels (rhotic)
            new PhonemeRule("^arr", "æɹ"),                 // carry, marry, arrow
            new PhonemeRule("^ar", "ɑɹ"),                  // car, far, start
            new PhonemeRule("^er", "ɚ"),                   // her, term, serve (use ɚ for unstressed)
            new PhonemeRule("^ir", "ɜ"),                   // bird, first, girl **(piper edit)
            new PhonemeRule("^or", "ɔɹ"),                  // for, port, storm
            new PhonemeRule("^ur", "ɜ"),                   // fur, turn, hurt **(piper edit)
            new PhonemeRule("^ear", "ɪɹ"),                 // hear, clear, year
            new PhonemeRule("^eer", "ɪɹ"),                 // deer, cheer, peer
            new PhonemeRule("^ier", "ɪɹ"),                 // pier, tier
            new PhonemeRule("^our", "aʊɹ"),                // hour, sour, flour
            new PhonemeRule("^air", "ɛɹ"),                 // hair, fair, chair
            new PhonemeRule("^are", "ɛɹ"),                 // care, share, prepare

            // Context-dependent consonants
            new PhonemeRule("^c(?=[eiy])", "s"),           // soft c: cent, city, cycle
            new PhonemeRule("^g(?=[eiy])", "dʒ"),          // soft g: gem, gin, gym (but not all cases)
            new PhonemeRule("^s(?=[eiy])", "s"),           // s before front vowels usually stays /s/

            // Improved consonant clusters
            new PhonemeRule("^spr", "spɹ"),                // spring, spray, spread
            new PhonemeRule("^str", "stɹ"),                // string, street, strong
            new PhonemeRule("^scr", "skɹ"),                // screen, script, scratch
            new PhonemeRule("^spl", "spl"),                // split, splash, splice
            new PhonemeRule("^squ", "skw"),                // square, squash, squeeze
            new PhonemeRule("^shr", "ʃɹ"),                 // shrimp, shrink, shrewd
            new PhonemeRule("^bl", "bl"),                  // blue, black, blow
            new PhonemeRule("^br", "bɹ"),                  // brown, bring, bread
            new PhonemeRule("^cl", "kl"),                  // clean, close, class
            new PhonemeRule("^cr", "kɹ"),                  // create, cross, cream
            new PhonemeRule("^dr", "dɹ"),                  // drive, dream, drop
            new PhonemeRule("^fl", "fl"),                  // fly, floor, flower
            new PhonemeRule("^fr", "fɹ"),                  // from, free, friend
            new PhonemeRule("^gl", "ɡl"),                  // glass, globe, glad
            new PhonemeRule("^gr", "ɡɹ"),                  // green, great, group
            new PhonemeRule("^pl", "pl"),                  // place, play, please
            new PhonemeRule("^pr", "pɹ"),                  // problem, provide, pretty
            new PhonemeRule("^sl", "sl"),                  // slow, sleep, slide
            new PhonemeRule("^sm", "sm"),                  // small, smile, smell
            new PhonemeRule("^sn", "sn"),                  // snow, snake, snack
            new PhonemeRule("^sp", "sp"),                  // speak, space, sport
            new PhonemeRule("^st", "st"),                  // start, stop, study
            new PhonemeRule("^sw", "sw"),                  // sweet, swim, switch
            new PhonemeRule("^two", "tu"),                 // two (special case)
            new PhonemeRule("^tr", "tɹ"),                  // tree, try, travel
            new PhonemeRule("^tw", "tw"),                  // twelve, twenty

            // Basic consonants
            new PhonemeRule("^b", "b"),
            new PhonemeRule("^c", "k"),                    // hard c (default)
            new PhonemeRule("^d", "d"),
            new PhonemeRule("^f", "f"),
            new PhonemeRule("^g", "ɡ"),                    // hard g (default)
            new PhonemeRule("^h", "h"),
            new PhonemeRule("^j", "dʒ"),
            new PhonemeRule("^k", "k"),
            new PhonemeRule("^l", "l"),
            new PhonemeRule("^m", "m"),
            new PhonemeRule("^n", "n"),
            new PhonemeRule("^p", "p"),
            new PhonemeRule("^r", "ɹ"),                    // American English rhotic r
            new PhonemeRule("^s", "s"),
            new PhonemeRule("^t", "t"),
            new PhonemeRule("^v", "v"),
            new PhonemeRule("^w", "w"),
            new PhonemeRule("^x", "ks"),                   // tax, fix, mix
            new PhonemeRule("^y(?=[aeiou])", "j"),         // yes, you, year (consonantal before vowels)
            new PhonemeRule("^y", "aɪ"),                   // by, my, try (vowel in other positions)
            new PhonemeRule("^z", "z"),

            // Default vowels (short/lax in closed syllables)
            new PhonemeRule("^a", "æ"),                    // cat, hat, bad
            new PhonemeRule("^e", "ɛ"),                   // bed, red, get (but she -> ʃi handled above)
            new PhonemeRule("^i", "ɪ"),                    // sit, hit, big
            new PhonemeRule("^o", "ɒ"),                    // cot, hot, dog  **piper
            new PhonemeRule("^u", "ʌ")                    // cut, but, run

    );

    private final WordToSyllables syllabify;
    private final Dictionary dictionary;

    public EnglishModel(Dictionary dictionary) {
        this(dictionary, new RulesSyllabiliser());
    }

    public EnglishModel(Dictionary dictionary, WordToSyllables syllabify) {
        this.syllabify = syllabify;
        this.dictionary = dictionary;
    }

    @Override
    public String predict(Trace trace, Language unused, String word, Pos pos) {
        Trace t = trace.start(word, pos);
        String result = predictInternal(t, word, pos);
        t.result(result);
        return result;
    }

    private String predictInternal(Trace t, String word, Pos pos) {
        String lowerWord = word.toLowerCase();

        // Hyphenated compounds (e.g., "recession-hit")
        if (lowerWord.contains("-")) {
            String cleanPart1 = handleHyphens(t, pos, lowerWord);
            if (cleanPart1 != null) return cleanPart1;
        }


        // Direct lookups (Dictionary, Homographs) - check known words first
        String knownPronunciation = wellKnown(t, lowerWord, pos, true); // Skip morphology here to avoid re-running
        if (knownPronunciation != null) {
            return knownPronunciation;
        }

        //  Morphological analysis - only for unknown words
        String morphPron = tryMorphologicalAnalysis(t, pos, lowerWord);
        if (morphPron != null) {
            return morphPron;
        }

        // Attempt to decompose the word into known dictionary parts
        String pronunciations = attemptDecomposition(t, lowerWord);
        if (pronunciations != null) return pronunciations;

        // Handle acronyms with or without periods, e.g., "TTS" or "M.L."
        String cleanProns = handleAcronyms(t, word);
        if (cleanProns != null) return cleanProns;

        // rule-based G2P
        List<String> syllables = syllabify.toSyllables(lowerWord);
        t.syllables(syllables);
        int stressedSyllableIndex = assignStress(syllables, lowerWord);

        List<String> syllableIPA = new ArrayList<>();
        for (int i = 0; i < syllables.size(); i++) {
            boolean isStressed = i == stressedSyllableIndex;
            boolean isLastSyllable = i == syllables.size() - 1;
            syllableIPA.add(syllableToIPA(t, syllables.get(i), i, isStressed, isLastSyllable));
        }

        if (!syllableIPA.isEmpty()) {
            String result = String.join("", syllableIPA);

            // Add stress marker
            if (syllables.size() > 1 && stressedSyllableIndex >= 0) {
                // Insert primary stress marker before the stressed syllable
                // **piper** Insert primary stress marker **after** the stressed syllable with <=????
                int charIndex = 0;
             //   for (int i = 0; i < stressedSyllableIndex; i++) {
                for (int i = 0; i <= stressedSyllableIndex; i++) {
                    charIndex += syllableIPA.get(i).length();
                }
                result = result.substring(0, charIndex) + "ˈ" + result.substring(charIndex);
            }

            return result;
        }

        // we failed, just return the word
        return lowerWord;
    }

    private String handleAcronyms(Trace t, String word) {
        if (word.matches("^([A-Z]\\.?){2,8}$")) {
            boolean containsPeriods = word.contains(".");
            String[] letters = word.replaceAll("\\.", "").split("");
            List<String> letterPronunciations = new ArrayList<>();
            boolean allValid = true;

            for (String letter : letters) {
                String pron = wellKnown(t, letter);
                if (pron != null) {
                    letterPronunciations.add(pron);
                } else {
                    allValid = false;
                    break;
                }
            }

            if (allValid) {
                if (containsPeriods) {
                    // No stress for acronyms with periods like M.L.
                    List<String> cleanProns = new ArrayList<>();
                    for (String pron : letterPronunciations) {
                        cleanProns.add(pron.replaceAll("ˈ", ""));
                    }
                    return String.join("", cleanProns);
                } else {
                    // Add stress for acronyms without periods like TTS
                    List<String> stressedProns = new ArrayList<>();
                    for (String pron : letterPronunciations) {
                        stressedProns.add("ˈ" + pron.replaceAll("ˈ", ""));
                    }
                    return String.join("", stressedProns);
                }
            }
        }
        return null;
    }

    private String attemptDecomposition(Trace t, String lowerWord) {
        List<String> decomposition = tryDecomposition(lowerWord);
        if (decomposition != null && decomposition.size() > 1) {
            List<String> pronunciations = new ArrayList<>();
            boolean allValid = true;

            for (String part : decomposition) {
                String pron = wellKnown(t, part);
                if (pron != null) {
                    pronunciations.add(pron.replaceAll("ˈ", ""));
                } else {
                    allValid = false;
                    break;
                }
            }

            if (allValid) {
                // Re-add stress markers between parts
                return "ˈ" + String.join("ˈ", pronunciations);
            }
        }
        return null;
    }

    private String handleHyphens(Trace t, Pos pos, String lowerWord) {
        String[] parts = lowerWord.split("-");
        if (parts.length == 2) {
            String part1 = predict(t, Language.en_GB, parts[0], pos);
            String part2 = predict(t, Language.en_GB, parts[1], pos);
            if (part1 != null && part2 != null) {
                // Remove stress from first part, add to second part for compound stress pattern
                String cleanPart1 = part1.replaceAll("ˈ", "");
                String cleanPart2 = part2.replaceAll("ˈ", "");
                return cleanPart1 + "ˈ" + cleanPart2;
            }
        }
        return null;
    }

    private List<String> tryDecomposition(String word) {
        if (word.length() < 8) return null; // Only try decomposition for reasonably long words

        // DP approach to find a valid decomposition into dictionary words.
        @SuppressWarnings("unchecked")
        List<String>[] dp = new List[word.length() + 1];
        dp[0] = new ArrayList<>();

        for (int i = 1; i <= word.length(); i++) {
            for (int j = 0; j < i; j++) {
                // Prioritize longer chunks
                String chunk = word.substring(j, i);
                if (dp[j] != null && dictionary.containsWord(chunk)) {
                    List<String> newDecomposition = new ArrayList<>(dp[j]);
                    newDecomposition.add(chunk);

                    // Prefer decompositions with fewer (longer) words.
                    if (dp[i] == null || newDecomposition.size() < dp[i].size()) {
                        dp[i] = newDecomposition;
                    }
                }
            }
        }

        return dp[word.length()];
    }

    private String wellKnown(Trace t, String word) {
        return wellKnown(t, word, null, false);
    }

    private String wellKnown(Trace t, String word, Pos pos, boolean skipMorphology) {
        Optional<String> known = dictionary.get(word, pos);
        if (known.isPresent()) {
            t.dictionaryHit(word, pos, known.get());
            return known.get();
        }

        if (skipMorphology) {
            return null;
        }

        // Morphological analysis for common endings
        return tryMorphologicalAnalysis(t, pos, word);
    }

    private String tryMorphologicalAnalysis(Trace t, Pos pos, String word) {
             // Try plural forms (-s, -es)
        if (word.endsWith("s") && !word.endsWith("ss") && word.length() > 2) {
            String singular = word.substring(0, word.length() - 1);
            String basePron = wellKnown(t, singular, pos, false);
            if (basePron != null) {
                t.morphology(basePron);
                String lastSound = basePron.substring(basePron.length() - 1);
                if (Arrays.asList("s", "z", "ʃ", "ʒ", "tʃ", "dʒ").contains(lastSound)) {
                    return basePron + "ɪz";
                }
                if (Arrays.asList("p", "t", "k", "f", "θ").contains(lastSound)) {
                    return basePron + "s";
                }
                return basePron + "z";
            }
        }

        // Try possessive forms ('s)
        if (word.endsWith("'s") && word.length() > 3) {
            String base = word.substring(0, word.length() - 2);
            String basePron = wellKnown(t, base);
            if (basePron != null) {
                t.morphology(basePron);
                String lastSound = basePron.substring(basePron.length() - 1);
                if (Arrays.asList("s", "z", "ʃ", "ʒ", "tʃ", "dʒ").contains(lastSound)) {
                    return basePron + "ɪz";
                }
                if (Arrays.asList("p", "t", "k", "f", "θ").contains(lastSound)) {
                    return basePron + "s";
                }
                return basePron + "z";
            }
        }

        // Try -es plural
        if (word.endsWith("es") && word.length() > 3) {
            String singular = word.substring(0, word.length() - 2);
            String basePron = wellKnown(t, singular, pos, false);
            if (basePron != null) {
                t.morphology(basePron);
                return basePron + "ɪz";
            }
        }

        // Try past tense (-ed)
        if (word.endsWith("ed") && word.length() > 3) {
            String base = word.substring(0, word.length() - 2);
            String basePron = wellKnown(t, base);
            if (basePron != null) {
                t.morphology(basePron);
                String lastSound = basePron.substring(basePron.length() - 1);
                if (Arrays.asList("t", "d").contains(lastSound)) {
                    return basePron + "ɪd";
                }
                if (Arrays.asList("p", "k", "s", "ʃ", "tʃ", "f", "θ").contains(lastSound)) {
                    return basePron + "t";
                }
                return basePron + "d";
            }
        }

        // Try present participle (-ing)
        if (word.endsWith("ing") && word.length() > 4) {
            String base = word.substring(0, word.length() - 3);
            String basePron = wellKnown(t, base);
            if (basePron != null) {
                t.morphology(basePron);
                return basePron + "ɪŋ";
            }
            // Handle cases like "running" -> "run"
            if (word.length() > 4) {
                String baseShort = word.substring(0, word.length() - 4);
                if (word.charAt(word.length() - 4) == baseShort.charAt(baseShort.length() - 1)) {
                    String basePronShort = wellKnown(t, baseShort);
                    if (basePronShort != null) {
                        t.morphology(basePron);
                        return basePronShort + "ɪŋ";
                    }
                }
            }
        }

        // Try -ally / -ly adverbs
        if (word.endsWith("ally") && word.length() > 4) {
            // e.g., globally -> global
            String base = word.substring(0, word.length() - 2);
            // Try to get pronunciation of the base word, either from dictionary or by recursive prediction.
            String basePron = wellKnown(t, base, pos, true);
            if (basePron == null) {
                basePron = predict(t, Language.en_GB, base, null);
            }
            if (basePron != null) {
                t.morphology(basePron);
                // basePron for global is ˈɡloʊbəl. Just add 'i'
                return basePron.replaceAll("ə$", "") + "əli";
            }
        }

        if (word.endsWith("ly") && !word.endsWith("ally") && word.length() > 2) {
            // e.g., "quickly" -> "quick"
            String base = word.substring(0, word.length() - 2);
            String basePron = wellKnown(t, base, pos, true);
            if (basePron == null) {
                basePron = predict(t, Language.en_GB, base, null);
            }
            if (basePron != null) {
                t.morphology(basePron);
                return basePron + "li";
            }
        }

        // Try -able suffix
        if (word.endsWith("able") && word.length() > 5) {
            String base = word.substring(0, word.length() - 4);
            String basePron = wellKnown(t, base, pos, true);
            if (basePron == null) {
                basePron = predict(t, Language.en_GB, base, null);
            }
            if (basePron != null) {
                t.morphology(basePron);
                return basePron.replaceAll("ə$", "") + "əbəl";
            }

            base = word.substring(0, word.length() - 3);
            basePron = wellKnown(t, base, pos, true);
            if (basePron == null) {
                basePron = predict(t, Language.en_GB, base, null);
            }
            if (basePron != null) {
                t.morphology(basePron);
                return basePron + "əbəl";
            }
        }

        // Try -logy suffix
        if (word.endsWith("logy") && word.length() > 4) {
            String base = word.substring(0, word.length() - 4);
            String basePron = wellKnown(t, base, pos, true);
            if (basePron == null) {
                basePron = predict(t, Language.en_GB, base, pos);
            }
            if (basePron != null) {
                t.morphology(basePron);
                return basePron.replaceAll("ə$", "") + "lədʒi";
            }
        }

        return null;
    }

    // Improved stress assignment based on morphological and phonological rules
    private int assignStress(List<String> syllables, String word) {
        if (syllables.size() <= 1){
            return 0;
        }

        // Check for stress-attracting suffixes (stress BEFORE the suffix)
        for (SuffixRule rule : SUFFIX_RULES) {
            if (rule.attractsStress && rule.matches(word)) {
                return Math.max(0, syllables.size() - 2);
            }
        }

        // Specific suffix stress patterns
        if (word.endsWith("tion") || word.endsWith("sion") ||
                word.endsWith("cial") || word.endsWith("tial")) {
            return Math.max(0, syllables.size() - 2);
        }

        // -ance/-ence words typically stress the antepenult (like dominance -> dəˈmɪnəns)
        if ((word.endsWith("ance") || word.endsWith("ence")) && syllables.size() >= 3) {
            return 1; // Usually second syllable for these patterns
        }

        if (word.endsWith("ic") && syllables.size() > 1) {
            return Math.max(0, syllables.size() - 2);
        }

        // Common prefixes that don't usually take stress
        List<String> unstressedPrefixes = Arrays.asList("un", "re", "pre", "dis", "mis", "over", "under", "out");
        for (String prefix : unstressedPrefixes) {
            if (word.startsWith(prefix) && syllables.size() > 2) {
                return 1; // Stress usually falls on the root, not the prefix
            }
        }

        // For 2-syllable words, generally stress the first syllable unless it's a weak prefix
        if (syllables.size() == 2) {
            // Check for weak prefixes
            for (String prefix : Arrays.asList("be", "de", "re", "un", "in", "ex", "pre")) {
                if (word.startsWith(prefix)) {
                    return 1; // Stress the second syllable
                }
            }
            return 0; // Default: stress first syllable
        }

        // For 3+ syllables, use improved stress assignment
        if (syllables.size() >= 3) {
            // Check for compound words (typically have primary stress on first part)
            if (isLikelyCompound(word, syllables)) {
                return 0; // First syllable gets primary stress in compounds
            }

            String penult = syllables.get(syllables.size() - 2);
            if (isSyllableHeavy(penult)) {
                return syllables.size() - 2; // Stress the penult if heavy
            } else {
                return Math.max(0, syllables.size() - 3); // Stress the antepenult if penult is light
            }
        }

        return 0; // Default fallback
    }

    private boolean isSyllableHeavy(String syllable) {
        // A syllable is heavy if it has:
        // 1. A long vowel (vowel digraph)
        // 2. A vowel followed by two or more consonants
        // 3. Ends in a consonant (closed syllable)

        List<String> vowelDigraphs = Arrays.asList(
                "aa", "ai", "au", "aw", "ay", "ea", "ee", "ei", "eu", "ey",
                "ie", "oa", "oo", "ou", "ow", "oy", "ue", "ui"
        );

        for (String digraph : vowelDigraphs) {
            if (syllable.contains(digraph)) return true;
        }

        // Count vowels and consonants after the vowel
        boolean vowelFound = false;
        int consonantCount = 0;

        for (char c : syllable.toCharArray()) {
            String charStr = String.valueOf(c);
            if (VOWELS.contains(charStr)) {
                vowelFound = true;
                consonantCount = 0; // Reset consonant count after vowel
            } else if (vowelFound && CONSONANTS.contains(charStr)) {
                consonantCount++;
            }
        }

        return consonantCount >= 1; // Closed syllable
    }

    private boolean isLikelyCompound(String word, List<String> syllables) {
        // Detect potential compound words based on patterns
        if (syllables.size() < 2) return false;

        // Common compound patterns
        List<Pattern> compoundPatterns = Arrays.asList(
                Pattern.compile("\\w{4,}wide$"),    // worldwide, nationwide
                Pattern.compile("\\w{3,}land$"),    // homeland, woodland
                Pattern.compile("\\w{3,}work$"),    // homework, network
                Pattern.compile("\\w{3,}time$"),    // sometime, longtime
                Pattern.compile("\\w{3,}way$"),     // highway, railway
                Pattern.compile("\\w{3,}ward$"),    // forward, backward
                Pattern.compile("hundred"),         // hundred (often in compounds)
                Pattern.compile("\\w{3,}side$"),    // outside, inside
                Pattern.compile("\\w{3,}where$")    // somewhere, anywhere
        );

        for (Pattern pattern : compoundPatterns) {
            if (pattern.matcher(word).find()) {
                return true;
            }
        }

        return false;
    }

    // Enhanced syllable to IPA conversion with stress-sensitive vowel reduction
    private String syllableToIPA(Trace t, String syllable, int syllableIndex, boolean isStressed, boolean isLastSyllable) {

        List<String> phonemes = new ArrayList<>();
        String remaining = syllable;

        // Check for suffix rules first
        for (SuffixRule rule : SUFFIX_RULES) {
            if (rule.matches(remaining)) {
                return rule.ipa;
            }
        }

        // Handle doubled consonants
        remaining = remaining.replaceAll("([b-df-hj-np-tv-z])\\1", "$1");

        // Silent 'e' detection (but exclude common function words like "the")
        boolean endsWithSilentE = isLastSyllable && syllable.length() > 1 && syllable.endsWith("e") &&
                !syllable.endsWith("ee") && !syllable.endsWith("le") && !syllable.endsWith("he") &&
                !syllable.endsWith("tte") && !syllable.endsWith("ght") && !syllable.endsWith("se") &&
                CONSONANTS.contains(String.valueOf(syllable.charAt(syllable.length() - 2)));

        if (endsWithSilentE) {
            remaining = syllable.substring(0, syllable.length() - 1);
        }

        // Apply phoneme rules
        while (!remaining.isEmpty()) {
            boolean matchFound = false;
            for (PhonemeRule rule : PHONEME_RULES) {
                Pattern pattern = Pattern.compile(rule.pattern);
                Matcher matcher = pattern.matcher(remaining);
                if (matcher.find()) {
                    t.phonemeRule(rule.pattern, remaining);
                    phonemes.add(rule.ipa);
                    remaining = remaining.substring(matcher.group().length());
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound) {
                remaining = remaining.substring(1);
            }
        }

        // Apply conservative vowel modifications based on stress and position
        if (!isStressed && syllableIndex > 0 && !isLastSyllable) {
            // More conservative vowel reduction - only for clearly unstressed syllables
            for (int i = 0; i < phonemes.size(); i++) {
                Map<String, String> vowelReductions = new HashMap<>();
                vowelReductions.put("æ", "ə");   // cat -> ə in unstressed (but not in final syllables)
                vowelReductions.put("ɛ", "ə");   // bed -> ə in unstressed
                vowelReductions.put("ɪ", "ɪ");   // keep ɪ - common in unstressed syllables
                vowelReductions.put("ɑ", "ə");   // cot -> ə in unstressed
                vowelReductions.put("ʌ", "ə");   // cut -> ə in unstressed
                // Don't reduce diphthongs as aggressively
                vowelReductions.put("eɪ", "eɪ"); // keep in most cases
                vowelReductions.put("aɪ", "aɪ"); // keep in most cases
                vowelReductions.put("ɔɪ", "ɔɪ"); // keep in most cases
                vowelReductions.put("oʊ", "oʊ"); // keep in most cases
                vowelReductions.put("aʊ", "aʊ"); // keep in most cases

                if (vowelReductions.containsKey(phonemes.get(i))) {
                    phonemes.set(i, vowelReductions.get(phonemes.get(i)));
                }
            }
        }

        // Special handling for final unstressed syllables (less reduction)
        if (!isStressed && isLastSyllable && syllableIndex > 0) {
            for (int i = 0; i < phonemes.size(); i++) {
                Map<String, String> finalSyllableReductions = new HashMap<>();
                finalSyllableReductions.put("æ", "ə");   // cat -> ə
                finalSyllableReductions.put("ɛ", "ɪ");   // bed -> ɪ in final position (like "pocket")
                finalSyllableReductions.put("ɑ", "ə");   // cot -> ə
                finalSyllableReductions.put("ʌ", "ə");   // cut -> ə

                if (finalSyllableReductions.containsKey(phonemes.get(i))) {
                    phonemes.set(i, finalSyllableReductions.get(phonemes.get(i)));
                }
            }
        }

        // Magic 'e' rule for stressed syllables
        if (endsWithSilentE && isStressed && !phonemes.isEmpty()) {
            Map<String, String> shortToLong = new HashMap<>();
            shortToLong.put("æ", "eɪ");   // cap -> cape
            shortToLong.put("ɛ", "i");    // met -> mete
            shortToLong.put("ɪ", "aɪ");   // bit -> bite
            shortToLong.put("ɑ", "oʊ");   // hop -> hope
            shortToLong.put("ʌ", "ju");   // cut -> cute

            for (int i = phonemes.size() - 1; i >= 0; i--) {
                if (shortToLong.containsKey(phonemes.get(i))) {
                    phonemes.set(i, shortToLong.get(phonemes.get(i)));
                    break;
                }
            }
        }

        return String.join("", phonemes);
    }

    @Override
    public void close() throws Exception {
       // no op
    }
}

class PhonemeRule {
    final String pattern;
    final String ipa;

    PhonemeRule(String pattern, String ipa) {
        this.pattern = pattern;
        this.ipa = ipa;
    }

}

class SuffixRule {
    private final String pattern;
    final String ipa;
    final boolean attractsStress;

    SuffixRule(String pattern, String ipa, boolean attractsStress) {
        this.pattern = pattern;
        this.ipa = ipa;
        this.attractsStress = attractsStress;
    }

    boolean matches(String word) {
        return word.matches(pattern);
    }
}