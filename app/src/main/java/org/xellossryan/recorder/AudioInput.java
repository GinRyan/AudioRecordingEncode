package org.xellossryan.recorder;

import android.media.AudioRecord;
import android.support.annotation.NonNull;

import org.xellossryan.abstractlayer.EncoderAbstractLayer;
import org.xellossryan.log.L;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 音频输入和编码线程
 * <p>
 * Created by Liang on 2017/5/3.
 */
public class AudioInput extends Thread {

    private EncoderAbstractLayer encoder;

    private AudioRecord audioRecorder;

    private int audioSource = 1;//1 for mic
    private int sampleRateInHz = 44100;
    private int channelConfig = 12;//12 for stereo, 10 for mono
    private int audioFormat = 0;//3 for 8bit, 2 for 16bit
    private int bufferSizeInBytes = 0;//compute by AudioRecord.getMinBufferSize()

    private final AtomicBoolean isRecording = new AtomicBoolean(true);

    int minBufferSizeInShort = 0;

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
        audioRecorder = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);

        if (bufferSizeInBytes == AudioRecord.ERROR_BAD_VALUE) {
            L.e("audioSource:" + audioSource);
            L.e("sampleRateInHz:" + sampleRateInHz);
            L.e("channelConfig:" + channelConfig);
            L.e("audioFormat:" + audioFormat);
            throw new UnsupportedOperationException("当前参数无法初始化音频硬件");
        }

        if (audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
            // Because the bufferSize is byte units
            // short = byte * 2
            minBufferSizeInShort = bufferSizeInBytes / 2;
        }
    }

    @Override
    public void run() {
        super.run();
        try {
            audioRecorder.startRecording();
        } catch (IllegalStateException e) {
            L.e("===========Recording Failed!===========");
            e.printStackTrace();
            L.e(String.format("===========%s===========", e.getLocalizedMessage()));
            return;
        }
        output = new AudioOutput();
        output.play();
        while (isRecording.get()) {

            //In fact we shouldn't always allocate []buffer in WHILE loop. That will cost time and
            // increase memory use significantly.
            short[] buffer = new short[minBufferSizeInShort];
            int ret = read(buffer, 0, minBufferSizeInShort);
            L.i("INPUT:" + ret);
            if (ret != AudioRecord.ERROR_BAD_VALUE && ret != AudioRecord.ERROR_INVALID_OPERATION) {
                //only if right value that buffer can be used.
                //TODO Running Audio Recording
                L.i("OUTPUT:" + ret);
                output.write(buffer, 0, minBufferSizeInShort);
            }
        }
        output.stopPlaying();
        audioRecorder.stop();
    }

    AudioOutput output = null;


    public int read(@NonNull short[] audioData, int offsetInBytes, int sizeInBytes) {
        return audioRecorder.read(audioData, offsetInBytes, sizeInBytes);
    }

    public void release() {
        audioRecorder.release();
    }

    public void stopRecording() {
        isRecording.set(false);
    }

    public void startRecording() {
        isRecording.set(true);
        super.start();
    }

    @Deprecated
    @Override
    public synchronized void start() {
        throw new UnsupportedOperationException("Use startRecording() method!");
        //super.start();
    }

    public String version() {
        return encoder.version();
    }

    public int close() {
        return encoder.close();
    }

    public AudioInput setRecording(boolean recording) {
        isRecording.set(recording);
        return this;
    }

    public boolean isRecording() {
        return isRecording.get();
    }

}
