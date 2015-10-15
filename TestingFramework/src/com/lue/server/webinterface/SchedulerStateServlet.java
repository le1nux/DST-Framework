package com.lue.server.webinterface;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lue.common.util.JsonProcessor;
import com.lue.server.Scheduler;
import com.lue.server.SchedulerDataAccessIF;

public class SchedulerStateServlet extends ApiServlet{
    private static final long serialVersionUID = 1L;

    public SchedulerStateServlet(SchedulerDataAccessIF dataAccess) {
	super(dataAccess);
    }  

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	response.setContentType("application/json");
	PrintWriter out = response.getWriter();
	Scheduler.STATE state = dataAccess.getState();
	out.println(JsonProcessor.toJson(state));
	out.close();
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	response.setContentType("application/json");
	String jsonSchedule = getBody(request);
	Scheduler.STATE state = (Scheduler.STATE) JsonProcessor.fromJson(jsonSchedule, Scheduler.STATE.class);
	try {
	    dataAccess.setState(state);
	} catch (Exception e) {
	    response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, e.getMessage());
	}
    }
}
