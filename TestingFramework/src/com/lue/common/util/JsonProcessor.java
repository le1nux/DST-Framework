package com.lue.common.util;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;


public class JsonProcessor {
    protected static ObjectMapper mapper;
    static {
	mapper = new ObjectMapper();
	mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
	mapper.enableDefaultTyping();
	mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
    }


    private JsonProcessor() {   

    }


    public static String toJson(Object o) throws JsonGenerationException, JsonMappingException, IOException {
	return mapper.writeValueAsString(o);
    }

    public static Object fromJson(String o, Class<?> type) throws JsonParseException, JsonMappingException, IOException {
	return mapper.readValue(o, type);
    }
    
    public static void objectToJsonFile(String path, Object o) throws JsonGenerationException, JsonMappingException, IOException {
	mapper.writeValue(new File(path), o);
    }
    
    public static Object jsonFileToObject(String path, Class<?> type) throws JsonParseException, JsonMappingException, IOException {
	return mapper.readValue(new File(path), type);
    }
}
