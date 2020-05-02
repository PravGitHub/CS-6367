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
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Value;

class MethodTransformVisitor extends MethodVisitor implements Opcodes {

	private String methodName; // name of the method
	private String className; // name of the class
	private int access;
	private String desc;
	private int line;
	private Type[] paramTypes;
	private List<LocalVariableNode> variableNames;
	private List<FieldNode> fieldNodeList;

	public MethodTransformVisitor(final MethodVisitor mv, int access, String methodName, String className, String desc,
			Type[] paramTypes, List<LocalVariableNode> variableNames, List<FieldNode> fieldNodeList) {
		super(ASM5, mv);
		this.methodName = methodName;
		this.className = className;
		this.access = access;
		this.desc = desc;
		this.paramTypes = paramTypes;
		this.variableNames = variableNames;
		this.line = 0;
		this.fieldNodeList = fieldNodeList;// when we enter a new method, there is no previous line that we been to in
											// that method.
	}

	// method coverage collection
	@Override
	public void visitCode() {
		
		getFieldValues();

		int flag_i = 1;
		if ((this.access & Opcodes.ACC_STATIC) != 0) {
			flag_i = 0;
		}

		for (Type paramtype : paramTypes) {

			String methodname;
			String formattedtype = getVariableType(paramtype.getDescriptor());

			if (methodName.equals("<init>")) {
				methodname = className.substring(className.lastIndexOf("/") + 1, className.length());
			} else
				methodname = methodName;

			int key = 0;
			for (int index = 0; flag_i < variableNames.size(); index++) {
				if (variableNames.get(index).index == flag_i) {
					key = index;
					break;
				}
			}
			mv.visitLdcInsn(className);
			mv.visitLdcInsn(methodname);
			mv.visitLdcInsn(desc);
			mv.visitLdcInsn(formattedtype + " " + variableNames.get(key).name);

			checkTypeParam(flag_i, paramtype, formattedtype);

			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "edu/utdallas/DataTracing", "addLocalVariables",
					"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V",
					false);

			flag_i++;           		 		
		}

		super.visitCode();
	}

	private void checkTypeParam(int i, Type paramtype, String formattedtype) {
		if (formattedtype.equals("short")) {
			mv.visitVarInsn(Opcodes.ILOAD, i);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
		}
		else if (formattedtype.equals("boolean")) {
			mv.visitVarInsn(Opcodes.ILOAD, i);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;",
					false);
		} 
		else if (formattedtype.equals("int")) {
			mv.visitVarInsn(Opcodes.ILOAD, i);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;",
					false);
		} 
		else if (formattedtype.equals("char")) {
			mv.visitVarInsn(Opcodes.ILOAD, i);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;",
					false);
		} 
		else if (formattedtype.equals("double")) {
			mv.visitVarInsn(Opcodes.DLOAD, i);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
			// i++;
		}  
		else if (formattedtype.equals("long")) {
			mv.visitVarInsn(Opcodes.LLOAD, i);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
			// i++;
		} 
		else if (formattedtype.equals("byte")) {
			mv.visitVarInsn(Opcodes.ILOAD, i);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
		} 
		else if (formattedtype.equals("float")) {
			mv.visitVarInsn(Opcodes.FLOAD, i);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
		}  
		else {
			if(formattedtype.equals("String")) {
				mv.visitVarInsn(Opcodes.ALOAD, i);
			}
			else {
				mv.visitLdcInsn(Integer.toHexString(paramtype.getDescriptor().hashCode()));
			}
		}
	}

	@Override // given from the hint
	public void visitLineNumber(final int lineNo, final Label start) {

		// calls onto the getLineNumbers function in JUnitExecutionListener
		this.line = lineNo; // stores line we have entered
		mv.visitLdcInsn(className);
		mv.visitLdcInsn(new Integer(line));
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
		mv.visitMethodInsn(INVOKESTATIC, "edu/utdallas/ControlFlowCoverage", "getLineNumbers",
				"(Ljava/lang/String;Ljava/lang/Integer;)V", false);
		super.visitLineNumber(lineNo, start);
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
		super.visitLocalVariable(name, desc, signature, start, end, index);
	}

	public void getFieldValues() {
		try {
			if (!fieldNodeList.isEmpty() && fieldNodeList != null) {
				for (FieldNode fieldNode : fieldNodeList) {
					if ((this.access & Opcodes.ACC_STATIC) != 0 || methodName.equals("<init>"))
						continue;
					boolean checkIfStatic = (fieldNode.access & Opcodes.ACC_STATIC) != 0;

					String type = getVariableType(fieldNode.desc);
					mv.visitLdcInsn(className);
					mv.visitLdcInsn(methodName);
					mv.visitLdcInsn(desc);
					mv.visitLdcInsn(type + " " + fieldNode.name);
					

					
					if (checkFieldType(fieldNode.desc).equals("othersVariables") && !type.equals("String") && !type.equals("Integer") && !type.equals("Boolean")) {
						mv.visitLdcInsn(Integer.toHexString(fieldNode.desc.hashCode()));
					} else {
						if (checkIfStatic) {
							mv.visitFieldInsn(Opcodes.GETSTATIC, className, fieldNode.name, fieldNode.desc);
						} else {
							mv.visitVarInsn(ALOAD, 0);
							mv.visitFieldInsn(Opcodes.GETFIELD, className, fieldNode.name, fieldNode.desc);
						}
						
						
						if(type.equals("boolean")) {
							mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;",false);
						} 
						else if(type.equals("long")) {
							mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;",false);
						} 
						else if(type.equals("double")) {
							mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;",false);
						} 
						else if(type.equals("byte")) {
							mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;",false);
						}  
						else if(type.equals("short")) {
							mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;",false);
						} 
						else if(type.equals("int")) {
							mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;",false);
						}  
						else if(type.equals("char")) {
							mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;",false);
						} 
						else if(type.equals("float")) {
							mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;",false);
						} 
					}						
					
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "edu/utdallas/DataTracing", "addLocalVariables",
							"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V",
							false);
					
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public String checkFieldType(String s) {
		String type;
		if (s.equals("Z")) {
			type = "boolean";
		} 
		else if (s.equals("I")) {
			type = "int";
		} 
		else if (s.equals("C")) {
			type = "char";
		} 
		else if (s.equals("B")) {
			type = "byte";
		} 
		else if (s.equals("J")) {
			type = "long";
		} 
		else if (s.equals("S")) {
			type = "short";
		}  
		else if (s.equals("D")) {
			type = "double";
		} 
		else if (s.equals("F")) {
			type = "float";
		} 
		else {
			return "othersVariables";
		}
		return type;
	}

	public String getVariableType(String s) {
		String type;
		boolean isArray = false;
		String arrayBrackets = "";

		while (s.startsWith("[")) {
			s = s.substring(1);
			arrayBrackets += "[]";
			isArray = true;
		}

		
    	if(s.length() > 1) {
    		type = s.substring(s.lastIndexOf("/")+1, s.length()-1);
    		if(s.equals("Boolean"))
    			type = "boolean";
    		if(s.equals("Integer"))
    			type = "int";
    	} 
    	else if (s.equals("Z")) {
			type = "boolean";
		}  
    	else if (s.equals("F")) {
			type = "float";
		} 
    	else if (s.equals("B")) {
			type = "byte";
		} 
    	else if (s.equals("I")) {
			type = "int";
		} 
    	else if (s.equals("C")) {
			type = "char";
		} 
    	else if (s.equals("J")) {
			type = "long";
		} 
    	else if (s.equals("D")) {
			type = "double";
		} 
    	else if (s.equals("S")) {
			type = "short";
		} 
    	else
			type = "othersVariables";

		if (isArray) {
			type += arrayBrackets;
		}
		return type;
	}

}