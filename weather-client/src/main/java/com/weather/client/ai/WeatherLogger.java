package com.weather.client.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherLogger {
    private static WeatherLogger instance;
    
    private WeatherLogger() {}
    
    public static synchronized WeatherLogger getInstance() {
        if (instance == null) {
            instance = new WeatherLogger();
        }
        return instance;
    }
    
    public void logInfo(String module, String message) {
        LoggerFactory.getLogger(module).info(message);
    }
    
    public void logWarning(String module, String message) {
        LoggerFactory.getLogger(module).warn(message);
    }
    
    public void logError(String module, String message) {
        LoggerFactory.getLogger(module).error(message);
    }
}