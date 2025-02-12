package indi.yolo.sample.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * golang代码编译成链接文件，通过JNA加载调用即可
 *
 * @author yoloz
 */
public class JNAGoSample {

    public interface GoLibrary extends Library {

        GoLibrary INSTANCE = Native.load("C:\\java\\libtest.dll", GoLibrary.class);

        int intMethod(int n);

        boolean booleanMethod(boolean bool);

        String stringMethod(String text);

        int intArrayMethod(int[] intArray);
    }

    public static void main(String[] args) {
        System.setProperty("jna.encoding", "UTF-8");
        GoLibrary goLibrary = GoLibrary.INSTANCE;

        int square = goLibrary.intMethod(5);
        boolean bool = goLibrary.booleanMethod(true);
        String text = goLibrary.stringMethod("java");
        int sum = goLibrary.intArrayMethod(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 13});

        System.out.println("intMethod: " + square);
        System.out.println("booleanMethod:" + bool);
        System.out.println("stringMethod:" + text);
        System.out.println("intArrayMethod:" + sum);
    }

}
