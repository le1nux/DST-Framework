package com.lue.client.tests.android;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lue.client.CallbackingProcessOutputReader;
import com.lue.client.tests.Test;
import com.lue.client.tests.TestConstructor;
import com.lue.client.tests.android.Communicator.Command;
import com.lue.common.Result.STATUS;
import com.lue.common.TestResult;
import com.lue.common.util.JsonProcessor;

public class AndroidTest<T extends AndroidTestParameters> extends Test<T> implements CommunicatorCallbackIF{
    public static final String TEST_IDENTIFIER = "AndroidTest";
    private static final long serialVersionUID = 1L;
    private static final int ADB_RESULT_POLL = 10;
    private static final String COMMAND_RESULT = "RESULT";

    protected AdbClient adbClient;
    protected CallbackingProcessOutputReader adbLogReader;
    protected TestResult testResult;
    protected int timeout;	// when retrieving testresults takes tooooo long 
    protected Communicator communicator;

    public AndroidTest(Integer testId, T parameters, int timeout) throws Exception {
	super(testId, parameters);
	this.timeout = timeout;
	adbClient = new AdbClient();
    }

    @TestConstructor
    public AndroidTest(Integer testId, T parameters) throws Exception {
	super(testId, parameters);
	timeout = 5000;
	adbClient = new AdbClient();
    }

    @Override
    protected TestResult runTestImpl() throws Exception {
	String command = "shell:am instrument -w -e class @Test com.lue.max.testapp3.test/com.lue.max.testapp3.MyTestRunner";
	command = command.replace("@Test", parameters.getInstrumentationTest());
	adbClient.sendShellCommand(command);
	LOGGER.info("getting testresult...");
	return getTestResult(); // blocking until testResult is available
    }

    @Override
    protected String getTestIdentifier() {
	return TEST_IDENTIFIER;
    }

    @Override
    protected void before() throws Exception {
	// synchronize clocks
	String timeStamp = new SimpleDateFormat("yyyyMMdd.HHmmss").format(new Date());
	adbClient.sendShellCommand("shell:date -s " + timeStamp);

	//TODO: Should run natively... however log:main command does not work 
	timeStamp = new SimpleDateFormat("MM-dd HH:mm:ss.000").format(new Date());
	communicator = new Communicator();
	communicator.addCallbackObject(COMMAND_RESULT, this);
    }

    @Override
    protected void after() throws Exception {
	communicator.close();
    }

    @Override
    public TestResult getTestResult() {
	if(testResult == null){
	    int milliSecondsWaited = 0;
	    LOGGER.info("Waiting up to " + timeout + "ms for adb's result message...");
	    while(testResult == null && milliSecondsWaited < timeout){
		try {
		    Thread.sleep(ADB_RESULT_POLL);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		milliSecondsWaited = milliSecondsWaited + ADB_RESULT_POLL;
	    }
	    if(testResult == null) {
		LOGGER.severe("Retrieving testresults timed out..");
		testResult = new TestResult();
		testResult.setErrorMessage("Retrieving testresults timed out...");
		testResult.setStatus(STATUS.ERROR);

	    }else {
		LOGGER.info("Received adb's result message after " + milliSecondsWaited + "ms!");
	    }
	}
	return testResult;
    }

    @Override
    public void callback(Command command) {
	if(command.getIdentifier().equals(COMMAND_RESULT)){
	    LOGGER.info("Result: " + command.getPayload());
	    try {
		testResult = (TestResult) JsonProcessor.fromJson(command.getPayload(), TestResult.class);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }
}
