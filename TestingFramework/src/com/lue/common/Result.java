package com.lue.common;

import java.io.Serializable;


public abstract class  Result implements Serializable{
    private static final long serialVersionUID = 1L;

    public enum STATUS {SUCCESS, FAILURE, ERROR};
    protected String testKey;
    protected STATUS status;
    protected long start;
    protected long end;
    protected String errorMessage;
    protected String failureMessage;
    protected transient ChangeListenerIF changeListenerIF;

    
    public Result(String testKey) {
        this(testKey, STATUS.SUCCESS);
    }

    public Result(String testKey, STATUS status) {
        this.testKey = testKey;
        this.status = status;
        start = Long.MAX_VALUE;
        end = Long.MIN_VALUE;
    }


    public String getTestKey() {
        return testKey;
    }

    public void setTestKey(String testKey) {
        this.testKey = testKey;
        udpate();
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
        udpate();
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
        udpate();
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
        udpate();
    }

    public void setChangeListenerIF(ChangeListenerIF changeListenerIF) {
        this.changeListenerIF = changeListenerIF;
    }

    protected void udpate() {
        if(changeListenerIF != null) {
            changeListenerIF.update();
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }
    
    
}