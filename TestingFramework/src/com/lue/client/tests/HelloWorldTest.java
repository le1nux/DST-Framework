package com.lue.client.tests;

import java.util.logging.Level;

import com.lue.common.Result.STATUS;
import com.lue.common.TestResult;

public class HelloWorldTest extends Test<TestParameters>{
    public static final String TEST_IDENTIFIER = "HelloWorldTest";
    private static final long serialVersionUID = 1L;
    
    @TestConstructor
    public HelloWorldTest(Integer testId, TestParameters parameters) throws IllegalArgumentException, IllegalAccessException {
	super(testId, parameters);
    }

    @Override
    protected TestResult runTestImpl() throws Exception {
	TestResult result = new TestResult(TEST_IDENTIFIER);
	result.setStart(System.currentTimeMillis());
	LOGGER.log(Level.INFO, "Logger says Hello World!");
	System.out.println("Console says Hello World!");
	result.setEnd(System.currentTimeMillis());
	result.setStatus(STATUS.SUCCESS);
	return result;	
    }

    @Override
    protected String getTestIdentifier() {
	return TEST_IDENTIFIER;
    }

    @Override
    protected void before() {
    }

    @Override
    protected void after() {
    }
}