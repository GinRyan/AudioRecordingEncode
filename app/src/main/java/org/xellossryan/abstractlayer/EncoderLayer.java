package org.xellossryan.abstractlayer;

import java.util.HashMap;

/**
 * Created by Liang on 2017/5/3.
 */
public interface EncoderLayer {

    public int initEncoder();

    public int initParameters(Object parameters);

    public int encode(short[] bufferLeft, short[] bufferRight, byte[] encodedBufferOut, int nSamples);

    public int encodeInterleaved(short[] bufferIn,byte[] encodedBufferOut, int nSamples);

    public int flush(byte[] encodedBufferOut);

    public int close();
}
