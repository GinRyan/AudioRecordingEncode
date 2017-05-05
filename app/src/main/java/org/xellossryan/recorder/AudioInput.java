package org.xellossryan.recorder;

import android.media.AudioRecord;
import android.support.annotation.NonNull;

import org.xellossryan.lame.MP3Lame;
import org.xellossryan.log.L;
import org.xellossryan.output.FrameEncodeQueue;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 音频输入和编码线程
 * <p>
 * Created by Liang on 2017/5/3.
 */
public class AudioInput extends Thread {
    FrameEncodeQueue encodeQueue;

    private AudioRecord audioRecorder;

    private int bufferSizeInBytes = 0;//compute by AudioRecord.getMinBufferSize()

    private final AtomicBoolean isRecording = new AtomicBoolean(true);

    private int audioSource = 1;//1 for mic
    private int audioFormat = 0;//3 for 8bit, 2 for 16bit
    private int sampleRateInHz = 44100;
    private int channelConfig = 12;//12 for stereo, 10 for mono

    int minBufferSizeInShort = 0;

    public AudioInput(FrameEncodeQueue encodeQueue) {
        this.encodeQueue = encodeQueue;


        //Initialize Audio Recorder
        // There should compute bufferSizeInBytes per period per channel.
        L.d("bufferSizeInBytes: " + bufferSizeInBytes);

        audioSource = EncodeArguments.DEFAULT_AUDIO_SOURCE;
        sampleRateInHz = EncodeArguments.DEFAULT_SAMPLING_RATE;
        channelConfig = EncodeArguments.DEFAULT_CHANNEL_CONFIG;
        audioFormat = EncodeArguments.DEFAULT_AUDIO_FORMAT.getAudioFormat();

        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);

        audioRecorder = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);

        encodeQueue.preparePool(bufferSizeInBytes);

        if (bufferSizeInBytes == AudioRecord.ERROR_BAD_VALUE) {
            L.e("audioSource:" + audioSource);
            L.e("sampleRateInHz:" + sampleRateInHz);
            L.e("channelConfig:" + channelConfig);
            L.e("audioFormat:" + audioFormat);
            throw new UnsupportedOperationException("当前参数无法初始化音频硬件");
        }

        if (audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
            // Because the bufferSizeInBytes is byte units
            // short = byte * 2, if not ,there will be data overflow.
            minBufferSizeInShort = bufferSizeInBytes / 2;
            //TODO
            bufferSizeInBytes = minBufferSizeInShort;

        }
    }

    @Override
    public void run() {
        super.run();
        try {
            audioRecorder.startRecording();

            byte[] encodedBuffer = MP3Lame.allocateBuffer(bufferSizeInBytes);
            L.i(" encodedBuffer Length:" + encodedBuffer.length);
            while (isRecording.get()) {
                //In fact we shouldn't always allocate []pcmBuffer in WHILE loop. That will cost time and
                // increase memory use significantly.
                short[] buffer = new short[minBufferSizeInShort];
                //TODO There , we can borrow a bufferFrame to fill PCM data.instead of new short[minBufferSizeInShort]
                int ret = read(buffer, 0, minBufferSizeInShort);

                if (ret != AudioRecord.ERROR_BAD_VALUE && ret != AudioRecord.ERROR_INVALID_OPERATION) {
                    //only if right value that pcmBuffer can be used.
                    //Add to the encode queue
                    encodeQueue.addInQueue(buffer, encodedBuffer, bufferSizeInBytes);
                    L.i("INPUT: " + ret + "  BufferSizeInBytes: " + bufferSizeInBytes + " BufferSizeInShorts: " + minBufferSizeInShort);
                }
            }
            audioRecorder.stop();
            encodeQueue.setStopEncoding();
        } catch (IllegalStateException e) {
            L.e("===========Recording Failed!===========");
            e.printStackTrace();
            L.e(String.format("===========%s===========", e.getLocalizedMessage()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        encodeQueue.start();
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
        return encodeQueue.version();
    }

    public int close() {
        return encodeQueue.close();
    }

    public AudioInput setRecording(boolean recording) {
        isRecording.set(recording);
        return this;
    }

    public boolean isRecording() {
        return isRecording.get();
    }

}
