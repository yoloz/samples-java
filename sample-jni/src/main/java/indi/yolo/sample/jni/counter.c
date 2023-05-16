#include <jni.h>

// C函数需要比Java本地方法多出两个参数，这两个参数之后的参数列表与Java本地方法保持一致
// 第一个参数表示JNI环境，该环境封装了所有JNI的操作函数
// 第二个参数为Java代码中调用该C函数的对象
// 函数名格式: Java_包名_类名_Java方法名
jint Java_indi_yolo_sample_jni_Counter_addFromC(JNIEnv *env, jobject thiz, jint a, jint b)
{
    return a + b;
}