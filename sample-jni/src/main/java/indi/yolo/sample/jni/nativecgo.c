// gcc -shared -fPIC -I /JDK_PATH/include/ -I /JDK_PATH/include/linux/  -o libnativecgo.so nativecgo.c

#include "indi_yolo_sample_jni_NativeCFuc.h"
// 使用双引号""是告诉编译器在用户指定的目录（通常是当前目录或指定的源码目录）中查找文件
// add include path: /JDK_PATH/include/**
#include "golang/libnativego.h"
#include <string.h>

JNIEXPORT jint JNICALL Java_indi_yolo_sample_jni_NativeCFuc_intMethod(JNIEnv *env, jobject obj, jint i)
{
    int r = IntMethod(i);
    return r;
}

JNIEXPORT jboolean JNICALL Java_indi_yolo_sample_jni_NativeCFuc_booleanMethod(JNIEnv *env, jobject obj, jboolean b)
{
    int r = BooleanMethod(b);
    return r;
}

JNIEXPORT jstring JNICALL Java_indi_yolo_sample_jni_NativeCFuc_stringMethod(JNIEnv *env, jobject obj, jstring string)
{
    const char *str = (*env)->GetStringUTFChars(env, string, 0);
    char *ret = StringMethod((char *)str);
    (*env)->ReleaseStringUTFChars(env, string, 0);
    return (*env)->NewStringUTF(env, (char *)ret);
}

JNIEXPORT jint JNICALL Java_indi_yolo_sample_jni_NativeCFuc_intArrayMethod(JNIEnv *env, jobject obj, jintArray array)
{
    return 0;
}