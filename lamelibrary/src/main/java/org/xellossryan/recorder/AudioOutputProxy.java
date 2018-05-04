package org.xellossryan.recorder;

import org.xellossryan.abstractlayer.EncoderAbstractLayer;
import org.xellossryan.recorder.AudioOutput;

/**
 * 音频输出代理
 */
public class AudioOutputProxy extends EncoderAbstractLayer {
    AudioOutput output = new AudioOutput();

    @Override
    public String version() {
        return "HearAid Echo 1.0.1";
    }

    @Override
    public int initEncoder() {
        output = new AudioOutput();
        return 0;
    }

    @Override
    public int initParameters(Object parameters) {
        output.play();
        return 0;
    }

    @Override
    public int encode(short[] bufferLeft, short[] bufferRight, byte[] encodedBufferOut, int nSamples) {
        output.write(bufferLeft, 0, bufferLeft.length);
        return 0;
    }

    @Override
    public int encodeInterleaved(short[] bufferIn, byte[] encodedBufferOut, int nSamples) {
        output.write(bufferIn, 0, bufferIn.length);
        return 0;
    }

    @Override
    public int flush(byte[] encodedBufferOut) {
        output.stopPlaying();
        return 0;
    }

    @Override
    public int close() {
        output.release();
        return 0;
    }
}
