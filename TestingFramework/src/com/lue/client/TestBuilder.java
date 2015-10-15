package com.lue.client;


import java.lang.reflect.Constructor;

import com.lue.client.tests.Test;
import com.lue.client.tests.TestConstructor;
import com.lue.client.tests.TestParameters;
import com.lue.common.UnitTestException;


public class TestBuilder {

    public static Test<? extends TestParameters> build(String key, int testId, TestParameters testParameters) throws UnitTestException {
	Test<? extends TestParameters> test = null;
	try{
	    Class<?> c  = Class.forName(key);
	    for(Constructor<?> constructor : c.getConstructors()){
		if(constructor.isAnnotationPresent(TestConstructor.class)) {
		    test = (Test<? extends TestParameters>) constructor.newInstance(testId, testParameters);
		    break;
		}
	    }
	} catch(Exception e) {
	    e.printStackTrace();
	    throw new UnitTestException("FATAL: Could not build test " + key, e);
	}
	return test;
    }

}
