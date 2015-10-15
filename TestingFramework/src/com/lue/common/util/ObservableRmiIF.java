package com.lue.common.util;

public interface ObservableRmiIF {
    public void addObserver(ObserverRmiIF o);
    public void removeObserver(ObserverRmiIF o);
    public void notifyObservers();
}
