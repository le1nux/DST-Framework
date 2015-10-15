package com.lue.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProcessOutputReader extends Thread {
    protected final InputStream is;
    protected final StringBuilder sb;

    public ProcessOutputReader(final InputStream is) {
	this.is = is;
	this.sb = new StringBuilder();
    }

    public void run() {
	try {
	    final InputStreamReader isr = new InputStreamReader(is);
	    final BufferedReader br = new BufferedReader(isr);
	    String line = null;
	    while ((line = br.readLine()) != null) {
		this.sb.append(line).append("\n");
	    }
	}
	catch (final IOException ioe) {
	    System.err.println(ioe.getMessage());
	    throw new RuntimeException(ioe);
	}
    }
    
    public void interruptThread() throws IOException {
	is.close();
    }

    @Override
    public String toString() {
	return this.sb.toString();
    }
}

