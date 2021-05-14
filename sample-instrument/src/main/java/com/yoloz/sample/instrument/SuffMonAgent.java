package com.yoloz.sample.instrument;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

/**
 * @author yoloz
 */
public class SuffMonAgent {

    // 编码agentmain
    public static void agentmain(String args, Instrumentation ins) {
        // 设置增强代码
        ins.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            System.out.println("reload class " + className);
            try {
                CtClass ctClass = ClassPool.getDefault().makeClass(new java.io.ByteArrayInputStream(classfileBuffer));
                System.out.println("********Transforming " + ctClass.getPackageName());
                CtMethod ctMethod = ctClass.getDeclaredMethod("test");
                ctMethod.addLocalVariable("stime", CtClass.longType);
                ctMethod.insertBefore("stime = System.nanoTime();");
                ctMethod.insertAfter("System.out.println(\"" + ctMethod.getLongName() + "\"" + "\" cost time:\" + (System.nanoTime() - stime));");
                return ctClass.toBytecode();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return classfileBuffer;
        }, true);
        try {
            // 重新转换类，以触发transform方法的调用
            ins.retransformClasses(Class.forName("com.yoloz.sample.instrument.App"));
        } catch (UnmodifiableClassException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
