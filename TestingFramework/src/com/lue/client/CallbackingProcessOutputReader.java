package com.lue.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CallbackingProcessOutputReader extends ProcessOutputReader{

    protected Map<String, List<ProcessOutputReaderCallbackIF>> regexToCallbackObjects;
    protected List<Pattern> patterns;
    
    public CallbackingProcessOutputReader(InputStream is) {
	super(is);
	regexToCallbackObjects = new HashMap<>();
	patterns = new ArrayList<>();
    }

    @Override
    public void run() {
	try {
	    final InputStreamReader isr = new InputStreamReader(is);
	    final BufferedReader br = new BufferedReader(isr);
	    String line = null;
	    while ((line = br.readLine()) != null) {
		this.sb.append(line).append("\n");
//		System.out.println(line);
		for(Pattern pattern : patterns) {
		    Matcher matcher = pattern.matcher(line);
		    if(matcher.find()) {
			String resultJson = line.substring(matcher.end()).trim();
			informCallbackObjects(pattern.toString(), resultJson);
		    }
		}
	    }
	}
	catch (final IOException ioe) {
	    String message = ioe.getMessage();
	    if (message.equals("Stream closed"))
		System.out.println(ioe.getMessage());
	    else
		throw new RuntimeException(ioe);
	}
    }    


    public void addCallbackObject(String regex, ProcessOutputReaderCallbackIF callbackIF) {
	if(!regexToCallbackObjects.containsKey(regex)) {
	    regexToCallbackObjects.put(regex, new ArrayList<ProcessOutputReaderCallbackIF>());
	}
	regexToCallbackObjects.get(regex).add(callbackIF);
	Pattern pattern = Pattern.compile(regex);
	patterns.add(pattern);
    }

    public void informCallbackObjects(String regex, String line) {
	if(regexToCallbackObjects.containsKey(regex)) {
	    List<ProcessOutputReaderCallbackIF> callbackObjects = regexToCallbackObjects.get(regex);
	    for(ProcessOutputReaderCallbackIF callbackIF : callbackObjects) {
		callbackIF.callback(regex, line);
	    }
	}
    }
}
