package com.lue.server.webinterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lue.client.NameIF;
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
	List<? extends NameIF> scheduleRunners = dataAccess.getTestRunners();
	List<String> scheduleRunnersRep = new ArrayList<>();
	for(final NameIF client : scheduleRunners){
	    scheduleRunnersRep.add(client.getName());
	}
	out.println(JsonProcessor.toJson(scheduleRunnersRep));
	out.close();
    }   
}