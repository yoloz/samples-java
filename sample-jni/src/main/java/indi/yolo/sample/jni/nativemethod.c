//gcc -shared -fPIC -I /JDK_PATH/include/ -I /JDK_PATH/include/linux/  -o libnativemethod.so nativemethod.c

#include "indi_yolo_sample_jni_NativeMethodTest.h"
// 使用双引号""是告诉编译器在用户指定的目录（通常是当前目录或指定的源码目录）中查找文件
// add include path: /JDK_PATH/include/**
#include <string.h>

void my_strupr(char str[])
{
    int nNum;
    nNum = strlen(str);
    for (int i = 0; i < nNum; i++)
    {
        if (str[i] >= 'a' && str[i] <= 'z')
        {
            str[i] -= 32;
        }
    }
}

JNIEXPORT jint JNICALL Java_indi_yolo_sample_jni_NativeMethodTest_intMethod(JNIEnv *env, jobject obj, jint i)
{
    return i * i;
}

JNIEXPORT jboolean JNICALL Java_indi_yolo_sample_jni_NativeMethodTest_booleanMethod(JNIEnv *env, jobject obj, jboolean b)
{
    return !b;
}

JNIEXPORT jstring JNICALL Java_indi_yolo_sample_jni_NativeMethodTest_stringMethod(JNIEnv *env, jobject obj, jstring string)
{
    const char *str = (*env)->GetStringUTFChars(env, string, 0);
    char cap[128];
    strcpy(cap, str);
    (*env)->ReleaseStringUTFChars(env, string, 0);
    //    strupr:将字符串s转换为大写形式,不是标准C库函数，只能在window(VC,MinGW)中使用,在linux gcc环境下需要自行定义这个函数。
    // return (*env)->NewStringUTF(env, strupr(cap));
    my_strupr(cap);
    return (*env)->NewStringUTF(env, cap);
}

JNIEXPORT jint JNICALL Java_indi_yolo_sample_jni_NativeMethodTest_intArrayMethod(JNIEnv *env, jobject obj, jintArray array)
{
    int i, sum = 0;
    jsize len = (*env)->GetArrayLength(env, array);
    jint *body = (*env)->GetIntArrayElements(env, array, 0);

    for (i = 0; i < len; i++)
    {
        sum += body[i];
    }
    (*env)->ReleaseIntArrayElements(env, array, body, 0);
    return sum;
}