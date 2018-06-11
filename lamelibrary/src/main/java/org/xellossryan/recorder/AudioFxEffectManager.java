package org.xellossryan.recorder;

import android.media.audiofx.AudioEffect;
import android.util.Log;

public class AudioFxEffectManager {


    public static void enableAudioEffect(AudioEffect effect) {
        //音频效果启用
        if (effect != null) {
            boolean effectEnabled = false;
            effectEnabled = effect.getEnabled();
            Log.i("AudioFx", "AudioFx: " + effect.getDescriptor().name + " Enabled:" + effectEnabled);
            effect.setEnabled(true);
            effect.setEnableStatusListener(new AudioEffect.OnEnableStatusChangeListener() {
                @Override
                public void onEnableStatusChange(AudioEffect effect, boolean enabled) {
                    Log.i("AudioFx", "AudioFx: " + effect.getDescriptor().name + " Enabled:" + enabled + " AudioEffect:" + effect);
                }
            });
        } else {
            Log.i("AudioFx", "AudioFx: AudioEffect is NULL");
        }
    }

}
