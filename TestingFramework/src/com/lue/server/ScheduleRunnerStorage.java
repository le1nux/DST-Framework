package com.lue.server;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.lue.client.ScheduleRunnerIF;

public class ScheduleRunnerStorage {
	protected ArrayList<ScheduleRunnerIF> scheduleRunners;

	public ScheduleRunnerStorage() {
		scheduleRunners = new ArrayList<>();
	}

	public int getCount() {
		int count = 0;
		for(ScheduleRunnerIF r : scheduleRunners)
			count += (r != null)?1:0;
		return count;
	}

	public ScheduleRunnerIF getScheduleRunnerById(int id) {
		if(id < scheduleRunners.size())
			return scheduleRunners.get(id);
		else 
			return null;
	}	

	public List<ScheduleRunnerIF> getScheduleRunners() {
		return scheduleRunners;
	}	

	public void removeScheduleRunnerById(int id) {
		if(id < scheduleRunners.size()) {
			scheduleRunners.set(id, null);
		}
	}	

	public int addScheduleRunner(ScheduleRunnerIF scheduleRunner) {
		scheduleRunners.add(scheduleRunner);
		return scheduleRunners.size()-1;
	}	

	public List<Integer> checkDeadScheduleRunners() {
		ArrayList<Integer> deadRunners = new ArrayList<>();
		for(int i = 0; i < scheduleRunners.size(); i++) {
			ScheduleRunnerIF scheduleRunner = scheduleRunners.get(i);
			if (scheduleRunner != null) {
				try {
					scheduleRunner.isAlive();	// throws RemoteException if connection lost
				} catch (RemoteException e) {
					System.out.println("Lost connection to schedulRunner " + i);
					deadRunners.add(i);
				}
			}
		}
		return deadRunners;
	}
}
