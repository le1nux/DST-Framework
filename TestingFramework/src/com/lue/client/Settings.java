package com.lue.client;

import java.io.File;
//import java.io.IOException;
//import java.lang.reflect.Constructor;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Enumeration;
//import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
//import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//import com.lue.client.tests.TestConstructor;
import com.lue.common.SupportedTests;
//import com.lue.common.SupportedTests.SupportedTest;

@XmlRootElement
public class Settings {
	protected SupportedTests supportedTests;

	//    // makes it a singleton
	//    public Settings() {
	//	supportedTests = analyzeSupportedTests();
	//    }

	//    public void toXML() {
	//	JAXBContext jaxbContext;
	//	try {
	//	    jaxbContext = JAXBContext.newInstance(Settings.class);
	//	    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	//	    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // in order to get nicely formatted output
	//	    File XMLfile = new File("./Settings.xml");
	//	    // Writing to XML file
	//	    jaxbMarshaller.marshal(this, XMLfile); 
	//	    // Writing to console
	//	    jaxbMarshaller.marshal(this, System.out); 
	//	} catch (JAXBException e) {
	//	    e.printStackTrace();
	//	}
	//    }

	@XmlElement
	public SupportedTests getSupportedTests() {
		return supportedTests;
	}

	public void setSupportedTests(SupportedTests supportedTests) {
		this.supportedTests = supportedTests;
	}

	public static Settings fromXML() {
		Settings settings = null;

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Settings.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			// specify the location and name of xml file to be read
			File XMLfile = new File("Settings.xml");
			if(!XMLfile.exists())
				return null;

			// this will create the settings object 
			settings = (Settings) jaxbUnmarshaller.unmarshal(XMLfile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return settings;
	}    

	//    public static SupportedTests analyzeSupportedTests() {
	//	SupportedTests supportedTests = new SupportedTests();
	//	Class<?>[] classes = null;
	//	try {
	//	    classes = getClasses("com.lue.client.tests");
	//	} catch (ClassNotFoundException | IOException e) {
	//	    e.printStackTrace();
	//	}
	//	if(classes != null) {
	//	    for(Class<?> c : classes){
	//		for(Constructor<?> constructor : c.getConstructors()){
	//		    if(constructor.isAnnotationPresent(TestConstructor.class)) {
	//			SupportedTest supportedTest = new SupportedTest(c.getName());
	//			supportedTests.addSupportedTest(supportedTest);
	//		    }
	//		}
	//	    }
	//	}
	//	return supportedTests;
	//    }

	//    // snippet copied from http://www.dzone.com/snippets/get-all-classes-within-package
	//    private static Class<?>[] getClasses(String packageName) throws ClassNotFoundException, IOException {
	//	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	//	assert classLoader != null;
	//	String path = packageName.replace('.', '/');
	//	Enumeration<URL> resources = classLoader.getResources(path);
	//	List<File> dirs = new ArrayList<File>();
	//	while (resources.hasMoreElements()) {
	//	    URL resource = resources.nextElement();		// TODO resource contains absolute path. test if this also works when framework is jared.
	//	    dirs.add(new File(resource.getFile()));
	//	}
	//	ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
	//	for (File directory : dirs) {
	//	    classes.addAll(findClasses(directory, packageName));
	//	}
	//	return classes.toArray(new Class[classes.size()]);
	//    }

	//    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
	//	List<Class<?>> classes = new ArrayList<Class<?>>();
	//	if (!directory.exists()) {
	//	    return classes;
	//	}
	//	File[] files = directory.listFiles();
	//	for (File file : files) {
	//	    if (file.isDirectory()) {
	//		assert !file.getName().contains(".");
	//		classes.addAll(findClasses(file, packageName + "." + file.getName()));
	//	    } else if (file.getName().endsWith(".class")) {
	//		classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
	//	    }
	//	}
	//	return classes;
	//    }
}
