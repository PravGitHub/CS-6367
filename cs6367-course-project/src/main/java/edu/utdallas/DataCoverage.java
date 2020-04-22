package edu.utdallas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DataCoverage {
	
	public static List<String> op;
	
	public DataCoverage() {
		op=new ArrayList<String>();
		
	}
	
	public synchronized static void addVariable(Object obj, String className, String methodName, String signature){
		System.out.println("addVariable:");
		System.out.println("Object:"+obj);
		
		op.add(className + "." + methodName + "." + signature + ": Object=" + obj.toString());
	}
	
	
	public void print() {
		FileWriter fw=null;
		  try {
			  fw= new FileWriter("data-cov.txt",true);
			  for(String s:op) {
				fw.write(s+"\n");
			  }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        finally {
        	try {
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
	}
}
