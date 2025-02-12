package indi.yolo.sample.jni;

/**
 * cd src/main/java
 * /opt/jdk8/bin/javac indi/yolo/sample/jni/NativeMethodTest.java
 * /opt/jdk8/bin/javah indi.yolo.sample.jni.NativeMethodTest
 * mv indi_yolo_sample_jni_NativeMethodTest.h indi/yolo/sample/jni/
 *
 * @author yoloz
 */
public class NativeMethodTest {

    public native int intMethod(int n);

    public native boolean booleanMethod(boolean bool);

    public native String stringMethod(String text);

    public native int intArrayMethod(int[] intArray);
}
