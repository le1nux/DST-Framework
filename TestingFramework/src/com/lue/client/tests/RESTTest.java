package com.lue.client.tests;

import java.io.IOException;
import java.util.logging.Level;

import com.lue.common.Result.STATUS;
import com.lue.common.TestResult;
import com.lue.common.util.RESTClient;
import com.lue.common.util.RESTClient.Response;

public class RESTTest extends Test<RESTTestParameters>{
    public static final String TEST_IDENTIFIER = "RESTTest";
    private static final long serialVersionUID = 1L;
    public static enum HTTP_METHOD {GET, POST, PUT, DELETE};

    @TestConstructor
    public RESTTest(Integer testId, TestParameters parameters) throws IllegalArgumentException, IllegalAccessException {
	super(testId, (RESTTestParameters) parameters);
    }

    @Override
    protected TestResult runTestImpl() {
	TestResult result = new TestResult(TEST_IDENTIFIER);
	LOGGER.info(parameters.getMethod() + " " + parameters.getUrl() + "  Payload: "+ parameters.getPayload());
	RESTClient client = new RESTClient();
	switch(parameters.getMethod()) {
	case GET:					// TODO inconsistencies between GET and PUT ...
	    get(result, client);
	    break;
	case PUT:
	    put(result, client);
	    break;
	case POST:
	    post(result, client);
	    break;
	case DELETE:
	    delete(result, client);
	    break;
	default:
	    LOGGER.log(Level.SEVERE, "ERROR: HTTP METHOD not supported!");
	    break;
	}
	return result;
    }

    private void start(TestResult result) {
	result.setStart(System.currentTimeMillis());
    }

    private void finish(TestResult result, Response response) {
	result.setEnd(System.currentTimeMillis());
	if(response.getStatusCode() != 200 && response.getStatusCode() != 201) {
	    result.setStatus(STATUS.FAILURE);
	    result.setErrorMessage("FAILURE: Status code was " + response.getStatusCode());
	}
	else {
	    result.setStatus(STATUS.SUCCESS);
	}
    }

    private void finishWithError(TestResult result, Exception e) {
	result.setEnd(System.currentTimeMillis());
	result.setStatus(STATUS.ERROR);
	result.setErrorMessage("Error: " + e.getMessage());
    }

    private void get(TestResult result, RESTClient client) {
	try {
	    start(result);
	    Response response = client.get(parameters.getUrl());
	    finish(result, response);
	} catch (IOException e1) {
	    e1.printStackTrace();
	    finishWithError(result, e1);
	}
    }

    private void put(TestResult result, RESTClient client) {
	try {
	    start(result);
	    Response response = client.put(parameters.getUrl(), parameters.getPayload());
	    finish(result, response);
	} catch (IOException e) {
	    e.printStackTrace();
	    finishWithError(result, e);
	}
    }

    private void post(TestResult result, RESTClient client) {
	try {
	    start(result);
	    Response response = client.post(parameters.getUrl(), parameters.getPayload());
	    finish(result, response);
	} catch (IOException e) {
	    e.printStackTrace();
	    finishWithError(result, e);
	}
    }

    private void delete(TestResult result, RESTClient client) {
	try {
	    start(result);
	    Response response = client.delete(parameters.getUrl());
	    finish(result, response);
	} catch (IOException e) {
	    e.printStackTrace();
	    finishWithError(result, e);
	}
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
