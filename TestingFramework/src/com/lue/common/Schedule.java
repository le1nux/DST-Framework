package com.lue.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.lue.client.tests.TestParameters;

@XmlRootElement(namespace = "com.lue.server;")
public class Schedule implements Iterable<Schedule.ScheduleElement>, Serializable{
	private static final long serialVersionUID = 1L;

	protected int scheduleRunnerId;

	@XmlElement
	protected List<ScheduleElement> schedule;

	public int getScheduleRunnerId() {
		return scheduleRunnerId;
	}

	public void setScheduleRunnerId(int scheduleRunnerId) {
		this.scheduleRunnerId = scheduleRunnerId;
	}

	public Schedule() {
		schedule = new ArrayList<ScheduleElement>();
	}

	public void addScheduleElement (ScheduleElement test) {
		schedule.add(test);
	}

	public List<ScheduleElement> getSchedule() {
		return schedule;
	}

	public ScheduleElement getScheduleElement(int testId) {
		if(schedule.size() > testId) 
			return schedule.get(testId);
		return null;
	}

	@JsonIgnore
	public int getElementCount() {
		return schedule.size();
	}

	@Override
	public Iterator<ScheduleElement> iterator() {
		Iterator<ScheduleElement> it = new Iterator<ScheduleElement>() {

			private int currentIndex = 0;

			@Override
			public boolean hasNext() {
				return currentIndex < schedule.size();
			}

			@Override
			public ScheduleElement next() {
				return schedule.get(currentIndex++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		return it;
	}

	public static class ScheduleElement implements Serializable{
		private static final long serialVersionUID = 1L;

		protected String testKey;
		protected TestParameters parameters;
		protected TestResult testResult;

		public ScheduleElement() {

		}

		public ScheduleElement(String testKey, TestParameters parameters, TestResult testResult) {
			this.testKey = testKey;
			this.parameters = parameters;
			this.testResult = testResult;
		}

		public String getTestKey() {
			return testKey;
		}

		public void setTestKey(String testKey) {
			this.testKey = testKey;
		}

		public TestParameters getParameters() {
			return parameters;
		}

		public void setParameters(TestParameters parameters) {
			this.parameters = parameters;
		}

		public TestResult getTestResult() {
			return testResult;
		}

		public boolean hasTestResult() {
			return testResult != null;
		}

		public void setTestResult(TestResult testResult) {
			this.testResult = testResult;
		}
	}
}
