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

        GoLibrary INSTANCE = Native.load("nativego", GoLibrary.class);

        int IntMethod(int n);

        boolean BooleanMethod(boolean bool);

        String StringMethod(String text);

    }

    public static void main(String[] args) {
        System.setProperty("jna.encoding", "UTF-8");
        GoLibrary goLibrary = GoLibrary.INSTANCE;

        int square = goLibrary.IntMethod(5);
        boolean bool = goLibrary.BooleanMethod(true);
        String text = goLibrary.StringMethod("java");

        System.out.println("intMethod: " + square);
        System.out.println("booleanMethod:" + bool);
        System.out.println("stringMethod:" + text);
    }

}
