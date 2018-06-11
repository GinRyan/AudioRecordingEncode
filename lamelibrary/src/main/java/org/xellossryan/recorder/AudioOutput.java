package org.xellossryan.recorder;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.audiofx.NoiseSuppressor;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * 音频输出线程
 * <p>
 * Created by Liang on 2017/5/3.
 */
public class AudioOutput {
    AudioTrack track;

    int audioSource;
    int sampleRateInHz;
    int channelConfig;
    int audioFormat;
    int bufferSizeInBytes;

    int audioSessionId;

    public AudioOutput() {
        audioSource = EncodeArguments.DEFAULT_AUDIO_SOURCE;
        sampleRateInHz = EncodeArguments.DEFAULT_SAMPLING_RATE;
        channelConfig = EncodeArguments.DEFAULT_CHANNEL_CONFIG;
        audioFormat = EncodeArguments.DEFAULT_AUDIO_FORMAT.getAudioFormat();
        bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);

        track = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes, AudioTrack.MODE_STREAM);
        audioSessionId = track.getAudioSessionId();


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
