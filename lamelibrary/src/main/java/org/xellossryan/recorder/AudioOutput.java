package org.xellossryan.recorder;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.annotation.NonNull;

/**
 * 音频输出线程
 * <p>
 * Created by Liang on 2017/5/3.
 */
public class AudioOutput extends Thread {
    AudioTrack track;

    int audioSource;
    int sampleRateInHz;
    int channelConfig;
    int audioFormat;
    int bufferSizeInBytes;


    public AudioOutput() {
        audioSource = EncodeArguments.DEFAULT_AUDIO_SOURCE;
        sampleRateInHz = EncodeArguments.DEFAULT_SAMPLING_RATE;
        channelConfig = EncodeArguments.DEFAULT_CHANNEL_CONFIG;
        audioFormat = EncodeArguments.DEFAULT_AUDIO_FORMAT.getAudioFormat();
        bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);

        track = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes, AudioTrack.MODE_STREAM);
    }

    @Override
    public void run() {
        super.run();
        //TODO Running audio track output playing
    }


    public void release() {
        track.release();
    }

    public void play() throws IllegalStateException {
        track.play();
    }

    public void stopPlaying() {
        track.stop();
    }

    public void pause() throws IllegalStateException {
        track.pause();
    }

    public int write(@NonNull short[] audioData, int offsetInBytes, int sizeInBytes) {
        return track.write(audioData, offsetInBytes, sizeInBytes);
    }
}
