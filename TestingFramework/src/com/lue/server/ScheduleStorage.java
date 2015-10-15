package com.lue.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.lue.client.tests.SleepTestParameters;
import com.lue.common.Schedule;
import com.lue.common.Schedule.ScheduleElement;

@XmlRootElement
public class ScheduleStorage implements Iterable<Schedule>{
    @XmlElement
    protected List<Schedule> schedules;

    public ScheduleStorage() {
	schedules = new ArrayList<Schedule>();	
    }

    public static Schedule generateDebugSchedule() {
	Schedule schedule= new Schedule();	
	SleepTestParameters testParameters = new SleepTestParameters(2000);
	ScheduleElement sleepTest = new ScheduleElement("com.lue.client.tests.SleepTest", testParameters, null);	
	schedule.addScheduleElement(sleepTest);
	
	ScheduleElement instrumentatinTest = new ScheduleElement("com.lue.client.tests.android.InstrumentationTest", null, null);	
	schedule.addScheduleElement(instrumentatinTest);
	return schedule;
    }

    public static ScheduleStorage generateDebugScheduleStorage(int clientCount) {
	ScheduleStorage cSchedule = new ScheduleStorage();
	for(int i = 0; i < clientCount; i++){
	    cSchedule.addEmptySchedule();
	    cSchedule.putSchedule(i, generateDebugSchedule());
	}
	return cSchedule;
    }

    private void addEmptySchedule() {
	schedules.add(new Schedule());
    }
    

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public Schedule getSchedule(int scheduleRunnerId) {
	if (schedules.size() > scheduleRunnerId)
	    return schedules.get(scheduleRunnerId);
	return null;
    }

    public boolean putSchedule(int scheduleRunnerId, Schedule schedule) {
	if(schedules.size()<= scheduleRunnerId)
	    return false;
	schedules.set(scheduleRunnerId, schedule);
	return true;
    }

    public Iterator<Schedule> iterator() {
	return new ScheduleIterator();
    }

    private class ScheduleIterator implements Iterator<Schedule> {
	int cursor;

	public ScheduleIterator() {
	    cursor = 0;
	}

	@Override
	public boolean hasNext() {
	    return cursor < schedules.size();
	}

	@Override
	public Schedule next() {
	    return schedules.get(cursor++);
	}
	
	@Override
	public void remove() {
	    throw new UnsupportedOperationException();
	}
    }
}
