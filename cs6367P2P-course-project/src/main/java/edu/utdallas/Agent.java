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
                // ASM Code
                if (className.startsWith("org/apache/commons/dbutils") ||
                		className.startsWith("nodebox") ||
                        className.startsWith("org/zeroturnaround/exec") ||
                        className.startsWith("com/github/maven_nar") ||
                        className.startsWith("org/jfree/chart") ||
                        className.startsWith("org/java_websocket") ||                        
                        className.startsWith("smartrics/rest/fitnesse/fixture") ||                        
                        className.startsWith("net/masterthought/cucumber") ||
                        className.startsWith("org/ansj") ||
                        className.startsWith("org/apache/commons/codec") ||                       
                        className.startsWith("com/laytonsmith")
                ){
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