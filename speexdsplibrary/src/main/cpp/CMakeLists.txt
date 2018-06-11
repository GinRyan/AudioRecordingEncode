# 如果需要了解更多关于android studio中使用CMake的信息，请查阅文档：
# https://d.android.com/studio/projects/add-native-code.html

# 设置CMake需求的最低版本
cmake_minimum_required(VERSION 3.4.1)

#设置对接项目源代码
SET(SPEEX_DSP_UTILS speex_utils.c)

#SpeexDSP的库根目录
SET(SPEEX_LIBROOT ${PROJECT_SOURCE_DIR}/speexdsp_${ANDROID_ABI} )

#包含进来有关的头文件所在目录
INCLUDE_DIRECTORIES(${PROJECT_SOURCE_DIR})
INCLUDE_DIRECTORIES(${PROJECT_SOURCE_DIR}/include)
INCLUDE_DIRECTORIES(${SPEEX_LIBROOT}/include)

#将原文件定义为dsplib模块
add_library(dsplib SHARED ${SPEEX_DSP_UTILS})

#定义一个导入库libspeexdsp
add_library(libspeexdsp  SHARED  IMPORTED)
#指定外部.a库文件路径
set_target_properties( # 指定目标库名称
                       libspeexdsp

                       # 指定你要定义的参数
                       PROPERTIES IMPORTED_LOCATION

                       # 提供你要导入的库路径
                      ${SPEEX_LIBROOT}/lib/libspeexdsp.a )

target_link_libraries( # 指定目标库
                       dsplib

                       # 连接库到目标库
                       libspeexdsp

                       #链接系统库
                       log android EGL GLESv1_CM GLESv2 dl m z
                       )