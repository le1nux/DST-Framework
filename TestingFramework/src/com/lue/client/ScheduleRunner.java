package com.lue.client;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lue.client.tests.Test;
import com.lue.client.tests.TestIF;
import com.lue.client.tests.TestParameters;
import com.lue.common.Schedule;
import com.lue.common.Schedule.ScheduleElement;
import com.lue.common.SupportedTests;
import com.lue.common.UnitTestException;
import com.lue.server.Scheduler;
import com.lue.server.SchedulerRmiIF;

public class ScheduleRunner extends UnicastRemoteObject implements ScheduleRunnerIF {
    private static final long serialVersionUID = 1L;

    public Logger logger;
    protected SchedulerRmiIF schedulerIF;
    protected int scheduleRunnerId;
    protected SupportedTests supportedTests;
    protected Schedule schedule;
    protected List<TestIF> testQueue;
    protected Settings settings;
    protected Registry registry;

    public ScheduleRunner() throws Exception {
	logger = Logger.getLogger(ScheduleRunner.class.getName());
	testQueue = new ArrayList<TestIF>();
	supportedTests = getSupportedTests();
	}

    public void connectToScheduler(String host, int port) throws Exception {
	scheduleRunnerId = -1;
	logger.log(Level.INFO, "Connecting to server " + host + " on port " + port + "...");
	Registry registry = LocateRegistry.getRegistry(host, port);
	schedulerIF = (SchedulerRmiIF) registry.lookup(Scheduler.SCHEDULER_RMI_NAME);
	scheduleRunnerId = schedulerIF.registerScheduleRunner(this);
	logger.log(Level.INFO, "Connected. Client ID is: " + scheduleRunnerId);

    }

    public void disconnectFromScheduler() {
	try {
	    UnicastRemoteObject.unexportObject(this, true);
	} catch (NoSuchObjectException e) {
	    e.printStackTrace();
	}	
    }


    @Override
    public SupportedTests getSupportedTests() {
	if(supportedTests == null) {
	    supportedTests = Settings.fromXML().getSupportedTests();
	}
	return supportedTests;
    }

    @Override
    public void pushSchedule(Schedule schedule) throws RemoteException, UnitTestException {
	while(scheduleRunnerId == -1) {		// waiting until scheduleRunnerId is set correctly ... otherwise the server starts its tests before the client even got its id back from registerclient... TODO quite dirty...
	    try {
		Thread.sleep(10);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }

	}

	// checking if all tests are supported
	logger.log(Level.INFO, "Receiving schedule..." + " client id is: " + scheduleRunnerId);
	for(ScheduleElement t : schedule) {
	    if(!supportedTests.isTestSupported(t.getTestKey()))
		throw new UnitTestException("Test " + t.getTestKey() + " is NOT supported!");
	}	
	// after making sure all tests are supported we store the schedule in a local field
	this.schedule = schedule;
	// when generating a new testQueue we need to empty the old one.
	testQueue.clear();
	int testId = 0;
	for(ScheduleElement t : schedule) {
	    String testClass = t.getTestKey();
	    TestParameters params = t.getParameters();
	    Test<? extends TestParameters> test = TestBuilder.build(testClass, testId, params);
	    test.addObserver(new TestCompletedObserver(scheduleRunnerId, test, schedulerIF));
	    testQueue.add(test);
	    testId++;
	}
	logger.log(Level.INFO, "received");
    }

    @Override
    public void resetSchedule() throws RemoteException {
	schedule = null;
    }

    @Override
    public void runSchedule() throws RemoteException {
	new Thread(){
	    public void run() {
		for(TestIF t : testQueue) {
		    t.execute();		// executes test and sends result automatically to server
		}		
	    }
	}.start();
    }

    public static void main(String[] args) throws Exception {
	ScheduleRunner scheduleRunner = new ScheduleRunner();
	scheduleRunner.logger.log(Level.INFO, "logger works!");
	try{
	    scheduleRunner.connectToScheduler("127.0.0.1", 1099);
	}catch(Exception e){
	    scheduleRunner.disconnectFromScheduler();
	    throw e;
	}
	do { 		
	    try {
		Thread.sleep(10000);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	} while (true);
    }

    @Override
    public String getName() {
	return "HardCoded Name";
    }
}
