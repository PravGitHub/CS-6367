package edu.utdallas;


import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.sun.org.apache.xpath.internal.operations.Variable;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

class MethodTransformVisitor extends MethodVisitor implements Opcodes {

	String mName;
	String className;	//name of the class
	int line;
	int acc;
	String sig;
	Type[] types;
	int len;
	
    public MethodTransformVisitor(final MethodVisitor mv, String mname, String className, String sig, int acc, Type[] types) {
        super(ASM5, mv);
        this.mName=mname;
        this.className = className;
        this.sig = sig;
        this.acc = acc;
        this.types = types;
        this.len=types.length;
		this.line = 0;	//when we enter a new method, there is no previous line that we been to in that method.
    }

   
	
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
	

    @Override
    public void visitCode(){
    	
        // avoid construct method
        if(!mName.equals("<init>")) {
            System.out.println(className + "." + mName + "." + sig + ": len = " + len);
          
            // set the starting index, if the method is static starting from 0,
            // otherwise starting from 1
            int i = 1;
            if((this.acc & Opcodes.ACC_STATIC) != 0){
                System.out.println("in visitCode " + className + "/" + mName + "/" + sig + "is a static method");
                i = 0;
            }
            for (Type tp : types) {
                System.out.println("tp.getClassName() = " + tp.getClassName());
                if (tp.equals(Type.BOOLEAN_TYPE)) {
                    mv.visitVarInsn(Opcodes.ILOAD, i);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
                } else if (tp.equals(Type.BYTE_TYPE)) {
                    mv.visitVarInsn(Opcodes.ILOAD, i);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
                } else if (tp.equals(Type.CHAR_TYPE)) {
                    mv.visitVarInsn(Opcodes.ILOAD, i);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
                } else if (tp.equals(Type.SHORT_TYPE)) {
                    mv.visitVarInsn(Opcodes.ILOAD, i);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
                } else if (tp.equals(Type.INT_TYPE)) {
                    mv.visitVarInsn(Opcodes.ILOAD, i);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                } else if (tp.equals(Type.LONG_TYPE)) {
                    mv.visitVarInsn(Opcodes.LLOAD, i);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                    // long may need two indices
                    i++;
                } else if (tp.equals(Type.FLOAT_TYPE)) {
                    mv.visitVarInsn(Opcodes.FLOAD, i);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
                } else if (tp.equals(Type.DOUBLE_TYPE)) {
                    mv.visitVarInsn(Opcodes.DLOAD, i);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                    // double may need two indices
                    i++;
                } else
                    mv.visitVarInsn(Opcodes.ALOAD, i);
                mv.visitLdcInsn(className);
                mv.visitLdcInsn(mName);
                mv.visitLdcInsn(sig);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "edu/utd/DataCoverage", "addVariable", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false);
                i++;
            }
        }
        super.visitCode();
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index){
        if("this".equals(name)){
            //DataTraceCollection.staticmap.put(className + "/" + methodName + "/" + this.signature, false);
            //System.out.println(className + "/" + methodName + "/" + this.signature + "is a virtual method");
        } else if(len-- > 0) {
            String s = className + "/." + mName + "/." +name+"/."+desc+"/."+ signature+"/."+start+"/."+end+"/."+index;
            
            //Variable v = new Variable(Type.getType(desc), name, index);
            DataCoverage.op.add(s);
        }
        super.visitLocalVariable(name,desc,signature,start,end,index);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }

}