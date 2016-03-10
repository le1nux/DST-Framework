package com.lue.client.tests;

public class SQLTestParameters extends TestParameters {
	private static final long serialVersionUID = 1L;

	protected String driver; // com.mysql.jdbc.Driver 
	protected String user;
	protected String password;
	protected String databaseURL;
	protected String query;
	protected String result;

	public SQLTestParameters() {

	}

	public SQLTestParameters(String driver, String user, String password, String databaseURL, String query, String result) {
		this.driver = driver;
		this.user = user;
		this.password = password;
		this.databaseURL = databaseURL;
		this.query = query;
		this.result = result;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatabaseURL() {
		return databaseURL;
	}

	public void setDatabaseURL(String databaseURL) {
		this.databaseURL = databaseURL;
	}	
}