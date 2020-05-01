package com.diptam.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Properties;

@ToString
@Slf4j
public class ApplicationProperties {

    private static final String propertyFile = "application.properties";
    @Getter @Setter
    private String accessToken;
    @Getter @Setter
    private String accessTokenSecret;
    @Getter @Setter
    private String apiKey;
    @Getter @Setter
    private String apiSecretKey;

    public static ApplicationProperties loadProperties(){
        Properties properties = new Properties();
        ApplicationProperties applicationProperties = new ApplicationProperties();
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        try(InputStream input = new FileInputStream(rootPath+"/"+propertyFile)) {
            properties.load(input);
            applicationProperties.setAccessToken(properties.getProperty("access_token"));
            applicationProperties.setAccessTokenSecret(properties.getProperty("access_token_secret"));
            applicationProperties.setApiKey(properties.getProperty("api_key"));
            applicationProperties.setApiSecretKey(properties.getProperty("api_secret_key"));
        } catch (FileNotFoundException e) {
            log.debug("Error Occurred", e);
        } catch (IOException e) {
            log.debug("Error Occurred", e);
        }
       return applicationProperties;
    }
}
