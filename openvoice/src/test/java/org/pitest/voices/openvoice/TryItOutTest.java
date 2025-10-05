package org.pitest.voices.openvoice;

import org.junit.jupiter.api.Test;
import org.pitest.voices.alba.Alba;
import org.pitest.voices.g2p.core.Dictionary;
import org.pitest.voices.g2p.core.dictionary.Dictionaries;
import org.pitest.voices.g2p.core.tracing.LoggingTrace;
import org.pitest.voices.Resource;
import org.pitest.voices.Chorus;
import org.pitest.voices.ChorusConfig;
import org.pitest.voices.Voice;
import org.pitest.voices.audio.Audio;

import static org.pitest.voices.ChorusConfig.chorusConfig;
import static org.pitest.voices.util.Play.play;

class TryItOutTest {

    ChorusConfig config = chorusConfig(Dictionaries.empty())
            .withModel(new OpenVoiceSupplier())
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
    void interruptedSpeech() {
        try (Chorus chorus = new Chorus(config)) {
            Voice v1 = chorus.voice(Alba.albaMedium());
            var audio = v1.say("That's not what I---");
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
    void usingHomographDictionary() {

        try (Chorus chorus = new Chorus(config.withDictionary(Dictionaries.englishHomographs()))) {
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

}