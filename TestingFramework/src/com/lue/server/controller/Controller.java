package com.lue.server.controller;


import java.io.IOException;
import java.util.logging.Logger;

import org.codehaus.jackson.map.JsonMappingException;

import com.lue.client.ScheduleRunner;
import com.lue.common.Result.STATUS;
import com.lue.common.Schedule;
import com.lue.common.TestResult;
import com.lue.common.Schedule.ScheduleElement;
import com.lue.common.util.JsonProcessor;
import com.lue.server.ScheduleStorage;
import com.lue.server.Scheduler;
import com.lue.server.SchedulerDataAccessIF;

public class Controller implements ControllerIF {

	//    private final static String SCHEDULE_PATH = "schedule.json";

	protected SchedulerDataAccessIF dataAccess;
	protected Logger logger;
	public Controller(SchedulerDataAccessIF dataAccess) {
		logger = Logger.getLogger(ScheduleRunner.class.getName());
		this.dataAccess = dataAccess;
		dataAccess.addObserver(this);
		control();
	}


	@Override
	public void control() {
		// ScheduleStorage scheduleStorage;
		//scheduleStorage = ScheduleStorage.generateDebugScheduleStorage(dataAccess.getScheduleRunnerMaxCount());
		//	try {
		//	    writeScheduleToJSONFile(SCHEDULE_PATH, scheduleStorage);
		//	} catch (IOException e1) {
		//	    e1.printStackTrace();
		//	}
		//	try {
		//	    scheduleStorage = readScheduleFromJSONFile(SCHEDULE_PATH);
		//	} catch (IOException e) {
		//	    e.printStackTrace();
		//	}
		//		try {
		//			dataAccess.setScheduleStorage(scheduleStorage);
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}
	}

	public ScheduleStorage readScheduleFromJSONFile(String path) throws JsonMappingException, IOException {
		logger.info("Parsing scheduler from file " + path + "...");
		ScheduleStorage scheduleStorage = (ScheduleStorage) JsonProcessor.jsonFileToObject(path, ScheduleStorage.class);
		return scheduleStorage;	
	}

	public void writeScheduleToJSONFile(String path, ScheduleStorage scheduleStorage) throws IOException {
		logger.fine("Writing scheduler to file " + path + "...");
		JsonProcessor.objectToJsonFile(path, scheduleStorage);
	}

	@Override
	public void update() {
		System.out.println("State changed to: " + dataAccess.getState());
		if(dataAccess.getState() == Scheduler.STATE.INITIALIZED){
			try {	// TODO check these lines ...
				//		dataAccess.setState(Scheduler.STATE.RUNNING);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(dataAccess.getState() == Scheduler.STATE.FINISHED){
			ScheduleStorage c = dataAccess.getScheduleStorage();
			evaluateResult(c);
		}
	}

	private void evaluateResult(ScheduleStorage cs) {
		String evaluation = "";
		for(Schedule schedule : cs) {	// for each scheduleRunner:
			evaluation += "scheduleRunner: " + schedule.getScheduleRunnerId() + "\n";
			for(ScheduleElement scheduleElement : schedule.getSchedule()) { 	// for each performed test:
				evaluation += "\tTest Identifier: " + scheduleElement.getTestKey() + "\n";
				TestResult testResult = scheduleElement.getTestResult();
				evaluation += testResultsToString("\t\t", testResult);
			}
		}
		logger.info("RESULT:\n" + evaluation);
	}

	private String testResultsToString(String tabOffset, TestResult testResult) {
		if(testResult == null) {
			return "";
		}
		String resultString = "";
		resultString += tabOffset + testResult.getTestKey() + ": " + testResult.getStatus() + "\t";

		if(testResult.getStatus() == STATUS.ERROR) {
			resultString += "ERROR: " + testResult.getErrorMessage() + "\n"; 
		} else if(testResult.getStatus() == STATUS.FAILURE) {
			resultString += "FAILURE: " + testResult.getFailureMessage()  + "\n"; 
		}else if(testResult.getStatus() == STATUS.SUCCESS) {
			resultString += "\n";
		}
		for(TestResult t : testResult.getTestSubResults()){
			resultString += testResultsToString(tabOffset+"\t", t);
		}
		return resultString;
	}
}
