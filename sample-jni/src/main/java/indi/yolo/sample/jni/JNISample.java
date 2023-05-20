package indi.yolo.sample.jni;

/**
 * @author yoloz
 */
public class JNISample {

    static {
        System.loadLibrary("nativemethod");
    }

    public static void main(String[] args) {

        NativeMethodTest nmt = new NativeMethodTest();

        int square = nmt.intMethod(5);
        boolean bool = nmt.booleanMethod(true);
        String text = nmt.stringMethod("java");
        int sum = nmt.intArrayMethod(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 13});

        System.out.println("intMethod: " + square);
        System.out.println("booleanMethod:" + bool);
        System.out.println("stringMethod:" + text);
        System.out.println("intArrayMethod:" + sum);
    }
}

