package com.lue.client.tests;

import com.lue.common.TestResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.lue.common.Result.STATUS;

/**
 * Test checks whether a given file contains the correct data
 */
public class FileTest extends Test<FileTestParameters>{
	public static final String TEST_IDENTIFIER = "FileTest";
	private static final long serialVersionUID = 1L;

	@TestConstructor
	public FileTest(Integer testId, FileTestParameters parameters) throws IllegalArgumentException, IllegalAccessException {
		super(testId, parameters);
	}

	@Override
	protected TestResult runTestImpl() throws Exception {
		TestResult result = new TestResult(TEST_IDENTIFIER);
		result.setStart(System.currentTimeMillis());
		// do the file test here
		if(! waitForFile(parameters.getFilePath(), parameters.getTimeout())) {
			result.setFailureMessage("File does not exist!");
			result.setStatus(STATUS.FAILURE);
		}
		else {
			List<Integer> matches = matchesRegex(parameters.getFilePath(), parameters.getRegex());
			if(matches.size() > 0){
				result.setStatus(STATUS.SUCCESS);
			} else {
				result.setStatus(STATUS.FAILURE);
			}
		}
		result.setEnd(System.currentTimeMillis());
		return result;	
	}


	/**
	 * Checks if the file given by the filePath contains and does ONLY contain the given content
	 * @param content
	 * @param filePath
	 * @param timeout
	 * @return
	 */
	protected boolean waitForFile(String filePath, int timeout) {
		if(! fileExists(filePath) && parameters.getTimeout() < 1) { // file does not exist and no timeout is set.
			return false;
		} else {
			int waitedTime = 0;
			int checkDelay = 100;
			while(!fileExists(filePath) && timeout > waitedTime) {
				try {
					Thread.sleep(checkDelay);	// going to sleep for 100ms
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				waitedTime += checkDelay;
			}
			return (timeout > waitedTime);	// if true the file exists!
		}
	}

	protected boolean fileExists(String filePath) {
		File f = new File(filePath);
		return (f.exists() && !f.isDirectory());
	}

	protected List<Integer> matchesRegex(String filePath, String regex) throws Exception {
		List<Integer> lineNumbers = new ArrayList<>();
		Scanner scanner = null;
		try {
			File file = new File(filePath);
			scanner = new Scanner(file);
			int lineNumber = 1;
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if(line.matches(regex)) {
					lineNumbers.add(lineNumber);
				}
				lineNumber++;
				
			}
			scanner.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			scanner.close();
		}
		return lineNumbers;		
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