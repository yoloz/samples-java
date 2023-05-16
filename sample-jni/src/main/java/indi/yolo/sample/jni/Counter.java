package indi.yolo.sample.jni;

/**
 * @author yoloz
 */
public class Counter {
    static {
        System.loadLibrary("counter");
//        System.load("/home/yoloz/link/counter.so");
    }

    // 声明本地方法
    public static native int addFromC(int a, int b);

    public static void main(String[] argv) {
        // 调用本地方法
        System.out.println("1 + 2 = " + addFromC(1, 2));
    }
}
