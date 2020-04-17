package edu.utdallas;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class ClassTransformVisitor extends ClassVisitor implements Opcodes {
	
	//gets the name of class from somewhere
	private String classname;
	
    public ClassTransformVisitor(final ClassVisitor cv) {
        super(ASM5, cv);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name,
            final String desc, final String signature, final String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        return mv == null ? null : new MethodTransformVisitor(mv, name, classname);
    }
    
    //added the visit method just so we can get the classname
    @Override
    public void visit(int version, int access, String name, String signature,
    		String superName, String[] interfaces) {
    	super.visit(version, access, name, signature, superName, interfaces);
    	this.classname = name;
    }
}


