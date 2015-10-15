package com.lue.common.util;

import java.rmi.RemoteException;

public interface ObserverRmiIF {
    public void update() throws RemoteException;
}
