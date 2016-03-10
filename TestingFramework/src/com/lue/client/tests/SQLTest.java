package com.lue.client.tests;

import com.lue.common.Result.STATUS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.lue.common.TestResult;

public class SQLTest extends Test<SQLTestParameters>{
	public static final String TEST_IDENTIFIER = "SQLTest";
	private static final long serialVersionUID = 1L;
	protected Connection con; 

	@TestConstructor
	public SQLTest(Integer testId, SQLTestParameters parameters) throws IllegalArgumentException, IllegalAccessException {
		super(testId, parameters);
	}

	@Override
	protected TestResult runTestImpl() throws Exception {
		TestResult result = new TestResult(TEST_IDENTIFIER);
		result.setStart(System.currentTimeMillis());
		// Do the sql test here
		Statement statement = con.createStatement();
		ResultSet rs = statement.executeQuery(parameters.getQuery());
		String queryResult = "";
		if(rs.next()) {
			queryResult = rs.getString(1);				// TODO: might be a little limiting...
			System.out.println("QUERY RESULT: " + queryResult);
		}
		result.setEnd(System.currentTimeMillis());
		if(queryResult.equals(parameters.getResult())) {
			result.setStatus(STATUS.SUCCESS);
		} else {
			result.setStatus(STATUS.FAILURE);
		}
		return result;	
	}

	@Override
	protected String getTestIdentifier() {
		return TEST_IDENTIFIER;
	}

	@Override
	protected void before() {
		try {
			con = openConnection(parameters.getDriver(), parameters.getDatabaseURL(), parameters.getUser(), parameters.getPassword());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void after() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected Connection openConnection(String driver, String databaseURL, String user, String password) throws SQLException  {
		// loading the driver
		if(!driver.equals("")) {
			try {
				Class.forName(driver);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		con = DriverManager.getConnection(databaseURL, user, password);
		return con;
	}
}