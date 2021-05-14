package com.yoloz.sample.instrument;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

/**
 * java -javaagent:sample-instrument-1.0.jar -cp sample-instrument-1.0.jar:javassist-3.27.0-GA.jar com.yoloz.sample.instrument.App
 *
 * @author yoloz
 */
public class PerfMonAgent {

    /**
     * This method is called before the application’s main-method is called,
     * when this agent is specified to the Java VM.
     **/
    public static void premain(String agentArgs, Instrumentation _inst) {
        System.out.println("PerfMonAgent.premain() was called.");
        // Initialize the static variables we use to track information.
        // Set up the class-file transformer.
        ClassFileTransformer trans = new PerfMonXformer();
        System.out.println("Adding a PerfMonXformer instance to the JVM.");
        _inst.addTransformer(trans);
    }
}
