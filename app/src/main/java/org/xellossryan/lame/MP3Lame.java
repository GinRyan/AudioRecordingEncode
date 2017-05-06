package org.xellossryan.lame;

/**
 * Created by Liang on 2017/5/2.
 */

public class MP3Lame {

    private MP3Lame() {
    }

    private static final MP3Lame LAME = new MP3Lame();

    public static MP3Lame getInstance() {
        return LAME;
    }


    /**
     * 载入mp3lame库
     */
    static {
        System.loadLibrary("mp3lame");
    }

    /**
     * 获取lame的版本信息
     */
    public native String version();

    /**
     * 初始化lame编解码器
     *
     * @return
     */
    public native int initLame();

    /**
     * 初始化参数
     *
     * @param inSampleRate  PCM输入采样率
     * @param inChannels    输入声道数
     * @param outSampleRate 输出采样率
     * @param outBitrate    输出码率
     * @param quality       质量quality=0~9.  0=最好 (但是速度非常慢).  9=最差.
     * @return 0
     */
    public native int initParameters(int inSampleRate, int inChannels, int outSampleRate, int outBitrate, int quality);

    /**
     * 分配新的空缓冲区
     *
     * @param nSamples
     * @return
     */
    public static byte[] allocateBuffer(int nSamples) {
        int length = (int) Math.ceil(1.25 * nSamples + 7200L);
        return new byte[length];
    }

    /**
     * 获得MP3 buffer 大小
     * @return
     */
    public static native int getMP3BufferSize();
    /**
     * 获得MP3 buffer 大小
     * @return
     */
    public static native int getMP3BufferSizeBySample(int samples);
    /**
     * 将PCM编码为mp3帧
     *
     * @param bufferLeft  PCM左声道缓冲数据
     * @param bufferRight PCM右声道缓冲数据
     * @param nSamples    每个声道的采样数
     * @param mp3buf      编码好的mp3流，该数组大小应为至少 1.25 x nSamples + 7200
     * @return -1为未初始化错误
     */
    public native int encode(short[] bufferLeft, short[] bufferRight, int nSamples, byte[] mp3buf);

    /**
     * 将交错的PCM编码为mp3帧
     *
     * @param bufferIn PCM缓冲数据
     * @param nSamples 每个声道的采样数
     * @param mp3buf   编码好的mp3流，该数组大小应为至少 1.25 x nSamples + 7200
     * @return -1:  mp3buf was too small
     * -2:  malloc() problem
     * -3:  lame_init_params() not called
     * -4:  psycho acoustic problems
     */
    public native int encodeInterleaved(short[] bufferIn, int nSamples, byte[] mp3buf);

    /**
     * flush掉缓冲数据
     *
     * @param mp3buf
     * @return
     */
    public native int flush(byte[] mp3buf);

    /**
     * 关闭lame
     *
     * @return
     */
    public native int close();
}
