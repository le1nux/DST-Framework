package com.lue.client;

import java.rmi.RemoteException;


import com.lue.common.Schedule;
import com.lue.common.SupportedTests;
import com.lue.common.UnitTestException;

public interface ScheduleRunnerIF extends NameIF {
    public SupportedTests getSupportedTests() throws RemoteException;
    public void pushSchedule(Schedule schedule) throws RemoteException, UnitTestException; 
    public void resetSchedule() throws RemoteException; 
    public void runSchedule() throws RemoteException;
    
    public boolean isAlive() throws RemoteException;

    public int getId() throws RemoteException;
}
