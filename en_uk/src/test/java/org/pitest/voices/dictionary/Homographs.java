package org.pitest.voices.dictionary;

import org.pitest.voices.Language;
import org.pitest.voices.g2p.core.Dictionary;
import org.pitest.voices.g2p.core.EnglishModel;
import org.pitest.voices.g2p.core.PiperPhonemizer;
import org.pitest.voices.g2p.core.dictionary.Dictionaries;
import org.pitest.voices.g2p.core.pos.Pos;
import org.pitest.voices.g2p.core.tracing.Trace;
import org.pitest.voices.ChorusConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;


/**
 * Generates espeak phonemes for heteronyms and figure out how opennlp tags them as POS in
 * common sentences. The POS tags don't necessarily make sense, so trial error and experimentation
 * is required.
 */
public class Homographs {
    public static void main(String[] args) throws IOException {
        Path baseDir = new ChorusConfig(Dictionaries.empty()).base();
        Dictionary noHomophones = Dictionaries.fromFile(baseDir.resolve("dictionary_gb.txt"));

        List<Homograph> phrases = new ArrayList<>();

        phrases.add(new Homograph("wound", "I wound it round.", "A wound."));

        phrases.add(new Homograph("wicked", "The oil wicked.", "You're wicked."));

        phrases.add(new Homograph("tear", "I tear the paper.", "a tear."));
        phrases.add(new Homograph("tarry", "Don't tarry.", "A tarry rag."));

        phrases.add(new Homograph("separate", "A separate cabin", "I separate the sheep"));

        phrases.add(new Homograph("reject", "A reject", "I reject that"));

        phrases.add(new Homograph("record", "A record", "I record the show"));

        phrases.add(new Homograph("produce", "I produce.", "Some produce."));

        phrases.add(new Homograph("object", "I object to that.", "An object"));

        phrases.add(new Homograph("minute", "One minute.", "A minute amount of gold."));

        phrases.add(new Homograph("learned", "I learned the rules", "The learned man."));

        phrases.add(new Homograph("intimate", "intimate clothing", "what did you intimate?"));
        phrases.add(new Homograph("house", "Your house", "We can house you"));

        phrases.add(new Homograph("excuse", "You make an excuse", "I excuse you."));

        phrases.add(new Homograph("dogged", "dogged determination", "I dogged your steps"));

        phrases.add(new Homograph("entrance", "The entrance.", "You entrance me"));

        phrases.add(new Homograph("bow", "I bow down", "A bow tie"));
        phrases.add(new Homograph("analyses", "He analyses.", "The analyses."));
        phrases.add(new Homograph("appropriate", "Not appropriate.", "I appropriate the building."));

        phrases.add(new Homograph("articulate", "He is articulate", "I articulate"));

        phrases.add(new Homograph("abuse", "That is abuse.", "Abuse the dog."));
        phrases.add(new Homograph("live", "Go live", "I live"));

        phrases.add(new Homograph("read", "I read books.", "What have you read today?"));
        phrases.add(new Homograph("advocate", "Advocate for it", "I am an Advocate"));
        phrases.add(new Homograph("close", "I asked you to close the door.", "how close is it?"));
        phrases.add(new Homograph("desert", "don't desert me", "it's a desert"));

        phrases.add(new Homograph("lead", "made out of lead", "lead me"));
        phrases.add(new Homograph("coordinate", "coordinate the trial", "my coordinate is "));
        phrases.add(new Homograph("wind", "did you wind it?", "the wind"));
        phrases.add(new Homograph("winds", "it winds me up", "the winds were strong"));


        phrases.add(new Homograph("bow", "bow down", "a bow"));
        phrases.add(new Homograph("use", "use the bow", "what use are you?"));
        phrases.add(new Homograph("sow", "a sow", "sow the seed"));
        phrases.add(new Homograph("refuse", "i refuse", "the refuse"));
        phrases.add(new Homograph("rebel", "i am a rebel", "I rebel"));
        phrases.add(new Homograph("moped", "i moped", "a moped"));
        phrases.add(new Homograph("invalid", "invalid response", "an invalid"));


        List<String> lines = new ArrayList<>();
        for (Homograph h : phrases) {
            var a = ESpeakWrapper.runEspeak(h.phraseA, ESpeakWrapper.EspeakOptions.UK);
            var b = ESpeakWrapper.runEspeak(h.phraseB, ESpeakWrapper.EspeakOptions.UK);

            var base = noHomophones.get(h.word, null).get();

            var phonA = Arrays.stream(a.split(" "))
                    .filter(s -> s.startsWith(base.substring(0,1)))
                    .findFirst().orElse(a);

            var phonB = Arrays.stream(b.split(" "))
                    .filter(s -> s.startsWith(base.substring(0,1)))
                    .findFirst().orElse(b);

            PosFinder posFinder = new PosFinder(h.word);
            PiperPhonemizer pa = new PiperPhonemizer(new EnglishModel(noHomophones), emptyList(), posFinder);
            pa.toPhonemes(Language.en_GB, h.phraseA);
            var posA = posFinder.pos;

            posFinder = new PosFinder(h.word);
            PiperPhonemizer pb = new PiperPhonemizer(new EnglishModel(noHomophones), emptyList(), posFinder);
            pb.toPhonemes(Language.en_GB, h.phraseB);
            var posB = posFinder.pos;


            if (!phonA.equals(phonB)) {
                System.out.printf("Current %s=%s\n", h.word, base);

                if (!phonA.equals(base)) {
                    lines.add(String.format("lines.add(\"%s=%s|%s\")", h.word, phonA, posA));
                    lines.add(String.format("lines.add(\"%s=%s\")", h.word, phonB));
                    System.out.printf("%s=%s|%s\n", h.word, phonA, posA);
                } else {
                    lines.add(String.format("lines.add(\"%s=%s|%s\")", h.word, phonB, posB));
                    lines.add(String.format("lines.add(\"%s=%s\")", h.word, phonA));
                    System.out.printf("%s=%s|%s\n", h.word, phonB, posB);
                }
            } else {
                System.out.printf("Same %s %s %s %s \n", h.word, phonA, posA, posB);
            }
            System.out.println("-----------");

        }

        lines.forEach(System.out::println);

    }
}

class Homograph {
    String word;
    String phraseA;
    String phraseB;

    Homograph(String word, String phraseA, String phraseB) {
        this.word = word;
        this.phraseA = phraseA;
        this.phraseB = phraseB;
    }
}

class PosFinder implements Trace {

    private final String target;
    Pos pos;

    PosFinder(String target) {
        this.target = target;
    }

    @Override
    public Trace start(String word, Pos pos) {
        if (word.equalsIgnoreCase(target)) {
            this.pos = pos;
        }

        return this;
    }

    @Override
    public void result(String phonemes) {

    }

    @Override
    public void dictionaryHit(String word, Pos pos, String s) {

    }

    @Override
    public void morphology(String base) {

    }

    @Override
    public void phonemeRule(String pattern, String remaining) {

    }

    @Override
    public void syllables(List<String> syllables) {

    }

    @Override
    public void unknownPhoneme(String phoneme) {

    }
}
