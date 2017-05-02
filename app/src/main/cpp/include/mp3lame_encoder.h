//
// Created by Liang on 2017/5/2.
//

#ifndef AKATEAN_MP3LAMEPROXY_C_H
#define AKATEAN_MP3LAMEPROXY_C_H

#include "allneeded.h"

#define   LOG_TAG    "MP3_LAME"
#define   LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define   LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

/**
 * JNI接口：获取Lame版本信息
 */
JNIEXPORT jstring JNICALL
Java_org_xellossryan_lame_MP3Lame_version(JNIEnv *env, jobject instance);

/**
 * JNI接口：初始化lame
 */
JNIEXPORT jint JNICALL
Java_org_xellossryan_lame_MP3Lame_initLame(JNIEnv *env, jobject instance);

/**
 * JNI接口：初始化参数
 */
JNIEXPORT jint JNICALL
Java_org_xellossryan_lame_MP3Lame_initParameters(JNIEnv *env, jobject instance, jint inSampleRate,
                                                 jshort inChannels, jint outSampleRate,
                                                 jint outBitrate, jint quality);

/**
 * JNI接口：编码PCM音频帧到MP3
 */
JNIEXPORT jint JNICALL
Java_org_xellossryan_lame_MP3Lame_encode(JNIEnv *env, jobject instance, jshortArray bufferLeft_,
                                         jshortArray bufferRight_, jint nSamples,
                                         jbyteArray mp3buf_);

JNIEXPORT jint JNICALL
Java_org_xellossryan_lame_MP3Lame_flush(JNIEnv *env, jobject instance, jbyteArray mp3buf_) ;

JNIEXPORT jint JNICALL
Java_org_xellossryan_lame_MP3Lame_close(JNIEnv *env, jobject instance);

/**
 * 初始化lame参数
 * @param inSampleRate  输入音频采样率
 * @param inChannels  输入声道数
 * @param outSampleRate  输出采样率
 * @param outBitrate   输出比特率
 * @param quality    输出质量
 */

void init(int inSampleRate, short inChannels, int outSampleRate, int outBitrate, int quality);

/**
 * 初始化lame编解码器
 * @return  lame_global_flags结构体指针
 */
lame_global_flags *init_lame();

/**
 * 将PCM编码为mp3帧
 *
 * @param buffer_l PCM左声道缓冲数据
 * @param buffer_r PCM右声道缓冲数据
 * @param nsamples  每个声道的采样数
 * @param mp3buf  编码好的mp3流的指针
 * @param mp3buf_size  当前流的字节数
 * @return  -1为未初始化错误
 */
int encode(
        short buffer_l[],
        short buffer_r[],
        int nsamples,
        unsigned char *mp3buf,
        int mp3buf_size);


/**
 * flush 缓冲数据
 *
 * @param mp3buf
 * @param size
 * @return -1为未初始化错误
 */
int flush(unsigned char *mp3buf, int size);

/**
 * 关闭编码器
 * @return
 */
int close();



#endif //AKATEAN_MP3LAMEPROXY_C_H
