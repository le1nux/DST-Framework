package com.lue.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;


public class SupportedTests implements Serializable {
    private static final long serialVersionUID = 1L;
    @XmlElement
    protected Map<String, SupportedTest> supportedTestsMap;
    
    public SupportedTests() {
	supportedTestsMap = new HashMap<>();
    }
    
    public void addSupportedTest(SupportedTest supportedTest) {
	supportedTestsMap.put(supportedTest.name, supportedTest);
    }
    
    public boolean isTestSupported(String key) {
	return supportedTestsMap.containsKey(key);
    }
    
    public SupportedTest getSupportedTest(String key) {
	if(supportedTestsMap.containsKey(key))	
	    return supportedTestsMap.get(key);
	return null;
    }
    
    public Map<String, SupportedTest>  getSupportedTests() {
	return supportedTestsMap;	
    }
    
    public void intersectSupportedTests(SupportedTests st) {
	supportedTestsMap.keySet().retainAll(st.getSupportedTests().keySet());
    }
    
    @Override
    public String toString() {
	String rep = "";
	for(Map.Entry<String, SupportedTest> entry : supportedTestsMap.entrySet()) {
	    SupportedTest test = entry.getValue();
	    rep += test.toString() + "\n";
	}
	return rep;
    }

    public static class SupportedTest implements Serializable{
	private static final long serialVersionUID = 1L;
	
	protected String name;
	
	public SupportedTest() {

	}
	
	public SupportedTest(String name) {
	    this.name = name;
	}
	
	
	@XmlElement
	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	@Override
	public String toString() {
	    String rep = "";
	    rep += name + "\n";
	    return rep;
	}
    }
}
