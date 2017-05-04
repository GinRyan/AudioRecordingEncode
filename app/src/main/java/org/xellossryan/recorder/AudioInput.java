package org.xellossryan.recorder;

import android.media.AudioRecord;

import org.xellossryan.abstractlayer.EncoderAbstractLayer;
import org.xellossryan.log.L;

/**
 * 音频输入和编码线程
 * <p>
 * Created by Liang on 2017/5/3.
 */
public class AudioInput extends Thread {

    EncoderAbstractLayer encoder;

    AudioRecord audioRecorder;

    int audioSource;
    int sampleRateInHz;
    int channelConfig;
    int audioFormat;
    int bufferSizeInBytes;

    public AudioInput(EncoderAbstractLayer layer) {
        encoder = layer;
        audioSource = EncodeArguments.DEFAULT_AUDIO_SOURCE;
        sampleRateInHz = EncodeArguments.DEFAULT_SAMPLING_RATE;
        channelConfig = EncodeArguments.DEFAULT_CHANNEL_CONFIG;
        audioFormat = EncodeArguments.DEFAULT_AUDIO_FORMAT.getAudioFormat();
        encoder.initEncoder();

        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        //Initialize Audio Recorder
        // There should compute bufferSize per period per channel.
        L.d("bufferSizeInBytes: " + bufferSizeInBytes);
        audioRecorder = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes * 2);

    }

    public String version() {
        return encoder.version();
    }

    public int close() {
        return encoder.close();
    }

    @Override
    public void run() {
        super.run();
        //TODO Running Audio Recording

    }
}
