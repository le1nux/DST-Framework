package com.lue.server;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

import org.apache.catalina.LifecycleException;

import com.lue.server.controller.Controller;
import com.lue.server.webinterface.WebServer;

public class Server {
	private static final int WEB_SERVER_PORT = 8080;
	private static final int REGISTRY_PORT = 1099;
	private static final String RMI_HOST_NAME = "127.0.0.1";

	protected Scheduler scheduler;
	protected WebServer webServer;


	public Server() {
		try {
			setUpScheduler();
			new Controller(scheduler);
		} catch (RemoteException | AlreadyBoundException e) {
			e.printStackTrace();
		}
		setUpWebInterface();
	}

	private void setUpScheduler() throws RemoteException, AlreadyBoundException {
		// explanation for this dirty hack: http://www.javacodegeeks.com/2013/11/two-things-to-remember-when-using-java-rmi.html
		System.setProperty("java.rmi.server.hostname", RMI_HOST_NAME);
		scheduler = new Scheduler(REGISTRY_PORT);
	}

	private void setUpWebInterface(){
		// handler to catch possible lifecycle exception thrown by tomcat at start
		Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread th, Throwable ex) {
				System.out.println("Uncaught exception: " + ex);
				System.exit(1);
			}
		};

		webServer = new WebServer(WEB_SERVER_PORT, scheduler);
		Thread t = new Thread(){
			@Override
			public void run() {
				try {
					webServer.startServer();
				} catch (LifecycleException e) {
					e.printStackTrace();
				}
			}
		};
		t.setUncaughtExceptionHandler(h);
		t.start();
	}

	public static void main(String[] args) throws InterruptedException {
		new Server();
		System.out.println("Server started! Press enter to stop the server...");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
