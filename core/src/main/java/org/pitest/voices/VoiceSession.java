package org.pitest.voices;

import org.pitest.voices.audio.Audio;

public interface VoiceSession extends AutoCloseable {
    Long idForSymbol(String phoneme);

    Audio sayPhonemes(int sid, long[] phoneme_ids, float gain, ModelParameters params);
}
