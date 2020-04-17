package edu.utdallas;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class Agent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Java Agent is running");
        inst.addTransformer(new ClassFileTransformer() {
            public byte[] transform(ClassLoader classLoader, String className, Class<?> aClass, ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {
                //System.out.println("transforming " + className);
                // ASM Code
                if (className.startsWith("org/java_websocket") ||
                        className.startsWith("org/jfree/chart") ||
                        className.startsWith("com/github/maven_nar") ||
                        className.startsWith("nodebox") ||
                        className.startsWith("spoon") ||
                        className.startsWith("org/mybatis") ||
                        className.startsWith("org/perf4j") ||
                        className.startsWith("com/skype") ||
                        className.startsWith("org/springframework/data/jpa/convert") ||
                        className.startsWith("net/sourceforge/pebble")
                        
                        || className.startsWith("org/springframework") ||
                        className.startsWith("org/zeroturnaround/exec")
                ){
                    //System.out.println(className);
                    ClassReader cr = new ClassReader(bytes);
                    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                    ClassTransformVisitor ca = new ClassTransformVisitor(cw);
                    cr.accept(ca, 0);
                    return cw.toByteArray();
                }
                return bytes;
            }
        });
    }
}