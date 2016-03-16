package com.lue.server.webinterface;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lue.common.util.JsonProcessor;
import com.lue.server.SchedulerDataAccessIF;

public class SupportedTestsServlet extends ApiServlet{
    private static final long serialVersionUID = 1L;

    public SupportedTestsServlet(SchedulerDataAccessIF dataAccess) {
	super(dataAccess);
    }  

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	response.setContentType("application/json");
	PrintWriter out = response.getWriter();
	out.println(JsonProcessor.toJson("API ENDPOINT NOT SUPPORTED ANYMORE"));
	out.close();
    }  
}
