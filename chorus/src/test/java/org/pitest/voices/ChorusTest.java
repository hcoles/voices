package org.pitest.voices;

import org.junit.jupiter.api.Test;
import org.pitest.g2p.core.Dictionary;
import org.pitest.g2p.core.EnglishModel;
import org.pitest.g2p.core.PiperPhonemizer;
import org.pitest.g2p.core.expansions.NumberExpander;
import org.pitest.g2p.core.tracing.LoggingTrace;
import org.pitest.g2p.core.tracing.Trace;
import org.pitest.voices.audio.Audio;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pitest.voices.ChorusConfig.chorusConfig;
import static org.pitest.voices.util.Play.play;

/**
 * Results without a dictionary are generally terrible
 */
class ChorusTest {

    ChorusConfig config = chorusConfig(Dictionary.empty())
            .withTrace(new LoggingTrace());


    @Test
    void someWords() {

        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Models.albaMedium());

            Audio audio = v1.say("A rainbow is a meteorological phenomenon that is caused by reflection, " +
                    "refraction and dispersion of light in water droplets resulting in a spectrum of light appearing in the sky.");

            play(audio);
        }

    }

    @Test
    void numbers() {
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Models.albaMedium());

            Audio audio = v1.say("The year is 2025");

            play(audio);
        }
    }

    @Test
    void dashes() {
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Models.albaMedium());

            Audio audio = v1.say("It wasn't meant for visitors like this one---low and insignificant.");
            play(audio);
        }
    }

}
