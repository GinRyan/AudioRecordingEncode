//
// Created by Liang on 2017/4/27.
//

#ifndef AKATEAN_TOOLS_H
#define AKATEAN_TOOLS_H

#include "allneeded.h"

#define   LOG_TAG    "CTOOLS"
#define   LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define   LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

typedef struct ElementaryMathStruct {
    int varA;
    int varB;
} ElementaryMath;


typedef struct fact {
    int a;
    int b;
} Fact;

extern int plus(int a,int b);

extern int plusFact(Fact *fact1);


#endif //AKATEAN_TOOLS_H
