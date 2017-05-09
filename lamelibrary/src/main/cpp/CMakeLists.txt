# 如果需要了解更多关于android studio中使用CMake的信息，请查阅文档：
# https://d.android.com/studio/projects/add-native-code.html

# 设置CMake需求的最低版本
cmake_minimum_required(VERSION 3.4.1)

# 创建并且命名一个库，设置为STATIC或者SHARED，
# 并且提供其源代码的相对路径。
# 你可以定义多个库，然后CMake会相应的构建它们。
# Gradle 会自动在你的apk中打包共享库。

#设置本地库NATIVE_LIB变量
SET(NATIVE_LIB
        native-lib.c
        ctools.c )

SET(MP3LIB_SRC
            mp3lame_encoder.c)

#设置对应ABI的导入目录和库目录根
SET(LIBROOT ${PROJECT_SOURCE_DIR}/${ANDROID_ABI} )

#添加生成 mp3lame 库
add_library( # 设置库名称儿
             mp3lame

             # 设置这个库为共享库
             SHARED

             # 提供你的源代码的相对路径
             ${MP3LIB_SRC}

             )
add_library(utilslib
            SHARED
            ${NATIVE_LIB}
                )

#从LIBROOT包含导入目录
INCLUDE_DIRECTORIES(${PROJECT_SOURCE_DIR})
INCLUDE_DIRECTORIES(${PROJECT_SOURCE_DIR}/include)
INCLUDE_DIRECTORIES(${LIBROOT}/include)

#添加生成mp3lamelib库，该库是外部.a库文件导入
add_library(mp3lamelib  SHARED  IMPORTED)
set_target_properties( # 指定目标库
                       mp3lamelib

                       # 指定你要定义的参数
                       PROPERTIES IMPORTED_LOCATION

                       # 提供你要导入的库路径
                      ${LIBROOT}/lib/libmp3lame.a )


#寻到系统日志库log并命名为log-lib
find_library( # 设置路径变量名称
              log-lib

              # 指定你想让CMake定位的NDK内置库的名称
              log )

#关键！连接mp3lamelib库，连接androidlib和log-lib库
target_link_libraries( # 指定目标库
                       mp3lame

                       # 连接库到目标库
                       mp3lamelib

                       ${log-lib}

                       #链接系统库
                       android EGL GLESv1_CM GLESv2 dl m z
                       )

target_link_libraries( # 指定目标库
                       utilslib

                       # 连接库到目标库
                       ${log-lib}

                       #链接系统库
                       android EGL GLESv1_CM GLESv2 dl m z
                       )