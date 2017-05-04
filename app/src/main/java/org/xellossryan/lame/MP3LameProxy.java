package org.xellossryan.lame;

import org.xellossryan.abstractlayer.EncoderAbstractLayer;

/**
 * MP3Lame
 * <p>
 * Created by Liang on 2017/5/3.
 */
public class MP3LameProxy extends EncoderAbstractLayer {
    private MP3Lame mp3Lame;

    public MP3LameProxy(MP3Lame mp3Lame) {
        this.mp3Lame = mp3Lame;
    }


    @Override
    public String version() {
        return mp3Lame.version();
    }

    @Override
    public int initEncoder() {
        try {
            mp3Lame.close();
        } catch (Exception ignored) {
        }
        return mp3Lame.initLame();
    }


    @Override
    public int initParameters(Object parameters) {
        ParameterBuilder parameterBuilder = (ParameterBuilder) parameters;
        return mp3Lame.initParameters(parameterBuilder.inSampleRate,
                parameterBuilder.inChannels,
                parameterBuilder.outSampleRate,
                parameterBuilder.outBitrate,
                parameterBuilder.quality);
    }

    @Override
    public int encode(short[] bufferLeft, short[] bufferRight, byte[] encodedBufferOut, int nSamples) {
        return mp3Lame.encode(bufferLeft, bufferRight, nSamples, encodedBufferOut);
    }

    @Override
    public int encodeInterleaved(short[] bufferIn, byte[] encodedBufferOut, int nSamples) {
        return mp3Lame.encodeInterleaved(bufferIn, nSamples, encodedBufferOut);
    }

    @Override
    public int flush(byte[] encodedBufferOut) {
        return mp3Lame.flush(encodedBufferOut);
    }

    @Override
    public int close() {
        return mp3Lame.close();
    }
}
