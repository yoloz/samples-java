package indi.yolo.sample.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * @author yoloz
 */
public class JNASample {

    public interface CLibrary extends Library {

        //CLibrary INSTANCE = Native.load((Platform.isWindows() ? "msvcrt" : "c"), CLibrary.class);
        //加载nativemethod，并使用其中的函数
        CLibrary INSTANCE = Native.load("nativemethod", CLibrary.class);

        int intMethod(int n);

        boolean booleanMethod(boolean bool);

        String stringMethod(String text);

        int intArrayMethod(int[] intArray);
    }

    public static void main(String[] args) {
        CLibrary cLibrary = CLibrary.INSTANCE;
        int square = cLibrary.intMethod(5);
        boolean bool = cLibrary.booleanMethod(true);
        String text = cLibrary.stringMethod("java");
        int sum = cLibrary.intArrayMethod(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 13});

        System.out.println("intMethod: " + square);
        System.out.println("booleanMethod:" + bool);
        System.out.println("stringMethod:" + text);
        System.out.println("intArrayMethod:" + sum);
    }

}
