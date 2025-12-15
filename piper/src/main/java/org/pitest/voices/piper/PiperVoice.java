package org.pitest.voices.piper;

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

public class PiperVoice extends AbstractVoice {
    PiperVoice(Model model,
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
        return new PiperVoice(model, phonemizer, trace, session, pauses, params, gain);
    }

    @Override
    public Voice withGain(float gain) {
        return new PiperVoice(model, phonemizer, trace, session, pauses, params, gain);
    }

    @Override
    public Voice amplifiedBy(float factor) {
        return new PiperVoice(model, phonemizer, trace, session, pauses, params, gain * factor);
    }

    @Override
    public Voice withSpeed(float speed) {
        return new PiperVoice(model, phonemizer, trace, session, pauses,
                params.withSpeed(speed), gain);
    }

    @Override
    public Voice withStress(Stress stress) {
        return new PiperVoice(model, phonemizer, trace, session, pauses,
                params.withStress(stress), gain);
    }
    @Override
    public Voice withModelParameters(ModelParameters params) {
        return new PiperVoice(model, phonemizer, trace, session, pauses, params, gain);
    }

}
