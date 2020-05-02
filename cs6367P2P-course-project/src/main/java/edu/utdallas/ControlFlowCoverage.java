package edu.utdallas;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ControlFlowCoverage {
	//The main hashmap that contains all test classnames, test methods, classname, and line number
	//the key is the testclass, its values are the statements that it covers
	private static HashMap<String, HashMap<String,HashSet<Integer>>> testMethod;
	//intermediate hash map that holds the statement it covers and what class it belongs to
	//key is the class, values are the line numbers in the class
	private static HashMap<String, HashSet<Integer>> className;
	//HashSet that contains the statements that were covered without duplicates
	private static HashSet<Integer> lineNum;
	//name of the testClass
	private String testClass;
	
	//constructor, when we start running a test suite, clear previous info
	public ControlFlowCoverage() {		
		//clears the testMethod for new test suite
		testMethod = new HashMap<String, HashMap<String,HashSet<Integer>>>();
	}
	
	//when a new test case is started, clear previous test case info
	public void newTest() {
		//clears the classes we entered for the test case
		className = new HashMap<String, HashSet<Integer>>();
	}
	//name of the test class we are running
	public void addTestMethod(String cName, String mName) {
		testClass = "[Test] " + cName + ":" + mName;
	}
	
	//when the test case is finished, store it for eventually output
	public void finishTest() {
		//places test class information into HashMap
		testMethod.put(testClass, className);
	}
	
	//stores the line numbers that a class covers
	public static void getLineNumbers(String cName, Integer line) {
		//adds line numbers the class covers, no duplicates
		
		//if we don't know the class name (cName), can't create a key for the lines it covers
		//if we don't have className, can't even place the key and values into it
		if(cName == null || className == null)
			return;
		//retrieve the values for the cName(cName is the key)
		lineNum = className.get(cName);
		//if cName is new, then it is not in the hashmap, so there is no HashSet
		//therefore we create one for it
		if(lineNum == null)
			lineNum = new HashSet<Integer>();
		//add the line it covers and place it back into the className HashMap
		lineNum.add(line);
		className.put(cName, lineNum);
	}
	
	//prints out the coverage
	public void print() {
		FileOutputStream fout;
    	try {
    		//creates or overwrites a txt file
    		fout = new FileOutputStream("stmt-cov.txt");
    		//goes through each test class/test method statements   		
    		for(Map.Entry<String, HashMap<String, HashSet<Integer>>> element : testMethod.entrySet()) {
    			//converts string into bytes and prints it out to file
    			byte[] methodHeader = (element.getKey() + "\n").getBytes();
    			fout.write(methodHeader);	//writes out the name of test class and method
    			//enter into the individual class names and line numbers that the test class covers
    			for(Map.Entry<String, HashSet<Integer>> entry : element.getValue().entrySet()) {			
    				//classes can cover multiple statements
    				List<Integer> values = new ArrayList<Integer>(entry.getValue());
    				Collections.sort(values);
    				for(Integer i : values) {	//was entry.getValue()
    					//prints class name and the statements that are covered
    					String statHeader = entry.getKey() + ":" + i + "\n";
    					fout.write(statHeader.getBytes());
    				}
    			}
    		}
    		fout.close();
    	} catch(IOException ex) {
    		ex.getStackTrace();
    	}
	}
}
