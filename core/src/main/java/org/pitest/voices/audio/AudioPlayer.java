package org.pitest.voices.audio;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class AudioPlayer implements LineListener {

    boolean isPlaybackCompleted;

    public void play(byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

        try {
            try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(bais)) {
                try(Clip audioClip = AudioSystem.getClip()) {
                    audioClip.addLineListener(this);
                    audioClip.open(audioStream);
                    audioClip.start();
                    audioClip.drain();
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(LineEvent event) {
        if (LineEvent.Type.STOP == event.getType()) {
            isPlaybackCompleted = true;
        }
    }

}