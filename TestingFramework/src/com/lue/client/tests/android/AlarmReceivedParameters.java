package com.lue.client.tests.android;

public class AlarmReceivedParameters extends AndroidTestParameters{
    private static final long serialVersionUID = 1L;
    
    protected int userId;	// the id the app registered at the alarm server with

    public AlarmReceivedParameters() {
	super();
    }
    
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
