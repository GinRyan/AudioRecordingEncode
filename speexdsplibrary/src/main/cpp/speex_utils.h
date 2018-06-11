//
// Created by 95 on 2018/6/11.
//

#ifndef AUDIORECORDINGENCODE_SPEEX_UTILS_H
#define AUDIORECORDINGENCODE_SPEEX_UTILS_H


#include "speex/speex_echo.h"
#include "speex/speex_jitter.h"
#include "speex/speex_preprocess.h"
#include "speex/speex_resampler.h"

//全量so包编译测试
void testInit() {
    speex_echo_state_init(0, 0);
    jitter_buffer_init(0);
    speex_preprocess_state_init(0, 0);
    speex_resampler_init(0, 0, 0, 0, 0);
}

#endif //AUDIORECORDINGENCODE_SPEEX_UTILS_H
