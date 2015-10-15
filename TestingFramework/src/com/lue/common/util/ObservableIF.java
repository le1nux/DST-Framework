package com.lue.common.util;

public interface ObservableIF {
    public void addObserver(ObserverIF o);
    public void removeObserver(ObserverIF o);
    public void notifyObservers();
}
