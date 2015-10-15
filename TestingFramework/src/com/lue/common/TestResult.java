package com.lue.common;


import java.util.ArrayList;

/**
 * Contains all the TestMethodResuls of ONE test class (for instance a instrumentation test class called MainActivityTest).
 */
public class TestResult extends Result implements ChangeListenerIF{

    private static final long serialVersionUID = 1L;
    ArrayList<TestResult> testSubResults;
    
    ChangeListenerIF superResult;

    public TestResult() {
	super("");
	testSubResults = new ArrayList<>();
    }

    public TestResult(String testName) {
	super(testName);
	testSubResults = new ArrayList<>();
    }
   
    public void addSubResult(TestResult testSubResult) {
	testSubResults.add(testSubResult);
	testSubResult.setChangeListenerIF(this);
	update();
    }

    public void update() {
	for(TestResult testSubResult : testSubResults) {
	    if (start > testSubResult.getStart())
		start = testSubResult.getStart();
	    if (end < testSubResult.getEnd())
		end = testSubResult.getEnd();
	    if (testSubResult.getStatus() == STATUS.FAILURE && testSubResult.getStatus() != STATUS.ERROR)
		status = STATUS.FAILURE;
	    if (testSubResult.getStatus() == STATUS.ERROR)
		status = STATUS.ERROR;
	}
	if(superResult != null)
	    superResult.update();
    }

    public TestResult getTestResultByKey(String key) {
        if(testKey.equals(key))
            return this;
        for(TestResult testSubResult : testSubResults) {
            TestResult t = testSubResult.getTestResultByKey(key);
            if(t != null)
                return t;
        }
        return null;
    }

    public boolean containsTestResult(String key) {
        if(testKey.equals(key))
            return true;
        for(TestResult testSubResult : testSubResults) {
            if(testSubResult.containsTestResult(key))
                return true;
        }
        return false;
    }

    public ArrayList<TestResult> getTestSubResults() {
	return  testSubResults;
    }
}
