package com.lue.client.tests;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.lue.common.Result.STATUS;
import com.lue.common.TestResult;
import com.lue.common.util.ObservableRmiIF;
import com.lue.common.util.ObserverRmiIF;

public abstract class Test<T extends TestParameters> implements Serializable, ObservableRmiIF, TestIF{
    public static final long serialVersionUID = 1000;
    protected static final Logger LOGGER = Logger.getLogger(Test.class.getName() );
    protected TestResult testResult;
    protected List<ObserverRmiIF> observers;
    protected Integer testId;
    protected T parameters;
    public Test(Integer testId, T parameters) throws IllegalArgumentException, IllegalAccessException {
	observers = new ArrayList<>();
	this.testId = testId;
	this.parameters = parameters;
    }

    /**
     * sets up the environment (result object etc.) and runs the test
     */
    @Override
    public TestResult execute() {
	testResult = new TestResult("Aggregated Result");
	// before
	try {
	    before();
	} catch (Exception e) {
	    e.printStackTrace();
	    testResult.setErrorMessage("Error @before! " + e.getMessage());
	    testResult.setStatus(STATUS.ERROR);
	    LOGGER.info("Test " + getTestIdentifier() + " threw error in before method!");
	    notifyObservers();
	    return testResult;
	}

	// run
	testResult.setStart(System.currentTimeMillis());
	TestResult subTestResult = null;
	try {
	    subTestResult = runTestImpl();
	    LOGGER.info("Test " + getTestIdentifier() + " finished.");
	} catch (Exception e) {
	    e.printStackTrace();
	    testResult.setErrorMessage("Error @runTestImpl " + e.getMessage());
	    testResult.setStatus(STATUS.ERROR);
	    LOGGER.info("Test " + getTestIdentifier() + " threw error in runTestImpl method!");
	    notifyObservers();
	    return testResult;
	} finally {
	    testResult.setEnd(System.currentTimeMillis());
	    if(subTestResult != null)
		testResult.addSubResult(subTestResult);
	    // after
	    try {
		after();
	    } catch (Exception e) {
		e.printStackTrace();
		testResult.setErrorMessage("Error @after! " + e.getMessage());
		testResult.setStatus(STATUS.ERROR);
		LOGGER.info("Test " + getTestIdentifier() + " threw error in after method!");
	    }
	}
	notifyObservers();
	return testResult;
    }

    public int getTestId() {
	return testId;
    }
    
    

    public TestResult getTestResult() {
        return testResult;
    }

    // Observable stuff...
    public void addObserver(ObserverRmiIF o){
	observers.add(o);
    }
    public void removeObserver(ObserverRmiIF o){
	observers.remove(o);
    }

    public void notifyObservers(){
	for(ObserverRmiIF o : observers){
	    try {
		o.update();
	    } catch (RemoteException e) {
		e.printStackTrace();
	    }
	}
    }

    /*
     * executes the test
     */
    protected abstract TestResult runTestImpl() throws Exception;

    protected abstract void before() throws Exception;
    protected abstract void after() throws Exception;

    protected abstract String getTestIdentifier();



    //    protected void setTestParams(Map<String, Object> payload) throws IllegalArgumentException, IllegalAccessException {
    //	Field[] fields = getClass().getDeclaredFields();
    //	for(Field f : fields) {
    //	    if(f.isAnnotationPresent(TestParam.class)) {
    //		String rep = f.getAnnotation(TestParam.class).rep();
    //		if(payload.containsKey(rep)){
    //		    boolean accessible = f.isAccessible();
    //		    f.setAccessible(true);
    //		    Object value = payload.get(rep);
    //		    f.set(this, value);
    //		    f.setAccessible(accessible);
    //		}
    //	    }
    //	}
    //    }	
}