package org.pitest.voices;

public class ModelParameters {
    private final float lengthScale;
    private final float noiseScale;
    private final float noiseScaleW;

    public ModelParameters(float lengthScale, float noiseScale, float noiseScaleW) {
        this.lengthScale = lengthScale;
        this.noiseScale = noiseScale;
        this.noiseScaleW = noiseScaleW;
    }

    public static ModelParameters defaultParams() {
      // smaller length scale gives faster speech, not clear that the others have
      // much effect
      return new ModelParameters(0.9f, 0.667f, 0.9f);
    }

    public float lengthScale() {
        return lengthScale;
    }

    public float noiseScale() {
        return noiseScale;
    }

    public float noiseScaleW() {
        return noiseScaleW;
    }

    public ModelParameters withLengthScale(float lengthScale) {
        return new ModelParameters(lengthScale, noiseScale, noiseScaleW);
    }
}
