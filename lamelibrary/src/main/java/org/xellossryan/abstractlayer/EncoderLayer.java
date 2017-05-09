package org.xellossryan.abstractlayer;

/**
 * 编码器层
 *
 * Created by Liang on 2017/5/3.
 */
public interface EncoderLayer {
    /**
     * 初始化编码器操作
     *
     * @return
     */
    public int initEncoder();

    /**
     * 使用特定参数对象编码
     *
     * @param parameters
     * @return
     */
    public int initParameters(Object parameters);

    /**
     * 区分左右声道编码
     *
     * @param bufferLeft       PCM左声道数据
     * @param bufferRight      PCM右声道数据
     * @param encodedBufferOut 已编码数据
     * @param nSamples         采样数据
     * @return
     */
    public int encode(short[] bufferLeft, short[] bufferRight, byte[] encodedBufferOut, int nSamples);

    /**
     * 编码交织PCM缓冲数据
     *
     * @param bufferIn
     * @param encodedBufferOut
     * @param nSamples
     * @return
     */
    public int encodeInterleaved(short[] bufferIn, byte[] encodedBufferOut, int nSamples);

    /**
     * 刷新缓冲区
     *
     * @param encodedBufferOut
     * @return
     */
    public int flush(byte[] encodedBufferOut);

    /**
     * 关闭和释放编码器资源
     *
     * @return
     */
    public int close();

    public String version();
}
