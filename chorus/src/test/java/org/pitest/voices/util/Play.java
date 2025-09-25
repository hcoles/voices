package org.pitest.voices.util;

import org.pitest.voices.audio.Audio;

public class Play {
    public static void play(Audio audio) {
        if (System.getProperty("silent") != null) {
            return;
        }
        audio.play();
    }
}
