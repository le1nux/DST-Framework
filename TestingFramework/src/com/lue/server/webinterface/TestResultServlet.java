package com.lue.server.webinterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lue.common.Schedule;
import com.lue.common.Schedule.ScheduleElement;
import com.lue.common.TestResult;
import com.lue.common.util.JsonProcessor;
import com.lue.server.ScheduleStorage;
import com.lue.server.SchedulerDataAccessIF;
import com.lue.server.webinterface.TestResultServlet.ScheduleRunResult.ScheduleRunner;

public class TestResultServlet extends ApiServlet{
    private static final long serialVersionUID = 1L;

    public TestResultServlet(SchedulerDataAccessIF dataAccess) {
	super(dataAccess);
    }  

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	response.setContentType("application/json");
	PrintWriter out = response.getWriter();
	ScheduleStorage scheduleStorage = dataAccess.getScheduleStorage();
	out.println(JsonProcessor.toJson(getResultsFromScheduleStorage(scheduleStorage)));
	out.close();
    }
    
    private ScheduleRunResult getResultsFromScheduleStorage(ScheduleStorage scheduleStorage) {
	ScheduleRunResult scheduleRunResult = new ScheduleRunResult();
	
	for(Schedule schedule : scheduleStorage) {
	    ScheduleRunner scheduleRunner = new ScheduleRunner(schedule.getScheduleRunnerId());
	    scheduleRunResult.scheduleRunners.add(scheduleRunner);
	    for(ScheduleElement scheduleElement : schedule) {
		String key = scheduleElement.getTestKey();
		TestResult testResult = scheduleElement.getTestResult();
		scheduleRunner.testResults.add(testResult);
	    }
	}
	
	return scheduleRunResult;
	
    }

    public static class ScheduleRunResult implements Serializable {
	private static final long serialVersionUID = 1L;
	List<ScheduleRunner> scheduleRunners;
	
	public ScheduleRunResult() {
	    scheduleRunners = new ArrayList<>();
	}
	
	public static class ScheduleRunner implements Serializable {
	    private static final long serialVersionUID = 1L;
	    int scheduleRunnerId;
	    List<TestResult> testResults;
	    
	    public ScheduleRunner(int scheduleRunnerId) {
		this.scheduleRunnerId = scheduleRunnerId;
		testResults = new ArrayList<TestResult>();
	    }
	    public int getScheduleRunnerId() {
	        return scheduleRunnerId;
	    }
	    public void setTestRunner(int scheduleRunnerId) {
	        this.scheduleRunnerId = scheduleRunnerId;
	    }
	    public List<TestResult> getTestResults() {
	        return testResults;
	    }
	    public void setTestResults(List<TestResult> testResults) {
	        this.testResults = testResults;
	    }
	}
    }
}
