package org.pitest.voices.kokoro;

import org.pitest.voices.AbstractVoice;
import org.pitest.voices.Model;
import org.pitest.voices.ModelParameters;
import org.pitest.voices.Pause;
import org.pitest.voices.Stress;
import org.pitest.voices.Voice;
import org.pitest.voices.VoiceSession;
import org.pitest.voices.g2p.core.PiperPhonemizer;
import org.pitest.voices.g2p.core.tracing.Trace;

import java.util.List;

import java.util.stream.LongStream;


public class KokoroVoice extends AbstractVoice {

    KokoroVoice(Model model,
                PiperPhonemizer phonemizer,
                Trace trace,
                VoiceSession session,
                List<Pause> pauses,
                ModelParameters params,
                float gain) {
        super(model, phonemizer, trace, session, pauses, params, gain);
    }


    @Override
    public Voice withPauses(List<Pause> pauses) {
        return new KokoroVoice(model, phonemizer, trace, session, pauses, params, gain);
    }

    @Override
    public Voice withGain(float gain) {
        return new KokoroVoice(model, phonemizer, trace, session, pauses, params, gain);
    }

    @Override
    public Voice amplifiedBy(float factor) {
        return new KokoroVoice(model, phonemizer, trace, session, pauses, params, gain * factor);
    }

    @Override
    public Voice withSpeed(float speed) {
        return new KokoroVoice(model, phonemizer, trace, session, pauses,
                params.withSpeed(speed), gain);
    }

    @Override
    public Voice withStress(Stress stress) {
        return new KokoroVoice(model, phonemizer, trace, session, pauses,
                params.withStress(stress), gain);
    }

    @Override
    public Voice withModelParameters(ModelParameters params) {
        return new KokoroVoice(model, phonemizer, trace, session, pauses, params, gain);
    }

    @Override
    protected LongStream toPhonemeId(String phoneme) {

        Long id = session.idForSymbol(phoneme);

        var pause = pauses.stream().filter(p -> p.matches(phoneme))
                .findFirst();

        if (pause.isPresent()) {
            long beatId = session.idForSymbol(";");
            return LongStream.generate(() -> beatId)
                    .limit(pause.get().beats() * 3L);
        }

        if (id == null) {
            trace.unknownPhoneme(phoneme);
            return LongStream.empty();
        }

        return LongStream.of(id);
    }

}