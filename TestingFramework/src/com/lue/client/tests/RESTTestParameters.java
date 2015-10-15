package com.lue.client.tests;

public class RESTTestParameters extends TestParameters {
    private static final long serialVersionUID = 1L;

    protected RESTTest.HTTP_METHOD method;
    protected String payload;
    protected String url;
    
    public RESTTestParameters() {

    }
    
    public RESTTestParameters(RESTTest.HTTP_METHOD method, String payload, String url) {
	this.method = method;
	this.payload = payload;
	this.url = url;
    }

    public RESTTest.HTTP_METHOD getMethod() {
        return method;
    }

    public void setMethod(RESTTest.HTTP_METHOD method) {
        this.method = method;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}