  
package edu.utdallas;
import java.io.IOException;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Value;

class ClassTransformVisitor extends ClassVisitor implements Opcodes {
	
	//gets the name of class from somewhere
	private String classname;
	private ClassNode classNode;
	private List<FieldNode> fieldNode;
	
    public ClassTransformVisitor(final ClassVisitor cv) {
        super(ASM5, cv);
        
        //this.classNode = new ClassNode();
    }

    @SuppressWarnings("unchecked")
	@Override
    public MethodVisitor visitMethod(final int access, final String name,
            final String desc, final String signature, final String[] exceptions) {
    	
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        Type methodType = Type.getMethodType(desc);
        this.fieldNode=classNode.fields;
        List<LocalVariableNode> localVariableNames = null;
        for (final MethodNode mn : (List<MethodNode>)classNode.methods) {
            if ( mn.name.equals(name)) {
            	localVariableNames = mn.localVariables;
            }
        }
        return mv == null ? null : new MethodTransformVisitor(mv, access, name, classname, desc, 
        		methodType.getArgumentTypes(), localVariableNames,fieldNode);
    }
    
    //added the visit method just so we can get the classname
    @Override
    public void visit(int version, int access, String name, String signature,
    		String superName, String[] interfaces) {
    	super.visit(version, access, name, signature, superName, interfaces);
    	this.classname = name;
    	this.classNode = new ClassNode();
    	
		try {
			ClassReader reader = new ClassReader(classname);
			reader.accept(classNode, 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("didnt work");
		}
    }
}