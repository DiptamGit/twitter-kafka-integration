package com.diptam.util;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

@ToString
@Slf4j
public class ApplicationProperties {

    private static HashMap<String,String> applicationPropertiesCache = new HashMap<>();

    private static final String propertyFile = "application.properties";


    public static HashMap<String, String> getProperties(){
        if (applicationPropertiesCache.isEmpty()){
            loadProperties();
        }
        return applicationPropertiesCache;
    }

    private static void loadProperties(){
        Properties properties = new Properties();
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        try(InputStream input = new FileInputStream(rootPath+"/"+propertyFile)) {
            properties.load(input);
            properties.keySet().forEach(key -> {
                applicationPropertiesCache.put(key.toString(), properties.getProperty(key.toString()));
            });
        } catch (FileNotFoundException e) {
            log.debug("Error Occurred", e);
        } catch (IOException e) {
            log.debug("Error Occurred", e);
        }
    }
}
