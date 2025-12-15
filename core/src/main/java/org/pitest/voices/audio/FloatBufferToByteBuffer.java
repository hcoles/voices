/*

MIT License

Copyright © 2024 HARDCODED JOY S.R.L. (https://hardcodedjoy.com)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package org.pitest.voices.audio;

class FloatBufferToByteBuffer {

    static public void convert(float[] samples, int offset, int len, boolean swapLR, int bitsPerSample, byte[] byteBuffer) {
        switch (bitsPerSample) {
            case 32: convert32(samples, offset, len, swapLR, byteBuffer); break;
            case 24: convert24(samples, offset, len, swapLR, byteBuffer); break;
            case 16: convert16(samples, offset, len, swapLR, byteBuffer); break;
            case 12: convert12(samples, offset, len, swapLR, byteBuffer); break;
            case 10: convert10(samples, offset, len, swapLR, byteBuffer); break;
            case  8: convert8 (samples, offset, len, swapLR, byteBuffer); break;
            case  7: convert7 (samples, offset, len, swapLR, byteBuffer); break;
            case  6: convert6 (samples, offset, len, swapLR, byteBuffer); break;
            case  5: convert5 (samples, offset, len, swapLR, byteBuffer); break;
            case  4: convert4 (samples, offset, len, swapLR, byteBuffer); break;
            case  3: convert3 (samples, offset, len, swapLR, byteBuffer); break;
            case  2: convert2 (samples, offset, len, swapLR, byteBuffer); break;
            case  1: convert1 (samples, offset, len, swapLR, byteBuffer); break;
            default: break;
        }
    }

    static public void convert32(float[] samples, int offset, int len, boolean swapLR, byte[] byteBuffer) {
        long sample;
        float fSample;
        int end = offset + len;
        int v = 0; // index variance
        if(swapLR) { v = 1; }

        for(int i=offset, j=0; i<end; i++, v=-v) {
            fSample = limit(samples[i+v]);

            //sample = (long)(fSample * 2147483647); // 32-bit integer
            sample = Float.floatToIntBits(fSample); // 32-bit float

            byteBuffer[j++] = (byte)sample; sample = sample >> 8;
            byteBuffer[j++] = (byte)sample; sample = sample >> 8;
            byteBuffer[j++] = (byte)sample; sample = sample >> 8;
            byteBuffer[j++] = (byte)sample;
        }
    }

    static public void convert24(float[] samples, int offset, int len, boolean swapLR, byte[] byteBuffer) {
        long sample;
        float fSample;
        int end = offset + len;
        int v = 0; // index variance
        if(swapLR) { v = 1; }

        for(int i=offset, j=0; i<end; i++, v=-v) {
            fSample = limit(samples[i+v]);
            sample = (long)(fSample * 8388607);
            byteBuffer[j++] = (byte)sample; sample = sample >> 8;
            byteBuffer[j++] = (byte)sample; sample = sample >> 8;
            byteBuffer[j++] = (byte)sample;
        }
    }

    static public void convert16(float[] samples, int offset, int len, boolean swapLR, byte[] byteBuffer) {
        long sample;
        float fSample;
        int end = offset + len;
        int v = 0; // index variance
        if(swapLR) { v = 1; }

        for(int i=offset, j=0; i<end; i++, v=-v) {
            fSample = limit(samples[i+v]);
            sample = (long)(fSample * 32767);
            byteBuffer[j++] = (byte)sample; sample = sample >> 8;
            byteBuffer[j++] = (byte)sample;
        }
    }

    static public void convert12(float[] samples, int offset, int len, boolean swapLR, byte[] byteBuffer) {
        long sample;
        float fSample;
        int end = offset + len;
        int v = 0; // index variance
        if(swapLR) { v = 1; }

        for(int i=offset, j=0; i<end; i++, v=-v) {
            fSample = samples[i+v];
            fSample += ((float)Math.random() - 0.5f) * 0.000244141f; // * 1 / 4096
            fSample = limit(fSample);

            sample = (long)(fSample * 32767);

            // remove 4 bits from 16:
            sample = sample >> 4;
            sample = sample << 4;

            byteBuffer[j++] = (byte)sample; sample = sample >> 8;
            byteBuffer[j++] = (byte)sample;
        }
    }

    static public void convert10(float[] samples, int offset, int len, boolean swapLR, byte[] byteBuffer) {
        long sample;
        float fSample;
        int end = offset + len;
        int v = 0; // index variance
        if(swapLR) { v = 1; }

        for(int i=offset, j=0; i<end; i++, v=-v) {
            fSample = samples[i+v];
            fSample += ((float)Math.random() - 0.5f) * 0.000976562f; // * 1 / 1024
            fSample = limit(fSample);

            sample = (long)(fSample * 32767);

            // remove 6 bits from 16:
            sample = sample >> 6;
            sample = sample << 6;

            byteBuffer[j++] = (byte)sample; sample = sample >> 8;
            byteBuffer[j++] = (byte)sample;
        }
    }

    static public void convert8(float[] samples, int offset, int len, boolean swapLR, byte[] byteBuffer) {
        long sample;
        float fSample;
        int end = offset + len;
        int v = 0; // index variance
        if(swapLR) { v = 1; }

        for(int i=offset, j=0; i<end; i++, v=-v) {
            fSample = samples[i+v];
            fSample += ((float)Math.random() - 0.5f) * 0.00390625f; // * 1 / 256
            fSample = limit(fSample);
            sample = ((long)(fSample * 127)) + 127;
            byteBuffer[j++] = (byte)sample;
        }
    }

    static public void convert7(float[] samples, int offset, int len, boolean swapLR, byte[] byteBuffer) {
        long sample;
        float fSample;
        int end = offset + len;
        int v = 0; // index variance
        if(swapLR) { v = 1; }

        for(int i=offset, j=0; i<end; i++, v=-v) {
            fSample = samples[i+v];
            fSample += ((float)Math.random() - 0.5f) * 0.0078125f; // * 1 / 128
            fSample = limit(fSample);
            sample = ((long)(fSample * 127)) + 127;
            sample = (sample) / 2;
            sample *= 2;
            byteBuffer[j++] = (byte)sample;
        }
    }

    static public void convert6(float[] samples, int offset, int len, boolean swapLR, byte[] byteBuffer) {
        long sample;
        float fSample;
        int end = offset + len;
        int v = 0; // index variance
        if(swapLR) { v = 1; }

        for(int i=offset, j=0; i<end; i++, v=-v) {
            fSample = samples[i+v];
            fSample += ((float)Math.random() - 0.5f) * 0.015625f; // * 1 / 64
            fSample = limit(fSample);
            sample = ((long)(fSample * 127)) + 127;
            sample = (sample) / 4;
            sample *= 4;
            byteBuffer[j++] = (byte)sample;
        }
    }

    static public void convert5(float[] samples, int offset, int len, boolean swapLR, byte[] byteBuffer) {
        long sample;
        float fSample;
        int end = offset + len;
        int v = 0; // index variance
        if(swapLR) { v = 1; }

        for(int i=offset, j=0; i<end; i++, v=-v) {
            fSample = samples[i+v];
            fSample += ((float)Math.random() - 0.5f) * 0.03125f; // * 1 / 32
            fSample = limit(fSample);
            sample = ((long)(fSample * 127)) + 127;
            sample = (sample) / 8;
            sample *= 8;
            byteBuffer[j++] = (byte)sample;
        }
    }

    static public void convert4(float[] samples, int offset, int len, boolean swapLR, byte[] byteBuffer) {
        long sample;
        float fSample;
        int end = offset + len;
        int v = 0; // index variance
        if(swapLR) { v = 1; }

        for(int i=offset, j=0; i<end; i++, v=-v) {
            fSample = samples[i+v];
            fSample += ((float)Math.random() - 0.5f) * 0.0625f; // * 1 / 16
            fSample = limit(fSample);
            sample = ((long)(fSample * 127)) + 127;
            sample = (sample) / 16;
            sample *= 16;
            byteBuffer[j++] = (byte)sample;
        }
    }

    static public void convert3(float[] samples, int offset, int len, boolean swapLR, byte[] byteBuffer) {
        long sample;
        float fSample;
        int end = offset + len;
        int v = 0; // index variance
        if(swapLR) { v = 1; }

        for(int i=offset, j=0; i<end; i++, v=-v) {
            fSample = samples[i+v];
            fSample += ((float)Math.random() - 0.5f) * 0.125f; // * 1 / 8
            fSample = limit(fSample);
            sample = ((long)(fSample * 127)) + 127;
            sample = (sample) / 32;
            sample *= 32;
            byteBuffer[j++] = (byte)sample;
        }
    }

    static public void convert2(float[] samples, int offset, int len, boolean swapLR, byte[] byteBuffer) {
        long sample;
        float fSample;
        int end = offset + len;
        int v = 0; // index variance
        if(swapLR) { v = 1; }

        for(int i=offset, j=0; i<end; i++, v=-v) {
            fSample = samples[i+v];
            fSample += ((float)Math.random() - 0.5f) * 0.25f; // * 1 / 4
            fSample = limit(fSample);
            sample = ((long)(fSample * 127)) + 127;
            sample = (sample) / 64;
            sample *= 64;
            byteBuffer[j++] = (byte)sample;
        }
    }

    static public void convert1(float[] samples, int offset, int len, boolean swapLR, byte[] byteBuffer) {
        long sample;
        float fSample;
        int end = offset + len;
        int v = 0; // index variance
        if(swapLR) { v = 1; }

        for(int i=offset, j=0; i<end; i++, v=-v) {
            fSample = samples[i+v];
            fSample += ((float)Math.random() - 0.5f) * 0.5f; // * 1 / 2
            fSample = limit(fSample);
            sample = (fSample < 0) ? 0 : 255;
            byteBuffer[j++] = (byte)sample;
        }
    }

    static private float limit(float f) {
        if(f >  0.99999f) { return   0.99999f; }
        if(f < -0.99999f) { return  -0.99999f; }
        return f;
    }
}