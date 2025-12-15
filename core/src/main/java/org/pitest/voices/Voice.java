package org.pitest.voices;

import org.pitest.voices.audio.Audio;

import java.util.List;

public interface Voice {

    /**
     * Produce audio for text
     * @param text text to speak
     * @return an Audio object containing rendered speech
     */
    Audio say(String text);

    /**
     * Produce audio for phonemes
     * @param text List of IPA phonemes, eg ɹ,eɪ,n,b,əʊ
     * @return an Audio object containing rendered speech
     */
    Audio sayPhonemes(List<String> text);

    /**
     * Produce audio using the low level identifiers for a model
     * @param phonemeIds array of longs representing phonemes
     * @return Audio object containing rendered speech
     */
    Audio sayPhonemes(long[] phonemeIds);

    /**
     * Produce a variation of this voice with different pauses during speech
     * @param pauses pauses to use
     * @return a new Voice
     */
    Voice withPauses(List<Pause> pauses);

    /**
     * Produce a variation of this voice with an explicitly set gain (volume)
     * @param gain gain to use
     * @return a new Voice
     */
    Voice withGain(float gain);


    /**
     * Produce a variation of this voice with a gain (volume) increased
     * by the given factor from the current one
     * @param factor factor to apply
     * @return a new Voice
     */
    Voice amplifiedBy(float factor);

    /**
     * Produce a variation of this voice with an explicitly set speed.
     * @param speed speed to use
     * @return a new Voice
     */
    Voice withSpeed(float speed);

    /**
     * Produce a variation of this voice with an explicitly set stress.
     * Removing stress can result in faster more natural sounding speech, particularly
     * with the kokoro model.
     *
     * @param stress speech stress
     * @return updated parameters
     */
    Voice withStress(Stress stress);

    /**
     * Produce a variation of this voice with explicitly set parameters
     * Lower numbers produce faster speech.
     * @param params params to use
     * @return a new Voice
     */
    Voice withModelParameters(ModelParameters params);
}
