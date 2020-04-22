package edu.utdallas;
import org.junit.runner.Description;
import org.junit.runner.Result;
//import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class JUnitExecutionListener extends RunListener {

	private ControlFlowCoverage coverage;
	private DataCoverage dcoverage;
	
	//begin running the test suite
    public void testRunStarted(Description description) throws Exception {
    	//initalizes coverage the first string would contain test class and test method,    	
    	coverage = new ControlFlowCoverage();
    	dcoverage = new DataCoverage();
    }
    //finish running the test suite
    public void testRunFinished(Result result) throws Exception {
    	coverage.print();
    	dcoverage.print();
    }

    //begin running a test
    public void testStarted(Description description) throws Exception {
    	coverage.newTest();
    	coverage.addTestMethod(description.getClassName(), description.getMethodName());
    	
    }

    public void testFinished(Description description) throws Exception {
    	coverage.finishTest();
    }

    
    
    
    
    
    
    /*public void testFailure(Failure failure) throws Exception {
        System.out.println("Failed: " + failure.getDescription().getMethodName());
    }

    public void testAssumptionFailure(Failure failure) {
        System.out.println("Failed: " + failure.getDescription().getMethodName());
    }

    public void testIgnored(Description description) throws Exception {
        System.out.println("Ignored: " + description.getMethodName());
    }*/
}