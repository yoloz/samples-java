package indi.yolo.sample.jni;

/**
 * 直接调用C实现；调用C桥接golang实现
 *
 * @author yoloz
 */
public class JNISample {

    static {
//        System.loadLibrary("nativec");
        System.loadLibrary("nativecgo");
    }

    public static void main(String[] args) {

        NativeCFuc nmt = new NativeCFuc();

        int square = nmt.intMethod(5);
        boolean bool = nmt.booleanMethod(true);
        String text = nmt.stringMethod("java");

        System.out.println("intMethod: " + square);
        System.out.println("booleanMethod:" + bool);
        System.out.println("stringMethod:" + text);
    }
}

