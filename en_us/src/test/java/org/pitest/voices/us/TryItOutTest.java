package org.pitest.voices.us;


import org.junit.jupiter.api.Test;
import org.pitest.voices.alba.Alba;
import org.pitest.voices.bryce.Bryce;
import org.pitest.voices.g2p.core.tracing.LoggingTrace;
import org.pitest.voices.Resource;
import org.pitest.voices.Chorus;
import org.pitest.voices.ChorusConfig;
import org.pitest.voices.Voice;
import org.pitest.voices.audio.Audio;

import static org.pitest.voices.ChorusConfig.chorusConfig;
import static org.pitest.voices.util.Play.play;

public class TryItOutTest {

    ChorusConfig config = chorusConfig(EnUsDictionary.en_us())
            .withTrace(new LoggingTrace());

    @Test
    void gibson() {
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Bryce.bryceMedium()).withLengthScale(0.7f);
            var audio = v1.say("The sky above the port was the color of television, tuned to a dead channel.");
            play(audio);
        }
    }

    @Test
    void huckleberryFinn() {
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Alba.albaMedium()).withLengthScale(0.5f);

            String text = String.join("\n", Resource.read("/samples/huckleberry_finn.md"));
            Audio audio = v1.say(text);
            play(audio);
        }
    }

    @Test
    void aWord() {
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Alba.albaMedium());
            Audio audio = v1.say("abbreviated");
            play(audio);
        }
    }

    @Test
    void someWords() {
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Alba.albaMedium());
            Audio audio = v1.say("usurped\n" +
                    "usurping\n" +
                    "usurps\n" +
                    "utamaro\n" +
                    "utilization\n" +
                    "vanbiesbrouck\n" +
                    "vandenboom\n" +
                    "vanderkooi\n" +
                    "vanderpool\n" +
                    "vandersluis\n" +
                    "vanloo\n" +
                    "vanuaaku\n" +
                    "verisimilitude\n" +
                    "vestibule\n" +
                    "vestibules\n" +
                    "virgule\n" +
                    "virgules\n" +
                    "voodoo\n" +
                    "waldroop\n" +
                    "waldroup\n" +
                    "washroom\n" +
                    "wasiyu\n" +
                    "waterloo\n" +
                    "waterproof\n" +
                    "waterproofing\n" +
                    "weatherproof\n" +
                    "weatherspoon\n" +
                    "wetsuit\n" +
                    "whirlpool\n" +
                    "whirlpool's\n" +
                    "whirlpools\n" +
                    "whomsoever\n" +
                    "whosoever\n" +
                    "wintermute\n" +
                    "witherspoon\n" +
                    "woodroof");
            play(audio);
        }

    }


    @Test
    void homographs() {

        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Alba.albaMedium());

            Audio audio = v1.say(
                    "invalid response for an invalid. " +
                            "I moped on my moped. " +
                            "I rebel because I am a rebel. " +
                            "I refuse to take out the refuse. " +
                            "sow the seeds for the sow to eat. " +
                            "Use it or what use are you?" +
                            "I led the horse to water but it was hit with a lead bar. " +
                            "I will not lead again. Then I read a book. " +
                            "I will read again. Close the door. That was a close shave.");

            play(audio);
        }

    }

}
