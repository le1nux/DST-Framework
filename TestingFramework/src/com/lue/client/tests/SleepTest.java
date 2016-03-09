package com.lue.client.tests;

import java.util.logging.Level;

import com.lue.common.Result.STATUS;
import com.lue.common.TestResult;

public class SleepTest extends Test<SleepTestParameters>{
	public static final String TEST_IDENTIFIER = "SleepTest";
	private static final long serialVersionUID = 1L;

	@TestConstructor
	public SleepTest(Integer testId, TestParameters parameters) throws IllegalArgumentException, IllegalAccessException {
		super(testId, (SleepTestParameters) parameters);
	}

	public Integer getDuration() {
		return parameters.getDuration();
	}

	public void setDuration(Integer duration) {
		parameters.setDuration(duration);
	}

	@Override
	protected TestResult runTestImpl() {
		TestResult result = new TestResult(TEST_IDENTIFIER);
		try {
			LOGGER.log(Level.INFO, "Going to sleep for " + parameters.getDuration() + "ms...");
			result.setStart(System.currentTimeMillis());
			Thread.sleep(parameters.getDuration());
			result.setEnd(System.currentTimeMillis());
			LOGGER.log(Level.INFO, "executed");
			result.setStatus(STATUS.SUCCESS);
		} catch (InterruptedException e) {
			result.setStatus(STATUS.FAILURE);
			result.setErrorMessage(e.getMessage());
		}
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
