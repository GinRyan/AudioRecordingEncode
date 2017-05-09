package org.xellossryan.lame;

/**
 * 参数构建器
 */
public class ParameterBuilder {
    int inSampleRate;//设置输入采样率
    int inChannels;//设置输入声道数
    int outSampleRate;//设置输出采样率
    int outBitrate;//设置输出码率
    int quality;//设置输出质量

    public static ParameterBuilder builder() {
        return new ParameterBuilder();
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