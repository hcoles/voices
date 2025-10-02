package org.pitest.voices;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Piper tts model
 */
public interface Model {

    /**
     * Unique id
     * @return an id
     */
    String id();

    /**
     * Speaker id
     * @return speaker id or -1 if model doesn't support them
     */
    int sid();

    /**
     * Language model speaks
     * @return A language instance
     */
    Language language();

    /**
     * Creates a variation of the model with a different language
     * @param lang Language to use
     * @return a new model instance
     */
    Model withLanguage(Language lang);

    /**
     * The model in bytes
     * @param cacheBase Directory currently used by Voices for any caches
     * @return Model as bytes
     * @throws IOException in event of error
     */
    byte[] asBytes(Path cacheBase) throws IOException;

    /**
     * The model config
     * @param cacheBase Directory currently used by Voices for any caches
     * @return Model config
     * @throws IOException in event of error
     */
    ModelConfig resolveConfig(Path cacheBase) throws IOException;

    /**
     * Default gain to use with this model. Useful as some models
     * appear much louder or quieter than others
     * @return float representing the default gain
     */
    float defaultGain();

}
