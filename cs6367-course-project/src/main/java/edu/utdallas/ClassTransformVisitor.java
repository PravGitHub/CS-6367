  
package edu.utdallas;
import java.io.IOException;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

class ClassTransformVisitor extends ClassVisitor implements Opcodes {
	
	//gets the name of class from somewhere
	private String classname;
	private ClassNode classNode;
	
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
        
        List<LocalVariableNode> localVariableNames = null;
        //System.out.println("before loop: className is " + classNode.name);
        for (final MethodNode mn : (List<MethodNode>)classNode.methods) {
            if ( mn.name.equals(name)) {
                //System.out.println("mn.localVariables.size():" +  mn.localVariables.size());
                /*localVariableNames = new String[mn.localVariables.size()];
                for (LocalVariableNode n : (List<LocalVariableNode>) mn.localVariables) {
                	n.
                    localVariableNames[n.index] = n.name;
                }
                */
            	localVariableNames = mn.localVariables;
            }
        }
        return mv == null ? null : new MethodTransformVisitor(mv, access, name, classname, desc, 
        		methodType.getArgumentTypes(), localVariableNames);
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