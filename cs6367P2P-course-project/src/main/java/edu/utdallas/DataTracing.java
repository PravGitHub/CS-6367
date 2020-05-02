package edu.utdallas;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataTracing {
	
	private static HashMap<String, HashMap<String, HashMap<String, List<Object>>>> classMethods;
	private static HashMap<String, HashMap<String, List<Object>>> methodVariables;
	private static HashMap<String, List<Object>> variableValues;
	private static List<Object> values;
	private static int classCounter=1;
	private static int methodCounter=1;
	
	private static HashMap<String, Integer> count;
	
	public DataTracing() {
		classMethods = new HashMap<String, HashMap<String, HashMap<String, List<Object>>>>();
		count = new HashMap<String, Integer>();
		
	}
	
	public synchronized static void addLocalVariables(String className, String methodName, String desc, String variable, Object value) {
		String fullName = className + "." + methodName;
		
		methodVariables = classMethods.get(fullName);
		if(methodVariables == null)
			methodVariables = new HashMap<String, HashMap<String, List<Object>>>();
		
		variableValues = methodVariables.get(desc);
		if(variableValues == null)
			variableValues = new HashMap<String, List<Object>>();
		
		values = variableValues.get(variable);
		if(values == null)
			values = new ArrayList<Object>();
		
		values.add(value);		
		variableValues.put(variable, values);
		methodVariables.put(desc, variableValues);
		classMethods.put(fullName, methodVariables);
		
		String key = fullName + "." + desc + "." + variable;
		Integer num = (Integer) count.get(key);
		if(num == null)
			num = 1;
		else
			num++;
		count.put(key, num);
	}
	

	
	public void print() {
		
		FileWriter fout,fout1;
		String tmp=null;
		String res[]=null;
    	try {
    		//creates or overwrites a txt file
    		fout = new FileWriter("datatrace.txt");  
    		fout1 = new FileWriter("invariants.txt");
    		for(Map.Entry<String, HashMap<String, HashMap<String, List<Object>>>> element : classMethods.entrySet()) {
    			for(Map.Entry<String, HashMap<String, List<Object>>> entry : element.getValue().entrySet()) {
    				String line = element.getKey() + ":-->\n";
    				fout.write(line);
    				fout1.write(line);
    				for(Map.Entry<String, List<Object>> subentry : entry.getValue().entrySet()) {
    					String key = element.getKey() + "." + entry.getKey() + "." +subentry.getKey();    					
    					tmp = subentry.toString();
    					fout.write("\n"+tmp);
    					
    					res = tmp.split("=\\[");
    					
    					String var[] = res[0].split(" ");    
    					if(res[1].equals("]")) {
    						fout1.write("\n"+var[0]+" "+var[1]+"\t\t---> UnInit Invariant"+"\t --No. of values analyzed(Confidence)= 1 \t --Low Confidence");
    						continue;
    					}
    					String v[] = res[1].substring(0, res[1].length()-1).split(", ");
    					boolean flag;
    					
    					
						if(var[0].equals("int")) {							
							int t=Integer.parseInt(v[0]);
							int count =0 ;
							int min,max;
							String confidence="";
							min=max=t;
							flag=true;
							for(String s : v) {
								int i = Integer.parseInt(s);
								if(t!=i) {
									flag=false;									
								}
								if(i<min) { 
									min=i;
								}
								if(i>max) {
									max=i;
								}
								count++;
							}
							if(count<=10) {
								confidence = "\t --Low Confidence";
							}
							else if(count<=30) {
								confidence = "\t --Medium Confidence";
							}
							else {
								confidence = "\t --High Confidence";
							}
							
							if(flag) {
								fout1.write("\n"+var[0]+" "+var[1]+" = "+t+"\t\t---> Constant Invariant"+"\t --No. of values analyzed(Confidence)= "+count+confidence);
							}
							else {
								fout1.write("\n"+var[0]+" "+min+" <= "+var[1]+" <= "+max+"\t\t---> Range Invariant"+"\t --No. of values analyzed(Confidence)= "+count+confidence);
							}
						
						}
						else {
							if(v.length>0) {
							String t = v[0];
							String confidence="";
							int count=0;
							flag=true;
							
							for(String s : v) {
								if(!t.equals(s)) {
									flag=false;									
								}
								count++;
							}
							if(count<=10) {
								confidence = "\t --Low Confidence";
							}
							else if(count<=30) {
								confidence = "\t --Medium Confidence";
							}
							else {
								confidence = "\t --High Confidence";
							}
							
							if(flag) {
								fout1.write("\n"+var[0]+" "+var[1]+" = "+t+"\t\t---> Constant Invariant"+"\t --No. of values analyzed(Confidence)= "+count+confidence);
							}
							
						}
						}
        				
        				
    				}
    				fout.write("\n\n----------\n ");
    				fout1.write("\n\n----------\n ");
    			}
    		}
    		fout.close();
    		fout1.close();
    	} catch(IOException ex) {
    		ex.getStackTrace();
    	}
    	
	}
	
	public static Object getObj(Object obj) {
		return obj;
	}
}

