package com.lue.server;

import java.util.List;

import com.lue.client.ScheduleRunnerIF;
import com.lue.common.util.ObservableIF;

public interface SchedulerDataAccessIF extends ObservableIF {
    public List<? extends ScheduleRunnerIF> getScheduleRunners();
    
    public ScheduleStorage getScheduleStorage();
    public void setScheduleStorage(ScheduleStorage scheduleStorage) throws Exception;

    public boolean isInitialized();
    public Scheduler.STATE getState();
    public void setState(Scheduler.STATE state) throws Exception;
 }
