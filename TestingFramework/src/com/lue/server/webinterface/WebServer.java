package com.lue.server.webinterface;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import com.lue.server.SchedulerDataAccessIF;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

public class WebServer {
    protected Tomcat tomcat;
    protected Context ctx;
    protected SchedulerDataAccessIF dataAccess;
    
    public WebServer(int port, SchedulerDataAccessIF dataAccess){
	this.dataAccess = dataAccess;
	tomcat = new Tomcat();
	tomcat.setPort(port);
	ctx = tomcat.addContext("/", new File(".").getAbsolutePath());
	addServlets();
    }
    
    private void addServlets() {
	
	// static HTML content
	

	
	
	// API
	Tomcat.addServlet(ctx, "api_info", new HttpServlet() {
	    private static final long serialVersionUID = 1L;
	    protected void service(HttpServletRequest req, HttpServletResponse resp) 
		    throws ServletException, IOException {
		Writer w = resp.getWriter();
		w.write("<h2>RESTful Ressources</h2>" + "</br>");
		w.write(API.SCHEDULE_STORAGE + "</br>");
		w.write(API.SCHEDULE_RUNNERS + "</br>");
		w.write(API.SCHEDULER_STATE + "</br>");
		w.write(API.SUPPORTED_TESTS + "</br>");
		w.flush();
	    }
	});
	ctx.addServletMapping("/api/*", "api_info");
	
	Tomcat.addServlet(ctx, "ClientsSchedule", new ScheduleStorageServlet(dataAccess));
	ctx.addServletMapping(API.SCHEDULE_STORAGE, "ClientsSchedule");
	
	Tomcat.addServlet(ctx, "clients", new ScheduleRunnersServlet(dataAccess));
	ctx.addServletMapping(API.SCHEDULE_RUNNERS, "clients");

	Tomcat.addServlet(ctx, "schedulerstate", new SchedulerStateServlet(dataAccess));
	ctx.addServletMapping(API.SCHEDULER_STATE, "schedulerstate");
	
	Tomcat.addServlet(ctx, "supportedtests", new SupportedTestsServlet(dataAccess));
	ctx.addServletMapping(API.SUPPORTED_TESTS, "supportedtests");
	
	Tomcat.addServlet(ctx, "testresults", new TestResultServlet(dataAccess));
	ctx.addServletMapping(API.TEST_RESULTS, "testresults");
    }
    
    public void startServer() throws LifecycleException {
	tomcat.start();
	tomcat.getServer().await();
    }

}

// clientsSchedule clientIF clients testRunner test