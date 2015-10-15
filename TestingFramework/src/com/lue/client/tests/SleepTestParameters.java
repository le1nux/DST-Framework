package com.lue.client.tests;


public class SleepTestParameters extends TestParameters {
    private static final long serialVersionUID = 1L;

    protected int duration;
    
    public SleepTestParameters() {

    }
    
    public SleepTestParameters(int duration) {
	this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}

