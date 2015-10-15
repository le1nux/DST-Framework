package com.lue.client.tests.android;

import com.lue.client.tests.TestParameters;

public class AndroidTestParameters extends TestParameters {
    private static final long serialVersionUID = 1L;
    protected String instrumentationTest;
    
    public AndroidTestParameters() {
    }
    
    public AndroidTestParameters(String instrumentationTest) {
	this.instrumentationTest = instrumentationTest;
    }
    public String getInstrumentationTest() {
        return instrumentationTest;
    }
    public void setInstrumentationTest(String instrumentationTest) {
        this.instrumentationTest = instrumentationTest;
    }
}