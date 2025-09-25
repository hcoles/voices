package org.pitest.voices.audio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class Audio {
    private final float[] samples;
    private final int sampleRate;

    public Audio(float[] samples, int sampleRate) {
        this.samples = samples;
        this.sampleRate = sampleRate;
    }

    public static Audio silence(int seconds) {
        float[] samples = new float[seconds * 22050];
        return new Audio(samples, 22050);
    }

    public static Audio smallSilence(int tenthSeconds) {
        float[] samples = new float[tenthSeconds * 2205];
        return new Audio(samples, 22050);
    }

    public void play() {
        AudioPlayer player = new AudioPlayer();
        player.play(asBytes());
    }

    public byte[] asBytes() {
        var bos = new ByteArrayOutputStream();
        save(bos);
        return bos.toByteArray();
    }

    public void save(Path filename) {
        try (OutputStream fos = Files.newOutputStream(filename, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
           save(fos);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void save(OutputStream os) {
        // amazingly Java doesn't seem to have much in the way of wav handling
        // libraries, so need to do the low level work ourselves
        int bitsPerSample = 16;
        int bytesPerSample = bitsPerSample / 8;

        WavFileHeader wavFileHeader = new WavFileHeader(this.sampleRate, 1, bitsPerSample);
        try {
            int byteBufferLen = this.samples.length * bytesPerSample;
            wavFileHeader.addToSubchunk2Size(byteBufferLen);
            wavFileHeader.writeHeader(os);
            byte[] byteBuffer = new byte[byteBufferLen];
            FloatBufferToByteBuffer.convert(samples, 0, samples.length, false, bitsPerSample, byteBuffer);
            os.write(byteBuffer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Audio append(Audio other) {
        if (this.sampleRate != other.sampleRate) {
            throw new IllegalArgumentException("Sample rates must match");
        }

        float[] combined = concat(this.samples, other.samples);

        return new Audio(combined, this.sampleRate);
    }

    public Audio withGain(double gain) {
        float[] samples = new float[this.samples.length];
        float[] orig = this.samples;
        for (int i = 0; i < samples.length; i++) {
            float newValue = (float) (orig[i] * gain);
            if(newValue > 1.0f) {
                newValue = 1.0f;
            } else if(newValue < -1.0f) {
                newValue = -1.0f;
            }
            samples[i] = newValue;
        }
        return new Audio(samples, this.sampleRate);

    }

    public int getSampleRate() {
        return sampleRate;
    }

    public float[] getSamples() {
        return samples;
    }

    static float[] concat(float[] a, float[] b) {
        int len1 = Array.getLength(a);
        int len2 = Array.getLength(b);

        float[] result = new float[len1 + len2];

        System.arraycopy(a, 0, result, 0, len1);
        System.arraycopy(b, 0, result, len1, len2);

        return result;
    }

    public float max() {
        float max = 0.0f;
        for (float sample : samples) {
            if (sample > max) {
                max = sample;
            }
        }
        return max;
    }
}
