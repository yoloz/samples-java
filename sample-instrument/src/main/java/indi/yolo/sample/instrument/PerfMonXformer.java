package indi.yolo.sample.instrument;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;

/**
 * CtClass cl=xx
 * 添加Class字段：
 * CtField newCtField = new CtField(pool.getCtClass("java/lang/ThreadLocal"), "threadLocal", cl);
 * newCtField.setModifiers(Modifier.STATIC);
 * 字段附上注解：
 * final FieldInfo fieldInfo = newCtField.getFieldInfo();
 * AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
 * Annotation fieldAnno = new Annotation(NEW_FIELD_ANNOTATION_NAME, constPool);
 * fieldAnno.addMemberValue(NEW_FIELD_ANNOTATION_VALUE,new StringMemberValue(fieldWrapper.sheetColumnValue, constPool));
 * fieldAttr.addAnnotation(fieldAnno);
 * fieldInfo.addAttribute(fieldAttr);
 * 添加字段：
 * cl.addField(threadLocal, CtField.Initializer.byNew(pool.getCtClass("java/lang/ThreadLocal")));
 * 为字段添加getter和setter：
 * ctClass.addMethod(CtNewMethod.getter(getter(fieldName), newCtField));
 * ctClass.addMethod(CtNewMethod.setter(setter(fieldName), newCtField));
 */
public class PerfMonXformer implements ClassFileTransformer {
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        byte[] transformed = classfileBuffer;
        if (className == null || !className.startsWith("indi/yolo/sample/instrument")) {
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