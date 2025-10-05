package org.pitest.voices.uk;

import org.junit.jupiter.api.Test;
import org.pitest.voices.alba.Alba;
import org.pitest.voices.cori.Cori;
import org.pitest.voices.g2p.core.tracing.LoggingTrace;
import org.pitest.voices.Resource;
import org.pitest.voices.Chorus;
import org.pitest.voices.ChorusConfig;
import org.pitest.voices.Pause;
import org.pitest.voices.Voice;
import org.pitest.voices.audio.Audio;

import java.util.List;

import static org.pitest.voices.ChorusConfig.chorusConfig;
import static org.pitest.voices.util.Play.play;

public class TryItOutTest {

    ChorusConfig config = chorusConfig(EnUkDictionary.en_uk())
            .withTrace(new LoggingTrace());

    @Test
    void orwell() {
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Alba.albaMedium());
            var audio = v1.say("It was a bright cold day in April, and the clocks were striking thirteen.");
            play(audio);
        }
    }

    @Test
    void mobyDick() {
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Alba.albaMedium());

            String text = String.join("\n", Resource.read("/samples/moby_dick.md"));
            Audio audio = v1.say(text);
            play(audio);
        }
    }

    @Test
    void wutheringHeights() {
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Cori.coriHigh())
                    .withPauses(List.of(new Pause(",", 6)));
            String text = String.join("\n", Resource.read("/samples/wuthering_heights.md"));
            Audio audio = v1.say(text);
            play(audio);
        }
    }

    @Test
    void dashes() {
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Alba.albaMedium());

            Audio audio = v1.say("It wasn't meant for visitors like this one---low and insignificant.");
            play(audio);
        }
    }


    @Test
    void homographs() {

        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Alba.albaMedium());

            Audio audio = v1.say(
                    "I wound the bobbin. I have a wound " +
                    "Don't tarry. A tarry rag. " +
                    "I separate the sheep into separate pens. " +
                    "A record. I record the show. " +
                    "I produce produce. " +
                    "An object. I object to that. " +
                    "I learned the rules. The learned woman. " +
                    "Intimate clothing. What did you intimate? " +
                    "Your house. We should house everyone. " +
                    "You make an excuse. I excuse you. " +
                    "You entrance me. I make an entrance. " +
                    "With dogged determination I dogged his steps. " +
                    "I bow down. I like your bow. " +
                    "I articulate. He is very articulate. " +
                    "invalid response for an invalid. " +
                            "I moped on my moped. " +
                            "I rebel because I am a rebel. " +
                             "I refuse to take out the refuse. " +
                            "sows the seeds for the sows to eat. " +
                            "Use it or what use are you?" +
                            "I led the horse to water but it was hit with a lead bar. " +
                            "I will not lead again. Then I read a book. " +
                            "I will read again. I close the door. That was a close shave.");

            play(audio);
        }

    }


    @Test
    void rainbows() {
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Alba.albaMedium());
            Audio audio = v1.say("A rainbow is a meteorological phenomenon that is caused by reflection, " +
                    "refraction and dispersion of light in water droplets resulting in a spectrum of light appearing in the sky.");

            play(audio);
        }
    }

    @Test
    void hyphens() {

        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Alba.albaMedium());
            Audio audio = v1.say("anti-theft---a security term---device");
            play(audio);
        }

    }

    @Test
    void titles() {
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Alba.albaMedium());
            Audio audio = v1.say("# Ezra.");
            play(audio);
        }
    }

   // @Test
    void normalise() {
        String text = "This is a sentence";
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Alba.albaMedium());
            Audio reference = v1.say(text);
            float max = reference.max();

            for (float gain = 0.7f; gain < 5.0f; gain += 0.1f) {
                var v2 = chorus.voice(Cori.coriHigh())
                        .withGain(gain)
                        .say(text);
                System.out.println("Testing gain " + gain + " for max " + max + " got " + v2.max());
                if (v2.max() >= max) {
                    System.out.println("max is " + max + " for gain " + gain);
                    play(reference);
                    play(v2);
                    break;
                }
            }


        }
    }


}
