package com.lue.client;

import java.rmi.RemoteException;

import com.lue.client.tests.Test;
import com.lue.client.tests.TestParameters;
import com.lue.common.util.ObserverRmiIF;
import com.lue.server.SchedulerRmiIF;

public class TestCompletedObserver implements ObserverRmiIF{
    protected Test<? extends TestParameters> test;		// subject to be observed
    protected SchedulerRmiIF callbackInstance;
    protected int scheduleRunnerId;
    
    public TestCompletedObserver(int scheduleRunnerId, Test<? extends TestParameters> test, SchedulerRmiIF callbackInstance) {
	this.test = test;
	this.scheduleRunnerId = scheduleRunnerId;
	this.callbackInstance = callbackInstance;
    }

    @Override
    public void update() throws RemoteException {
	System.out.println("updating scheduleRunner:" + scheduleRunnerId + " testId:" + test.getTestId() + " result:" + test.getTestResult().getStatus());
	callbackInstance.pushTestResult(scheduleRunnerId, test.getTestId(), test.getTestResult());
    }
}
