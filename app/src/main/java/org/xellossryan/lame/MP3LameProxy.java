package org.xellossryan.lame;

import org.xellossryan.abstractlayer.AbstractEncoderLayer;
import org.xellossryan.lame.MP3Lame;

/**
 * MP3Lame
 * <p>
 * Created by Liang on 2017/5/3.
 */
public class MP3LameProxy extends AbstractEncoderLayer {
    private MP3Lame mp3Lame;

    public MP3LameProxy(MP3Lame mp3Lame) {
        this.mp3Lame = mp3Lame;
    }

    public static ParameterBuilder builder() {
        return new ParameterBuilder();
    }

    private static class ParameterBuilder {
        int inSampleRate;//设置输入采样率
        int inChannels;//设置输入声道数
        int outSampleRate;//设置输出采样率
        int outBitrate;//设置输出码率
        int quality;//设置输出质量

        public int getInSampleRate() {
            return inSampleRate;
        }

        /**
         * 设置输入采样率
         *
         * @param inSampleRate
         * @return
         */
        public ParameterBuilder setInSampleRate(int inSampleRate) {
            this.inSampleRate = inSampleRate;
            return this;
        }

        /**
         * 设置输入声道数
         *
         * @param inChannels
         * @return
         */
        public ParameterBuilder setInChannels(int inChannels) {
            this.inChannels = inChannels;
            return this;
        }

        /**
         * 设置输出采样率
         *
         * @param outSampleRate
         * @return
         */
        public ParameterBuilder setOutSampleRate(int outSampleRate) {
            this.outSampleRate = outSampleRate;
            return this;
        }

        /**
         * 设置输出码率
         *
         * @param outBitrate
         * @return
         */
        public ParameterBuilder setOutBitrate(int outBitrate) {
            this.outBitrate = outBitrate;
            return this;
        }

        /**
         * 设置输出质量
         *
         * @param quality
         * @return
         */
        public ParameterBuilder setQuality(int quality) {
            this.quality = quality;
            return this;
        }
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
