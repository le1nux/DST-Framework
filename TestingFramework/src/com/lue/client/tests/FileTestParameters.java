package com.lue.client.tests;

/**
 * data class for the FileTest parameters
 *
 */
public class FileTestParameters  extends TestParameters {
	private static final long serialVersionUID = 1L;
	String filePath;
	String regex;
	int timeout; // in milli seconds
	
	public FileTestParameters(String filePath, String regex, int timeout) {
		this.filePath = filePath;
		this.regex = regex;
		this.timeout = timeout;
	}
	
	public FileTestParameters() {
		
	}

	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}