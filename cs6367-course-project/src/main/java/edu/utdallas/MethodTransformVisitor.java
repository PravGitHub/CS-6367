package edu.utdallas;


import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;


class MethodTransformVisitor extends MethodVisitor implements Opcodes {

	private String methodName;	//name of the method
	private String className;	//name of the class
	private int access;
	private String desc;
	private String signature;
	private int line;
	
	private Type[] paramTypes;
	private List<LocalVariableNode> variableNames;
	
    public MethodTransformVisitor(final MethodVisitor mv, int access, String methodName, String className,
    		String desc, Type[] paramTypes, List<LocalVariableNode> variableNames) {
        super(ASM5, mv);
        this.methodName = methodName;
        this.className = className;
        this.access = access;
        this.desc = desc;
        this.paramTypes = paramTypes;
        this.variableNames = variableNames;
		this.line = 0;	//when we enter a new method, there is no previous line that we been to in that method.
		
    }

    // method coverage collection
    @Override
    public void visitCode(){
    	/*
    	mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
    	mv.visitLdcInsn("visitCode executed");
    	mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    	*/
    	//System.out.println("in visitCode");
    	
    	
    	int i = 1;
        if((this.access & Opcodes.ACC_STATIC) != 0){
            //System.out.println("in visitCode " + className + "/" + methodName + "/" + signature + "is a static method");
            i = 0;
        }

        //System.out.println("beginning of loop, size of variableNames = " + variableNames.size());
        int x = 0;
    	for(Type tp: paramTypes) {
    		
    		 String mName;
    		 String type;
    		 
             if(methodName.equals("<init>")) {
             	mName = className.substring(className.lastIndexOf("/")+1, className.length());
             } else
             	mName = methodName;

             int z = 0;
             for(int y = 0; i < variableNames.size(); y++) {
            	 if (variableNames.get(y).index == i) {
            		 z = y;
            		 break;
            	 }
             }
             mv.visitLdcInsn(className);
             mv.visitLdcInsn(mName);
             mv.visitLdcInsn(desc);
             mv.visitLdcInsn(getVariableType(tp.getDescriptor()) + " " +variableNames.get(z).name);
             //type = getVariableType(local.desc);
             //mv.visitLdcInsn(type + " " + local.name);
             //System.out.println(type);
             //int loc = local.index;
             
             
             //System.out.println(className + "." + mName + ": " + type + " " + local.name + ", index=" + local.index);
    		if (tp.equals(Type.BOOLEAN_TYPE)) {
    			//System.out.println("in bool");
                mv.visitVarInsn(Opcodes.ILOAD, i);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
            } 
    		else if (tp.equals(Type.BYTE_TYPE)) {
            	//System.out.println("in byte");
                mv.visitVarInsn(Opcodes.ILOAD, i );
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
            } 
    		else if (tp.equals(Type.CHAR_TYPE)) {
            	//System.out.println("in char");
                mv.visitVarInsn(Opcodes.ILOAD, i );
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
            } 
    		else if (tp.equals(Type.SHORT_TYPE)) {
            	//System.out.println("in short");
                mv.visitVarInsn(Opcodes.ILOAD, i );
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
            } 
    		else if (tp.equals(Type.INT_TYPE)) {
            	//System.out.println("in int");
                mv.visitVarInsn(Opcodes.ILOAD, i );
                //mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/Integer;", false);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            } 
    		else if (tp.equals(Type.LONG_TYPE)) {
            	//System.out.println("in long");
                mv.visitVarInsn(Opcodes.LLOAD, i );
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                //i++;
            } 
    		else if (tp.equals(Type.FLOAT_TYPE)) {
            	//System.out.println("in float");
                mv.visitVarInsn(Opcodes.FLOAD, i );
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
            } 
    		else if (tp.equals(Type.DOUBLE_TYPE)) {
            	//System.out.println("in double");
                mv.visitVarInsn(Opcodes.DLOAD, i );
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                //i++;
            } 
    		else {
            	//System.out.println("in others");
                mv.visitVarInsn(Opcodes.ALOAD, i);
            }
    		
             
    		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "edu/utdallas/DataTracing", 
            		"addLocalVariables", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V", false);
            		
    		i++;
    		
    		//mv.visitMethodInsn(Opcodes.INVOKESTATIC, "edu/utdallas/DataTracing", 
            //		"printOut", "(Ljava/lang/Object;)V", false);
            		
        	//mv.visitMethodInsn(Opcodes.ILOAD, "java/lang/String", "valueOf", "(I)Ljava/lang/Integer;", false);
    		
    	}
    	
    	//System.out.println("end of loop");
 
    	super.visitCode();
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
    public void visitLocalVariable(String name, String desc, String signature, 
    		Label start, Label end, int index) {
    	/*
    	System.out.println("Local Variable: ");
    	System.out.println(className + "." + methodName + ": " + name.toString() + "[desc]" + desc
    			+ "[sign]" + signature);
    	*/
    	check(className);
    	super.visitLocalVariable(name,desc,signature,start,end,index);
    }
    
    public void check(String className) {
    	try {
//    		FileWriter fout;
//    		fout = new FileWriter("localVariables.txt"); 
//    		fout.write("Classname and methodname"+className+"."+methodName);
    	 final ClassReader reader = new ClassReader(className);
    	    final ClassNode classNode = new ClassNode();
    	    reader.accept(classNode, 0);
    	    //System.out.println("ClassNode:"+classNode.name);
    	    for(final MethodNode mn : (List<MethodNode>)classNode.methods) {
//    	        if(mn.name.equalsIgnoreCase("testLocals")) {
    	    	//System.out.println("MethodNode:"+mn.name);
    	    	String key = classNode.name+"."+mn.name;
    	    	
    	    	if(!DataTracing.locvars.containsKey(key)) {
    	    		DataTracing.locvars.put(key, new HashSet<String>());
    	    	}
    	            for(final LocalVariableNode local : (List<LocalVariableNode>)mn.localVariables) {
    	                //System.out.println("Local Variable name " + local.name + " description: " + local.desc + "signature : " + local.signature + " index : " + local.index);
//    	                fout.write("Local Variable name " + local.name + " description: " + local.desc + " signature : " + local.signature + " index : " + local.index);
    	                HashSet<String> al=DataTracing.locvars.get(key);
    	                al.add(local.name);
    	                if(local.desc.contains("String")) {
    	                    mn.visitVarInsn(Opcodes.ALOAD, local.index);
//    	                    final VarInsnNode node = new VarInsnNode(Opcodes.ALOAD, 1);
//
//    	                    list.add(node);
    	                    //System.out.println("added local var String '" + local.name + "'");
    	                }if(local.desc.contains("I")) {
    	                	mn.visitVarInsn(Opcodes.ALOAD, local.index);
    	                	//System.out.println("added local var Int '" + local.name + "'");
    	                }
    	            }

//    	        }
    	    }
//    	    fout.close();
    	}catch (Exception e) {
			// TODO: handle exception
		}
    	
    }
    
    public String getVariableType(String s) {
    	String type;
    	boolean isArray = false;
    	String arrayBrackets = "";
    	
    	while(s.startsWith("[")) {
    		s = s.substring(1);
    		arrayBrackets += "[]";
    		isArray = true;
    	}
    	
    	if(s.length() > 1) {
    		type = s.substring(s.lastIndexOf("/")+1, s.length()-1);
    	} else if(s.equals("Z")) {
    		type = "bool";
    	} else if(s.equals("C")) {
    		type = "char";
    	} else if(s.equals("B")) {
    		type = "byte";
    	} else if(s.equals("S")) {
    		type = "short";
    	} else if(s.equals("I")) {
    		type = "int";
    	} else if(s.equals("F")) {
    		type = "float";
    	} else if(s.equals("J")) {
    		type = "long";
    	} else if(s.equals("D")) {
    		type = "double";
    	} else 
    		type = "othersVariables";
    	
    	if(isArray) {
    		type += arrayBrackets;
    	}
    	return type;
    }
	
}