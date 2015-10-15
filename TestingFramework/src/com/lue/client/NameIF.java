package com.lue.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.codehaus.jackson.annotate.JsonValue;

public interface NameIF extends Remote{
    @JsonValue
    public String getName() throws RemoteException;
}
