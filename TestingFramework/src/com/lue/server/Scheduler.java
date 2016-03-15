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
	protected int registryPort;
	protected STATE state;
	protected List<ObserverIF> observers;

	public Scheduler(int registryPort) throws RemoteException, AlreadyBoundException {
		state = STATE.UNINITIALIZED;
		this.registryPort = registryPort;
		scheduleRunners = new ScheduleRunnerStorage();
		observers = new ArrayList<>();
		setUpLogger();
		setUpRegistry();
		runScheduleRunnerAliveThread();
		System.out.println("Scheduler instantiated!");
		isInitialized(); // to print the first line that tells the user what is missing.
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
	public void registerScheduleRunner(ScheduleRunnerIF scheduleRunner) throws Exception {
		scheduleRunners.addScheduleRunner(scheduleRunner);	// this throws an exception if scheduleRunner already exists with given id!
		if(supportedTests == null){	// TODO needs to get tested!
			supportedTests = scheduleRunner.getSupportedTests();
		}
		supportedTests.intersectSupportedTests(scheduleRunner.getSupportedTests());
		logger.log(Level.INFO, "ScheduleRunner " + scheduleRunner.getId() + " connected.");
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
		boolean status = true;
		if(scheduleStorage == null){
			status = false;
			System.out.println("SCHEDULER STATUS: NOT INITIALIZED");
			System.out.println("\tSchedules not set, yet!");
		}
		else {
			for(Schedule s : scheduleStorage) {
				int id = s.getScheduleRunnerId();
				if(scheduleRunners.getScheduleRunnerById(id) == null) {
					if(status == true){
						System.out.println("SCHEDULER STATUS: NOT INITIALIZED");
						System.out.println("\tSchedules: set");
					}
					System.out.println("\tMissing ScheduleRunner: " + id);
					status = false;
				}
			}
		}
		return status;
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
		for(Schedule s : scheduleStorage){
			ScheduleRunnerIF scheduleRunner = scheduleRunners.getScheduleRunnerById(s.getScheduleRunnerId());
			try {
				scheduleRunner.pushSchedule(s);
			} catch (RemoteException | UnitTestException e) {
				e.printStackTrace();
			}	    
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
		this.scheduleStorage = scheduleStorage;
		logger.info("Accepted new schedule.");
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

	public synchronized List<ScheduleRunnerIF> getScheduleRunners() {
		return scheduleRunners.getScheduleRunners();
	}

	/*
	 * Deletes the schedule at the scheduleRunners' side
	 */
//	private void abortRunningTest() {		// TODO might be deleted...
//		for(int scheduleRunnerId = 0; scheduleRunnerId < scheduleRunners.getCount(); scheduleRunnerId++) {
//			try {
//				scheduleRunners.getScheduleRunnerById(scheduleRunnerId).resetSchedule();
//			} catch (RemoteException e){
//				e.printStackTrace();
//			}
//		}
//	}

	private void runScheduleRunnerAliveThread() {
		new Thread() {
			public void run() {
				while(true) {
					List<Integer> deadRunners = scheduleRunners.checkDeadScheduleRunners();
					for(int id : deadRunners){
						scheduleRunners.removeScheduleRunnerById(id);
						System.out.println("Removed ScheduleRunner " + id + " due to connection loss.");
					}						
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
}
