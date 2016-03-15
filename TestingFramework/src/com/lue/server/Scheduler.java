package com.lue.server;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lue.client.ScheduleRunnerIF;
import com.lue.common.Result;
import com.lue.common.Schedule;
import com.lue.common.Schedule.ScheduleElement;
import com.lue.common.SupportedTests;
import com.lue.common.TestResult;
import com.lue.common.UnitTestException;
import com.lue.common.util.ObserverIF;

public class Scheduler implements SchedulerRmiIF, SchedulerDataAccessIF{
	public static final String SCHEDULER_RMI_NAME = "Scheduler";
	private Logger logger;

	public static enum STATE {UNINITIALIZED, INITIALIZED, RUNNING, FINISHED};

	protected ScheduleRunnerStorage scheduleRunners;
	protected SupportedTests supportedTests;
	protected Registry registry;
	protected ScheduleStorage scheduleStorage;
	protected int scheduleRunnerCount;
	protected int registryPort;
	protected STATE state;
	protected List<ObserverIF> observers;

	public Scheduler(int scheduleRunnerCount, int registryPort) throws RemoteException, AlreadyBoundException {
		state = STATE.UNINITIALIZED;
		this.scheduleRunnerCount = scheduleRunnerCount;
		this.registryPort = registryPort;
		scheduleRunners = new ScheduleRunnerStorage();
		observers = new ArrayList<>();
		setUpLogger();
		setUpRegistry();
		logger.log(Level.INFO, "Scheduler instantiated. Waiting for scheduleRunners to connect...");
		runScheduleRunnerAliveThread();
	}

	private void setUpLogger() {
		logger = Logger.getLogger(Scheduler.class.getName());
		logger.setLevel(Level.ALL);
		logger.getParent().getHandlers()[0].setLevel(Level.ALL);
	}

	private void setUpRegistry() throws RemoteException, AlreadyBoundException {
		SchedulerRmiIF stub = (SchedulerRmiIF) UnicastRemoteObject.exportObject(this, 0);
		registry = LocateRegistry.createRegistry(registryPort);
		registry.bind(SCHEDULER_RMI_NAME, stub);
		logger.log(Level.INFO, SCHEDULER_RMI_NAME + " bound");
	}

	protected void stopRegistry() {
		try {        
			registry.unbind(SCHEDULER_RMI_NAME);
			UnicastRemoteObject.unexportObject(this, true);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void setState(STATE state) throws Exception {
		switch(state) {
		case UNINITIALIZED:
			this.state = state;
			break;
		case INITIALIZED:
			if(! isInitialized() || this.state != STATE.UNINITIALIZED && this.state != STATE.INITIALIZED){
				throw new Exception("Error: Cannot set state to 'INITIALIZED'. Either scheduler isInititlized check failed (scheduleRunners connected and scheduler set) OR the current state is not unitialized");
			}
			this.state = state;
			break;
		case RUNNING:
			if(this.state != STATE.INITIALIZED && this.state != STATE.RUNNING)
				throw new Exception("Error: Cannot set state to 'RUNNING' because the current state is not 'INITIALIZED' or is already 'RUNNING'");
			this.state = state;
			runSchedule();
			break;
		case FINISHED:
			if(!isFinished() || this.state != STATE.RUNNING && this.state != STATE.FINISHED)
				throw new Exception("Error: Cannot set state to 'FINISHED'. Either not all of the scheduleRunners sent its results to the server, yet OR the current state is not 'RUNING'");
			this.state = state;
			break;
		}
		logger.log(Level.INFO, "State changed to " + this.state);
		notifyObservers();
	}

	@Override
	public int registerScheduleRunner(ScheduleRunnerIF scheduleRunner) throws Exception {
		int id = -1;
		if(scheduleRunnerCount > scheduleRunners.getCount()){
			id = scheduleRunners.addScheduleRunner(scheduleRunner);	
			if(supportedTests == null){	// TODO needs to get tested!
				supportedTests = scheduleRunner.getSupportedTests();
			}
			supportedTests.intersectSupportedTests(scheduleRunner.getSupportedTests());
			logger.log(Level.INFO, "" + scheduleRunners.getCount() + " scheduleRunners connected.");
		}
		else {
			throw new Exception("Error: scheduleRunner connection rejected! Already " + scheduleRunnerCount +  "/" + scheduleRunners.getCount() + " connected.");
		}
		new Thread() {		// TODO need to think this through!
			public void run() {
				if(isInitialized()) {
					try {
						setState(STATE.INITIALIZED);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		return id;
	}

	@Override
	public void unregisterScheduleRunner(ScheduleRunnerIF scheduleRunner) {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void pushTestResult(int scheduleRunnerId, int testId, TestResult testResult) {
		logger.log(Level.FINE, "TestRunner " + scheduleRunnerId + " ran test " + testId + (testResult.getStatus().equals(Result.STATUS.SUCCESS)?" successfully":" FAULTILY"));
		scheduleStorage.getSchedule(scheduleRunnerId).getScheduleElement(testId).setTestResult(testResult);
		if(isFinished()){
			try {
				setState(STATE.FINISHED);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * is initialized if all scheduleRunners are connected and schedule is present
	 */
	public boolean isInitialized() {
		return scheduleRunners.getCount() == scheduleRunnerCount && scheduleStorage != null;
	}

	private boolean isFinished() {
		for(Schedule s : scheduleStorage){
			for(ScheduleElement e : s) {
				if(! e.hasTestResult()){
					System.out.println(e.getTestKey() +  " not finished!");
					return false;
				}
			}
		}
		logger.log(Level.INFO, "All tests ran through!");
		return true;
	}

	private void runSchedule() {
		logger.log(Level.INFO, "Pushing schedule...");
		int counter = 0;
		for(ScheduleRunnerIF scheduleRunner : scheduleRunners){
			Schedule s = scheduleStorage.getSchedule(counter);
			try {
				scheduleRunner.pushSchedule(s);
			} catch (RemoteException | UnitTestException e) {
				e.printStackTrace();
			}	    
			counter ++;
		}
		logger.log(Level.INFO, "Running schedule...");;
		for(ScheduleRunnerIF scheduleRunner : scheduleRunners) {
			try {
				scheduleRunner.runSchedule();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}	
	}

	private void resetSchedules() {
		scheduleStorage = null;
	}

	@Override
	public void setScheduleStorage(ScheduleStorage scheduleStorage) throws Exception {
		if(scheduleStorage.getSchedules().size() == scheduleRunnerCount){
			this.scheduleStorage = scheduleStorage;
		}else {
			throw new Exception("ERROR: Schedule count does not match scheduleRunner count!");
		}
		if(isInitialized()) {
			try {
				setState(STATE.INITIALIZED);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized SupportedTests getSupportedTests() {
		return supportedTests;
	}

	public synchronized List<ScheduleRunnerIF> getTestRunners() {
		return scheduleRunners.getScheduleRunners();
	}

	/*
	 * Deletes the schedule at the scheduleRunners' side
	 */
	private void abortRunningTest() {		// TODO might be deleted...
		for(int scheduleRunnerId = 0; scheduleRunnerId < scheduleRunners.getCount(); scheduleRunnerId++) {
			try {
				scheduleRunners.getScheduleRunnerById(scheduleRunnerId).resetSchedule();
			} catch (RemoteException e){
				e.printStackTrace();
			}
		}
	}

	private void runScheduleRunnerAliveThread() {
		new Thread() {
			public void run() {
				while(true) {
					List<Integer> deadRunners = scheduleRunners.checkDeadScheduleRunners();
					for(int id : deadRunners)
						scheduleRunners.removeScheduleRunnerById(id);
					if(state != STATE.UNINITIALIZED && !isInitialized()) {
						try {
							setState(STATE.UNINITIALIZED);
						} catch (Exception e) {
							System.out.println("ERROR: Couldn't set state to uninitialized!");
							e.printStackTrace();
						}
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	@Override
	public void addObserver(ObserverIF o) {
		observers.add(o);
	}

	@Override
	public void removeObserver(ObserverIF o) {
		observers.remove(o);
	}

	@Override
	public void notifyObservers() {
		for(ObserverIF o : observers)
			o.update();
	}

	@Override
	public STATE getState() {
		return state;
	}

	@Override
	public ScheduleStorage getScheduleStorage() {
		return scheduleStorage;
	}

	@Override
	public int getScheduleRunnerCount() {
		return scheduleRunners.getCount();
	}

	@Override
	public int getScheduleRunnerMaxCount() {
		return scheduleRunnerCount;
	}
}
