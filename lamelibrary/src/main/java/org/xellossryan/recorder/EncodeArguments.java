package org.xellossryan.recorder;

import android.media.AudioFormat;
import android.media.MediaRecorder;

import org.xellossryan.abstractlayer.PCMFormat;

/**
 * Created by Liang on 2017/5/3.
 */

public class EncodeArguments {

    //=======================AudioRecord Default Settings=======================
    public static final int DEFAULT_AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    /**
     * 以下三项为默认配置参数。Google Android文档明确表明只有以下3个参数是可以在所有设备上保证支持的。
     */
    public static final int DEFAULT_SAMPLING_RATE = 44100;//模拟器仅支持从麦克风输入8kHz采样率
    public static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
    /**
     * 下面是对此的封装
     * private static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
     */
    public static final PCMFormat DEFAULT_AUDIO_FORMAT = PCMFormat.PCM_16BIT;

    //======================Lame Default Settings=====================
    public static final int DEFAULT_ENCODER_QUALITY = 6;
    /**
     * 与DEFAULT_CHANNEL_CONFIG相关，因为是mono单声，所以是1，如果是 AudioFormat.CHANNEL_IN_STEREO则为2
     */
    public static final int DEFAULT_ENCODER_IN_CHANNEL = 2;
    /**
     * Encoded bit rate. MP3 file will be encoded with bit rate 32kbps
     */
    public static final int DEFAULT_ENCODER_BIT_RATE = 128;
}
