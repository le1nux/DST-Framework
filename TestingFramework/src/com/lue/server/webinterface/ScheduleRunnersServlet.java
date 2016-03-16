package com.lue.server.webinterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lue.client.ScheduleRunnerIF;
import com.lue.common.SupportedTests;
import com.lue.common.util.JsonProcessor;
import com.lue.server.SchedulerDataAccessIF;

public class ScheduleRunnersServlet extends ApiServlet{
	private static final long serialVersionUID = 1L;

	public ScheduleRunnersServlet(SchedulerDataAccessIF dataAccess) {
		super(dataAccess);
	}  

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		List<? extends ScheduleRunnerIF> scheduleRunners = dataAccess.getScheduleRunners();
		List<Object> scheduleRunnersRep = new ArrayList<>();
		for(final ScheduleRunnerIF scheduleRunner : scheduleRunners){
			scheduleRunnersRep.add(new ScheduleRunnerRepresentation(scheduleRunner));
		}
		out.println(JsonProcessor.toJson(scheduleRunnersRep));
		out.close();
	}  


	private class ScheduleRunnerRepresentation{
		@SuppressWarnings("unused")
		int scheduleRunnerId;
		@SuppressWarnings("unused")
		SupportedTests supportedTests;

		public ScheduleRunnerRepresentation(ScheduleRunnerIF scheduleRunner) throws RemoteException {
			scheduleRunnerId = scheduleRunner.getId();
			supportedTests = scheduleRunner.getSupportedTests();
		}		
	}
}