//
// Created by Liang on 2017/5/2.
//

#include "mp3lame_encoder.h"

static lame_global_flags *lameGlobal;

JNIEXPORT jstring JNICALL
Java_org_xellossryan_lame_MP3Lame_version(JNIEnv *env, jobject instance) {
    const char *lameVersion = get_lame_version();
    const char *lameUrl = get_lame_url();
    const char *lameOsBit = get_lame_os_bitness();

   // int len = strlen(lameVersion) + strlen(lameOsBit) + 10;
    char versionStr[60] = "";

    LOGE("lameVersion: %s", lameVersion);
    LOGI("lameUrl: %s \nlameOsBit: %s", lameUrl, lameOsBit);

    strcat(versionStr, lameVersion);
    strcat(versionStr, "_");
    strcat(versionStr, lameOsBit);
    return (*env)->NewStringUTF(env, versionStr);
}

JNIEXPORT jint JNICALL
Java_org_xellossryan_lame_MP3Lame_initLame(JNIEnv *env, jobject instance) {
    lameGlobal = init_lame();
    if (lameGlobal != NULL) {
        LOGI("init lame success!");
        return 0;
    } else {
        LOGE("init lame failed!");
        return -1;
    }
}

JNIEXPORT jint JNICALL
Java_org_xellossryan_lame_MP3Lame_initParameters(JNIEnv *env, jobject instance, jint inSampleRate,
                                                 jint inChannels, jint outSampleRate,
                                                 jint outBitrate, jint quality) {
    init(inSampleRate, inChannels, outSampleRate, outBitrate, quality);
    return 0;
}


JNIEXPORT jint JNICALL
Java_org_xellossryan_lame_MP3Lame_encode(JNIEnv *env, jobject instance, jshortArray bufferLeft_,
                                         jshortArray bufferRight_, jint nSamples,
                                         jbyteArray mp3buf_) {
    jshort *bufferLeft = (*env)->GetShortArrayElements(env, bufferLeft_, NULL);
    jshort *bufferRight = (*env)->GetShortArrayElements(env, bufferRight_, NULL);
    jbyte *mp3buf = (*env)->GetByteArrayElements(env, mp3buf_, NULL);
    jsize mp3BufSize = (*env)->GetArrayLength(env, mp3buf);

    int ret = encode(bufferLeft, bufferRight, nSamples, mp3buf, mp3BufSize);

    (*env)->ReleaseShortArrayElements(env, bufferLeft_, bufferLeft, 0);
    (*env)->ReleaseShortArrayElements(env, bufferRight_, bufferRight, 0);
    (*env)->ReleaseByteArrayElements(env, mp3buf_, mp3buf, 0);
    return ret;
}


JNIEXPORT jint JNICALL
Java_org_xellossryan_lame_MP3Lame_flush(JNIEnv *env, jobject instance, jbyteArray mp3buf_) {
    jbyte *mp3buf = (*env)->GetByteArrayElements(env, mp3buf_, NULL);
    jsize mp3BufSize = (*env)->GetArrayLength(env, mp3buf);

    int ret = flush(mp3buf, mp3BufSize);

    (*env)->ReleaseByteArrayElements(env, mp3buf_, mp3buf, 0);
    return ret;
}


JNIEXPORT jint JNICALL
Java_org_xellossryan_lame_MP3Lame_encodeInterleaved(JNIEnv *env, jobject instance,
                                                    jshortArray bufferIn_, jint nSamples,
                                                    jbyteArray mp3buf_) {
    jshort *bufferIn = (*env)->GetShortArrayElements(env, bufferIn_, NULL);
    jbyte *mp3buf = (*env)->GetByteArrayElements(env, mp3buf_, NULL);
    jsize mp3BufSize = (*env)->GetArrayLength(env, mp3buf);

    int ret = encode_interleave(bufferIn,  nSamples, mp3buf, mp3BufSize);

    (*env)->ReleaseShortArrayElements(env, bufferIn_, bufferIn, 0);
    (*env)->ReleaseByteArrayElements(env, mp3buf_, mp3buf, 0);
    return ret;
}

JNIEXPORT jint JNICALL
Java_org_xellossryan_lame_MP3Lame_close(JNIEnv *env, jobject instance) {
    return close();
}


//////////////////////////////////////////////////////////

/**
 * 初始化lame对象
 * @return
 */
lame_global_flags *init_lame() {
    if (lameGlobal != NULL) {
        lame_close(lameGlobal);
        lameGlobal = NULL;
    }
    lameGlobal = lame_init();
    return lameGlobal;
}


/**
 * 初始化lame参数
 * @param inSampleRate 输入采样率
 * @param inChannels 声道数
 * @param outSampleRate 输出采样率
 * @param outBitrate 输出比特率
 * @param quality 输出质量
 */
void init(int inSampleRate, int inChannels, int outSampleRate, int outBitrate, int quality) {
    if (lameGlobal == NULL) {
        LOGE(" === ERROR: lame not loaded === ");
        return;
    }
    lame_set_in_samplerate(lameGlobal, inSampleRate);
    lame_set_num_channels(lameGlobal, inChannels);
    lame_set_out_samplerate(lameGlobal, outSampleRate);
    lame_set_brate(lameGlobal, outBitrate);
    lame_set_quality(lameGlobal, quality);
    lame_init_params(lameGlobal);
}


/**
 * 将PCM编码为mp3帧
 *
 * @param buffer_l      PCM左声道缓冲数据
 * @param buffer_r      PCM右声道缓冲数据
 * @param nsamples      每个声道的采样数
 * @param mp3buf        编码好的mp3流的指针
 * @param mp3buf_size   当前流的字节数
 * @return              -1为未初始化错误
 */
int encode(
        short buffer_l[],
        short buffer_r[],
        int nsamples,
        unsigned char *mp3buf,
        int mp3buf_size) {
    if (lameGlobal == NULL) {
        LOGE(" === ERROR: lame not loaded === ");
        return -1;
    }
    return lame_encode_buffer(lameGlobal, buffer_l, buffer_r, nsamples, mp3buf, mp3buf_size);
}
/**
 * 将交错编码的PCM编码为mp3帧
 *
 * @param bufferIn  PCM缓冲数据
 * @param nsamples  采样数
 * @param mp3buf  编码好的mp3流的指针
 * @param mp3buf_size  当前流的字节数
 * @return  -1为lame未初始化错误
 */
int encode_interleave(short bufferIn[],
                      int nsamples,
                      unsigned char *mp3buf,
                      int mp3buf_size) {
    if (lameGlobal == NULL) {
        LOGE(" === ERROR: lame not loaded === ");
        return -1;
    }
    return lame_encode_buffer_interleaved(lameGlobal, bufferIn, nsamples, mp3buf, mp3buf_size);
}

/**
 * flush 缓冲数据
 *
 * @param mp3buf
 * @param size
 * @return -1为未初始化错误
 */
int flush(unsigned char *mp3buf, int size) {
    if (lameGlobal == NULL) {
        LOGE(" === ERROR: lame not loaded === ");
        return -1;
    }
    return lame_encode_flush(lameGlobal, mp3buf, size);
}

/**
 * 关闭lame
 *
 * @return
 */
int close() {
    lame_close(lameGlobal);
    lameGlobal = NULL;
    return 0;
}
