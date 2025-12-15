package org.pitest.voices.kokoro;

import org.junit.jupiter.api.Test;


import org.pitest.voices.Chorus;
import org.pitest.voices.ChorusConfig;
import org.pitest.voices.Voice;
import org.pitest.voices.audio.Audio;
import org.pitest.voices.g2p.core.tracing.LoggingTrace;
import org.pitest.voices.uk.EnUkDictionary;


public class KokoroTest {

    ChorusConfig config = ChorusConfig.chorusConfig(EnUkDictionary.en_uk())
         //   .withModel(new OpenVoiceSupplier())
            .withTrace(new LoggingTrace());



    @Test
    void wordsWithPauseSymbols() {
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(KokoroModels.amAdam());
            Audio audio = v1.say("Hello world—that was three beats: that was two.");
            Play.play(audio);
        }
    }

    @Test
    void camus() {
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(KokoroModels.afSarah())
                    .withSpeed(1.1f);
            var audio = v1.say("Mother died today. Or maybe, yesterday; I can't be sure.");
            Play.play(audio);
        }
    }

    @Test
    void gibson() {
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(KokoroModels.bfIsabella())
                    .withSpeed(1f);
                   // .withStress(Stresses.KEEP_STRESS);
            var audio = v1.say("The sky above the port was the color of television, tuned to a dead channel.");
            Play.play(audio);
        }
    }

    @Test
    void someWords() {

        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(KokoroModels.bfEmma()).withSpeed(1.2f);

            long[] tokensArray = {
                    50,157,43,135,16,53,135,46,16,43,102,16,56,156,57,135,6,16,102,62,61,
                    16,70,56,16,138,56,156,72,56,61,85,123,83,44,83,54,16,53,65,156,86,61,
                    62,131,83,56,4,16,54,156,43,102,53,16,156,72,61,53,102,112,16,70,56,16,
                    138,56,44,156,76,158,123,56,16,62,131,156,43,102,54,46,16,102,48,16,81,
                    47,102,54,16,54,156,51,158,46,16,70,16,92,156,135,46,16,54,156,43,102,
                    48,4,16,81,47,102,16,50,156,72,64,83,56,62,16,156,51,158,64,83,56,16,
                    44,157,102,56,16,44,156,76,158,123,56,4
            };

            Audio audio =  v1.sayPhonemes(tokensArray);

            Play.play(audio);
        }

    }

}
