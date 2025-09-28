package org.pitest.voices.openvoice;

import org.junit.jupiter.api.Test;
import org.pitest.g2p.core.Dictionary;
import org.pitest.g2p.core.tracing.LoggingTrace;
import org.pitest.g2p.util.Resource;
import org.pitest.voices.Chorus;
import org.pitest.voices.ChorusConfig;
import org.pitest.voices.Models;
import org.pitest.voices.NonEnglishModels;
import org.pitest.voices.Voice;
import org.pitest.voices.audio.Audio;

import static org.pitest.voices.ChorusConfig.chorusConfig;
import static org.pitest.voices.util.Play.play;

class TryItOutTest {

    ChorusConfig config = chorusConfig(Dictionary.empty())
            .withModel(new OpenVoiceSupplier())
            .withTrace(new LoggingTrace());

    @Test
    void orwell() {
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Models.albaMedium());
            var audio = v1.say("It was a bright cold day in April, and the clocks were striking thirteen.");
            play(audio);
        }
    }

    @Test
    void interruptedSpeech() {
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Models.albaMedium());
            var audio = v1.say("\"That's not what I---\"");
            play(audio);
        }
    }

    @Test
    void frFR() {
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(NonEnglishModels.frFRSiwis());
            var audio = v1.say("Je parle français");
            play(audio);
        }
    }

    @Test
    void mobyDick() {
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Models.albaMedium());

            String text = String.join("\n", Resource.read("/samples/moby_dick.md"));
            Audio audio = v1.say(text);
            play(audio);
        }
    }


}