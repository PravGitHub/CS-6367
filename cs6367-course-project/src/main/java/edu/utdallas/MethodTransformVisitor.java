package edu.utdallas;


import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


class MethodTransformVisitor extends MethodVisitor implements Opcodes {

	String mName;
	String className;	//name of the class
	int line;
	
    public MethodTransformVisitor(final MethodVisitor mv, String name, String className) {
        super(ASM5, mv);
        this.mName=name;
        this.className = className;
		this.line = 0;	//when we enter a new method, there is no previous line that we been to in that method.
    }

    // method coverage collection
    /*@Override
    public void visitCode(){
    	mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
    	mv.visitLdcInsn(mName+" executed");
    	mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    	super.visitCode();
    }*/
	
    @Override	//given from the hint
    public void visitLineNumber(final int lineNo, final Label start) {
    	
    	//calls onto the getLineNumbers function in JUnitExecutionListener
		this.line = lineNo;	//stores line we have entered
		//System.out.println("inside MTV vLN");
		mv.visitLdcInsn(className);
		mv.visitLdcInsn(new Integer(line));
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
		mv.visitMethodInsn(INVOKESTATIC, "edu/utdallas/ControlFlowCoverage", "getLineNumbers", 
    			"(Ljava/lang/String;Ljava/lang/Integer;)V",false);
		super.visitLineNumber(lineNo, start);
    }
	
	/*@Override	//given from the hint
	public void visitLabel(final Label start) {
		//calls onto the getLineNumbers function in JUnitExecutionListener
		mv.visitLdcInsn(className);
		mv.visitLdcInsn(new Integer(this.line));
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
		mv.visitMethodInsn(INVOKESTATIC, "edu/utdallas/ControlFlowCoverage", "getLineNumbers", 
    			"(Ljava/lang/String;Ljava/lang/Integer;)V",false);

		super.visitLabel(start);
	}*/

}