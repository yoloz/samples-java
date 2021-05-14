package com.yoloz.sample.instrument;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;

public class PerfMonXformer implements ClassFileTransformer {
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        byte[] transformed = classfileBuffer;
        if (className == null || !className.startsWith("com/yoloz/sample/instrument")) {
            return transformed;
        }
        System.out.println("Transforming " + className);
        ClassPool pool = ClassPool.getDefault();
        CtClass cl = null;
        try {
            cl = pool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));
            if (!cl.isInterface()) {
                CtBehavior[] methods = cl.getDeclaredBehaviors();
                for (CtBehavior method : methods) {
                    if (!method.isEmpty()) {
                        method.addLocalVariable("stime", CtClass.longType);
                        method.insertBefore("stime = System.nanoTime();");
                        method.insertAfter("System.out.println(\"" + method.getLongName() + "\"" + "\" cost time:\" + (System.nanoTime() - stime));");
                    }
                }
                transformed = cl.toBytecode();
            }
        } catch (Exception e) {
            System.err.println("Could not instrument  " + className + ",  exception : " + e);
        } finally {
            if (cl != null) {
                cl.detach();
            }
        }
        return transformed;
    }
}