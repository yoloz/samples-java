package indi.yolo.sample.jni;

/**
 * @author yoloz
 */
public class NativeMethodTest {

    public native int intMethod(int n);

    public native boolean booleanMethod(boolean bool);

    public native String stringMethod(String text);

    public native int intArrayMethod(int[] intArray);
}
