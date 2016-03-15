package com.lue.server;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.lue.client.ScheduleRunnerIF;

public class ScheduleRunnerStorage implements Iterable<ScheduleRunnerIF>{
	protected TreeMap<Integer, ScheduleRunnerIF> scheduleRunners;

	public ScheduleRunnerStorage() {
		scheduleRunners = new TreeMap<>();
	}

	public ScheduleRunnerIF getScheduleRunnerById(int id) {
		return scheduleRunners.get(id);
	}	

	public List<ScheduleRunnerIF> getScheduleRunners() {
		return new ArrayList<ScheduleRunnerIF>(scheduleRunners.values());
	}	

	public void removeScheduleRunnerById(int id) {
		scheduleRunners.remove(id);
	}	

	public void addScheduleRunner(ScheduleRunnerIF scheduleRunner) throws Exception {
		if(scheduleRunners.containsKey(scheduleRunner.getId()))
			throw new Exception("ERROR: ScheduleRunner with id " + scheduleRunner.getId() + " already connected!");
		scheduleRunners.put(scheduleRunner.getId(), scheduleRunner);
	}	

	public List<Integer> checkDeadScheduleRunners() {
		ArrayList<Integer> deadRunners = new ArrayList<>();
		for(Map.Entry<Integer,ScheduleRunnerIF> entry : scheduleRunners.entrySet()) {
			Integer id = entry.getKey();
			ScheduleRunnerIF scheduleRunner = entry.getValue();
			try {
				scheduleRunner.isAlive();	// throws RemoteException if connection lost
			} catch (RemoteException e) {
				System.out.println("Lost connection to schedulRunner " + id);
				deadRunners.add(id);
			}
		}
		return deadRunners;
	}

	@Override
	public Iterator<ScheduleRunnerIF> iterator() {
		return new StorageIterator();
	}

	public class StorageIterator implements Iterator<ScheduleRunnerIF> {
		int current = -1;
		List<ScheduleRunnerIF> scheduleRunnersList;

		public StorageIterator() {
			scheduleRunnersList = new ArrayList<>(scheduleRunners.values());
		}

		public boolean hasNext() {
			return scheduleRunnersList.size()-1 > current; 	    
		}

		public ScheduleRunnerIF next() {
			current++;
			return scheduleRunnersList.get(current);
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
		}
	}
}
