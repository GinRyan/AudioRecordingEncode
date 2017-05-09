package org.xellossryan.abstractlayer;

/**
 * 音频编码抽象层
 * Created by Liang on 2017/5/3.
 */
public abstract class EncoderAbstractLayer implements EncoderLayer {

    @Override
    public abstract String version();

    @Override
    public abstract int initEncoder();

    @Override
    public abstract int initParameters(Object parameters);

    @Override
    public abstract int encode(short[] bufferLeft, short[] bufferRight, byte[] encodedBufferOut, int nSamples);

    @Override
    public abstract int encodeInterleaved(short[] bufferIn, byte[] encodedBufferOut, int nSamples);

    @Override
    public abstract int flush(byte[] encodedBufferOut);

    @Override
    public abstract int close();

}
