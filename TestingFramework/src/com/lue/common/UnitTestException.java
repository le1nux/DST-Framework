package com.lue.common;

public class UnitTestException extends Exception {
    private static final long serialVersionUID = 1997753363232807009L;

    public UnitTestException() {
    }

    public UnitTestException(String message) {
	super(message);
    }

    public UnitTestException(Throwable cause) {
	super(cause);
    }

    public UnitTestException(String message, Throwable cause) {
	super(message, cause);
    }

    public UnitTestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
	super(message, cause, enableSuppression, writableStackTrace);
    }

}
