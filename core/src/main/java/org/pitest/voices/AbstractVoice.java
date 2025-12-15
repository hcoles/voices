package org.pitest.voices;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import org.pitest.voices.audio.Audio;
import org.pitest.voices.g2p.core.PiperPhonemizer;
import org.pitest.voices.g2p.core.tracing.Trace;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Voice implementations do not greatly vary between our two
 * models, so most implementation logic is concentrated in
 * this class.
 */
public abstract class AbstractVoice implements Voice {
    // magic single character symbols to represent pauses
    // these are intended to be added internally, but will be processed
    // if present in the supplied text
    private static final String PAUSE_1_SEC = "♣";
    private static final String PAUSE_HALF_SEC = "♠";

    private final static SentenceDetectorME sentenceDetector = loadSentenceDetector();

    protected final VoiceSession session;
    protected final PiperPhonemizer phonemizer;
    protected final Model model;
    protected final Trace trace;

    protected final List<Pause> pauses;
    protected final ModelParameters params;
    protected final float gain;

    public AbstractVoice(Model model,
                         PiperPhonemizer phonemizer,
                         Trace trace,
                         VoiceSession session,
                         List<Pause> pauses,
                         ModelParameters params,
                         float gain) {
        this.session = session;
        this.phonemizer = phonemizer;
        this.model = model;
        this.trace = trace;
        this.pauses = pauses;
        this.params = params;
        this.gain = gain;
    }

    @Override
    public Audio say(String text) {
        return toSentences(stripMarkdownEmphasis(text)).stream()
                .map(this::saySentence)
                .reduce(Audio::append)
                .orElseGet(() -> Audio.silence(0));
    }

    @Override
    public Audio sayPhonemes(List<String> text) {
        long[] phoneme_ids = text.stream()
                .map(params::processPhoneme)
                .flatMap(this::asChars)
                .filter(p -> !p.isEmpty())
                .flatMapToLong(this::toPhonemeId)
                .toArray();

        return sayPhonemes(phoneme_ids);
    }

    @Override
    public Audio sayPhonemes(long[] phoneme_ids) {
        return session.sayPhonemes(model.sid(),
                phoneme_ids,
                gain,
                params);
    }

    private Audio saySentence(String text) {
        if (text.equals(PAUSE_1_SEC)) {
            return Audio.silence(1);
        }

        if (text.equals(PAUSE_HALF_SEC)) {
            return Audio.smallSilence(5);
        }

        return sayPhonemes(phonemizer.phonemize(model.language(), addPauseSymbols(text)));
    }

    private String addPauseSymbols(String text) {
        return hyphensToDashes(text);
    }

    // Replace multi hyphens with em and en dashes, giving us a single
    // token to convert to pauses after the text has passed through the phonemizer.
    // The source text may already use these symbols, in which case this is redundant
    private String hyphensToDashes(String text) {
        return text.replaceAll("---", "—")
                .replaceAll("--", "–");

    }

    // do we gain anything from this?
    private List<String> toSentences(String text) {
        return Arrays.stream(sentenceDetector.sentDetect(text))
                .flatMap(s -> Arrays.stream(s.split("\n")))
                .map(String::trim) // required?
                .flatMap(this::breakWithPauseSymbols)
                .filter(s -> !s.isEmpty()) // required?
                .collect(Collectors.toList());
    }

    private Stream<String> breakWithPauseSymbols(String s) {
        // title
        if (s.startsWith("#")) {
            String working = s;
            if (!working.endsWith(".")) {
                working = working + ".";
            }
            return Stream.concat(Stream.of(working.replaceAll("#", "")), Stream.of(PAUSE_1_SEC));
        }

        // section break
        if (s.equals("---")) {
            return Stream.generate(() -> PAUSE_1_SEC).limit(3);
        }

        // speech on new line
        if (s.startsWith("\"")) {
            return Stream.concat(Stream.of(PAUSE_HALF_SEC), Stream.of(s));
        }

        return Stream.of(s);
    }

    protected LongStream toPhonemeId(String phoneme) {
        Long id = session.idForSymbol(phoneme);

        var pause = pauses.stream().filter(p -> p.matches(phoneme))
                .findFirst();

        if (pause.isPresent()) {
            long beatId = session.idForSymbol(";");
            return LongStream.generate(() -> beatId)
                    .limit(pause.get().beats() * 2L);
        }

        if (id == null) {
            trace.unknownPhoneme(phoneme);
            return LongStream.empty();
        }

        return LongStream.of(id, id);
    }

    private static SentenceDetectorME loadSentenceDetector() {
        try (var model = Resource.readAsStream("/en-sent.bin")) {
            SentenceModel posModel = new SentenceModel(model);
            return new SentenceDetectorME(posModel);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private String stripMarkdownEmphasis(String text) {
        // we don't currently process emphasis, but the markdown symbols
        // break dictionary lookup
        return text.replace("*", "");
    }

    private Stream<String> asChars(String s) {
        return s.chars()
                .mapToObj(c -> "" + (char) c);
    }

}