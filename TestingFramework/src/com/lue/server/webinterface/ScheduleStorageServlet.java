package com.lue.server.webinterface;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lue.common.util.JsonProcessor;
import com.lue.server.ScheduleStorage;
import com.lue.server.SchedulerDataAccessIF;

public class ScheduleStorageServlet extends ApiServlet{
    private static final long serialVersionUID = 1L;

    public ScheduleStorageServlet(SchedulerDataAccessIF dataAccess) {
	super(dataAccess);
    }  

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	response.setContentType("application/json");
	PrintWriter out = response.getWriter();
	out.println(JsonProcessor.toJson(dataAccess.getScheduleStorage()));
	out.close();
    }  
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	response.setContentType("text/html");
	String jsonSchedule = getBody(request);
	ScheduleStorage scheduleStorage = (ScheduleStorage) JsonProcessor.fromJson(jsonSchedule, ScheduleStorage.class);
	try {
	    dataAccess.setScheduleStorage(scheduleStorage);
	} catch (Exception e) {
	    response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, e.getMessage());
	    e.printStackTrace();
	}
    }
}
