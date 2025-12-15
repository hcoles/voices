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

import java.io.IOException;
import java.io.OutputStream;

class WavFileHeader {

    private final long subchunk1Size;
	private final int audioFormat;
	private final int numChannels;
	private final long sampleRate;
	private final long byteRate;
	private final int blockAlign;
	private final int bitsPerSample;

	private long subchunk2Size;


	WavFileHeader(int sampleRate, int numChannels, int bitsPerSample) {
		this.sampleRate = sampleRate;
		this.numChannels = numChannels;
		this.bitsPerSample = bitsPerSample;

		subchunk1Size = 16; // 16
		audioFormat = 1; // PCM
		subchunk2Size = 0;

		this.blockAlign = this.numChannels * this.bitsPerSample/8;
		this.byteRate = this.sampleRate * this.blockAlign;
	}

	static private void write32(long val, OutputStream os) throws IOException {
		os.write( (int)( (val    ) & 0xFF ) );
		os.write( (int)( (val>> 8) & 0xFF ) );
		os.write( (int)( (val>>16) & 0xFF ) );
		os.write( (int)( (val>>24) & 0xFF ) );
	}


	static private void write16(long val, OutputStream os) throws IOException {
		os.write( (int)( (val    ) & 0xFF ) );
		os.write( (int)( (val>> 8) & 0xFF ) );
	}


	public void addToSubchunk2Size(int val) {
		subchunk2Size += val;
	}


	public void writeHeader(OutputStream os) throws IOException {
        long chunkSize = 36 + subchunk2Size;

		// chunkId:
		os.write('R');
		os.write('I');
		os.write('F');
		os.write('F');

		write32(chunkSize, os);

		// format:
		os.write('W');
		os.write('A');
		os.write('V');
		os.write('E');

		// subchunk1Id
		os.write('f');
		os.write('m');
		os.write('t');
		os.write(' ');

		write32(subchunk1Size, os);

		write16(audioFormat, os);
		write16(numChannels, os);
		write32(sampleRate, os);
		write32(byteRate, os);
		write16(blockAlign, os);
		write16(bitsPerSample, os);

		// subchunk2Id
		os.write('d');
		os.write('a');
		os.write('t');
		os.write('a');

		write32(subchunk2Size, os);
	}

}