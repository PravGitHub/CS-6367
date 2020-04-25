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

public class DataTracing {
	public static HashMap<String, HashSet<String>> locvars;
	
	private static HashMap<String, HashMap<String, HashMap<String, HashSet<Object>>>> classMethods;
	private static HashMap<String, HashMap<String, HashSet<Object>>> methodVariables;
	private static HashMap<String, HashSet<Object>> variableValues;
	private static HashSet<Object> values;
	
	public DataTracing() {
		classMethods = new HashMap<String, HashMap<String, HashMap<String, HashSet<Object>>>>();
		locvars = new HashMap<String,HashSet<String>>();
	}
	
	public synchronized static void addLocalVariables(String className, String methodName, String desc, String variable, Object value) {
		
		String fullName = className + "." + methodName;
		//System.out.println("in add localvariables");
		
		methodVariables = classMethods.get(fullName);
		if(methodVariables == null)
			methodVariables = new HashMap<String, HashMap<String, HashSet<Object>>>();
		
		variableValues = methodVariables.get(desc);
		if(variableValues == null)
			variableValues = new HashMap<String, HashSet<Object>>();
		
		values = variableValues.get(variable);
		if(values == null)
			values = new HashSet<Object>();
		
		values.add(value);		
		variableValues.put(variable, values);
		methodVariables.put(desc, variableValues);
		classMethods.put(fullName, methodVariables);
		
	}
	
	public void print() {
		
		FileWriter fout;
		String tmp=null;
		String res[]=null;
    	try {
    		//creates or overwrites a txt file
    		fout = new FileWriter("datatrace.txt");  
    		
    		for(Map.Entry<String, HashMap<String, HashMap<String, HashSet<Object>>>> element : classMethods.entrySet()) {
    			for(Map.Entry<String, HashMap<String, HashSet<Object>>> entry : element.getValue().entrySet()) {
    				String line = element.getKey() + ":-->\n";
    				fout.write(line);
    				for(Map.Entry<String, HashSet<Object>> subentry : entry.getValue().entrySet()) {
    					
    					tmp = subentry.toString();
    					//fout.write("\n"+tmp);
    					
    					res = tmp.split("=");
    					
    					String var[] = res[0].split(" ");    					
    					String v[] = res[1].substring(1, res[1].length()-1).split(", ");
    					boolean flag;
    					
    					
						if(var[0].equals("int")) {							
							int t=Integer.parseInt(v[0]);
							int min,max;
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
							}
							
							if(flag) {
								fout.write("\n"+var[0]+" "+var[1]+" = "+t+"\t\t---> Constant Invariant");
							}
							else {
								fout.write("\n"+var[0]+" "+min+" <= "+var[1]+" <= "+max+"\t\t---> Range Invariant");
							}
						
						}
						else {
							String t = v[0];
							flag=true;
							
							for(String s : v) {
								if(!t.equals(s)) {
									flag=false;
									break;
								}
							}
							
							if(flag) {
								fout.write("\n"+var[0]+" "+var[1]+" = "+t+"\t\t---> Constant Invariant");
							}
							
						}    					
        				
        				
    				}
    				fout.write("\n\n----------\n ");
    				
    			}
    		}
    		fout.close();
    		
    	} catch(IOException ex) {
    		ex.getStackTrace();
    	}
    	
	}
	
	public void printLocalVariables() {
		
		FileWriter f=null;
		
		try {
			f=new FileWriter("localvariabletrace.txt");
			
			for(String k: locvars.keySet()) {
				HashSet<String> a = locvars.get(k);
				f.write("\n"+k+"--->\n");
				for(String var: a) {
					f.write(var+"\n");
				}
				f.write("\n-----------------\n");
			}
			
			f.close();
			
		}
		catch(IOException ex) {
    		ex.getStackTrace();
    	}
    	
	}
}
