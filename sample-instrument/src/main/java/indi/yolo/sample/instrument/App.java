package indi.yolo.sample.instrument;

import java.util.concurrent.TimeUnit;

/**
 * 方法前后添加耗时
 *
 * @author yoloz
 */
public class App {

    public static void main(String[] args) {
        App app = new App();
        int i = 0;
        while (i < 1000) {
            app.test();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {
            }
            i++;
        }
    }

    public void test() {
        System.out.println("Hello World!!");
    }
}
