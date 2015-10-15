package com.lue.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class RESTClient {
    protected 	HttpClient httpClient;

    public RESTClient() {
	httpClient =  HttpClientBuilder.create().build();
    }

    public Response get(String url) throws ClientProtocolException, IOException {
	HttpGet getRequest = new HttpGet(url);
	getRequest.addHeader("accept", "application/json");

	HttpResponse response = httpClient.execute(getRequest);

	BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

	StringBuffer stringBuffer = new StringBuffer();
	String output;
	while ((output = br.readLine()) != null) {
	    stringBuffer.append(output);
	}
	return new Response(response.getStatusLine().getStatusCode(), stringBuffer.toString());
    }

    public Response post(String url, String jsonPayload) throws ClientProtocolException, IOException {
	HttpPost postRequest = new HttpPost(url);
	StringEntity input = new StringEntity(jsonPayload);

	input.setContentType("application/json");
	postRequest.setEntity(input);

	HttpResponse response = httpClient.execute(postRequest);

	BufferedReader br = new BufferedReader( new InputStreamReader((response.getEntity().getContent())));

	StringBuffer stringBuffer = new StringBuffer();
	String output;
	while ((output = br.readLine()) != null) {
	    stringBuffer.append(output);
	}
	return new Response(response.getStatusLine().getStatusCode(), stringBuffer.toString());
    }

    public Response put(String url, String jsonPayload) throws ClientProtocolException, IOException {
	HttpPut putRequest = new HttpPut(url);
	StringEntity input = new StringEntity(jsonPayload);

	input.setContentType("application/json");
	putRequest.setEntity(input);

	HttpResponse response = httpClient.execute(putRequest);

	BufferedReader br = new BufferedReader( new InputStreamReader((response.getEntity().getContent())));

	StringBuffer stringBuffer = new StringBuffer();
	String output;
	while ((output = br.readLine()) != null) {
	    stringBuffer.append(output);
	}
	return new Response(response.getStatusLine().getStatusCode(), stringBuffer.toString());
    }

    public Response delete(String url) throws ClientProtocolException, IOException {
	HttpDelete deleteRequest = new HttpDelete(url);

	HttpResponse response = httpClient.execute(deleteRequest);

	BufferedReader br = new BufferedReader( new InputStreamReader((response.getEntity().getContent())));

	StringBuffer stringBuffer = new StringBuffer();
	String output;
	while ((output = br.readLine()) != null) {
	    stringBuffer.append(output);
	}

	return new Response(response.getStatusLine().getStatusCode(), stringBuffer.toString());
    }

    public static void main(String[] args) {
	
	/* some little test cases*/
	
	RESTClient client = new RESTClient();
	try {
	    Response r = client.get("http://127.0.0.1:8080/api/schedulerstate");
	    System.out.println("ServerMsg: " + r.getServerMsg() + "  status code: " + r.getStatusCode());
	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	String clientState = "\"UNINITIALIZED\"";
	try {
	    Response r = client.put("http://127.0.0.1:8080/api/schedulerstate", clientState);
	    System.out.println("ServerMsg: " + r.getServerMsg() + "  status code: " + r.getStatusCode());

	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}


	try {
	    Response r = client.get("http://127.0.0.1:8080/api/schedulerstate");
	    System.out.println("ServerMsg: " + r.getServerMsg() + "  status code: " + r.getStatusCode());
	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public class Response {
	protected int statusCode;
	protected String serverMsg;

	public Response(int statusCode, String serverMsg) {
	    this.statusCode = statusCode;
	    this.serverMsg = serverMsg;
	}
	public int getStatusCode() {
	    return statusCode;
	}
	public void setStatusCode(int statusCode) {
	    this.statusCode = statusCode;
	}
	public String getServerMsg() {
	    return serverMsg;
	}
	public void setServerMsg(String serverMsg) {
	    this.serverMsg = serverMsg;
	}
    }
}

