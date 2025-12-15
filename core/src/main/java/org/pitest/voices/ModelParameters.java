package org.pitest.voices;

public record ModelParameters(Stress stress, float speed) {

    public ModelParameters(Stress stress, float speed) {
        this.stress = stress;
        this.speed = speed;
        if (speed < 0 || speed > 2) {
            throw new IllegalArgumentException("Speed must be between 0 and 2 inclusive");
        }
    }

    public static ModelParameters defaultParams() {
        return new ModelParameters(Stresses.KEEP_STRESS, .9f);
    }

    /**
     * Speed of speech between 0 and 2 inclusive.
     *
     * @param speed speech speed
     * @return updated parameters
     */
    public ModelParameters withSpeed(float speed) {
        return new ModelParameters(stress, speed);
    }

    /**
     * Determine how stress is applied to phonemes. Removing stress
     * can result in faster more natural sounding speech, particularly
     * with the kokoro model.
     *
     * @param stress speech stress
     * @return updated parameters
     */
    public ModelParameters withStress(Stress stress) {
        return new ModelParameters(stress, speed);
    }

    public String processPhoneme(String s) {
        return stress.apply(s);
    }
}
