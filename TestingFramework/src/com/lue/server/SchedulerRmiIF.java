package com.lue.server;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import com.lue.client.ScheduleRunnerIF;
import com.lue.common.TestResult;

public interface SchedulerRmiIF extends Remote {
    /**
     * registers scheduleRunner and returns its ID
     * @throws AlreadyBoundException 
     * @throws Exception 
     */
    void registerScheduleRunner(ScheduleRunnerIF scheduleRunner) throws RemoteException, AlreadyBoundException, Exception;
    public void pushTestResult(int scheduleRunnerId, int testId, TestResult testResult) throws RemoteException;
    
    public void unregisterScheduleRunner(ScheduleRunnerIF scheduleRunner) throws RemoteException;
}
