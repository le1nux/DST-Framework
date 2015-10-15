package com.lue.server.webinterface;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.lue.server.SchedulerDataAccessIF;

public abstract class ApiServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    protected SchedulerDataAccessIF dataAccess;

    public ApiServlet(SchedulerDataAccessIF dataAccess) {
	this.dataAccess = dataAccess;
    }  
    
    protected String getBody(HttpServletRequest request) throws IOException {
	StringBuilder buffer = new StringBuilder();
	BufferedReader reader = request.getReader();
	String line;
	while ((line = reader.readLine()) != null) {
	    buffer.append(line);
	}
	String data = buffer.toString();
	return data;
    }
    

}
