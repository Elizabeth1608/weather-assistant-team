package com.weather.client.ai;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherLogger {
    private static WeatherLogger instance;
    private static final String LOG_FILE = "weather_system.log";
    
    private WeatherLogger() {}
    
    public static synchronized WeatherLogger getInstance() {
        if (instance == null) {
            instance = new WeatherLogger();
        }
        return instance;
    }
    
    public void logInfo(String module, String message) {
        writeLog("INFO", module, message);
    }
    
    public void logWarning(String module, String message) {
        writeLog("WARN", module, message);
    }
    
    public void logError(String module, String message) {
        writeLog("ERROR", module, message);
    }
    
    private synchronized void writeLog(String level, String module, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logEntry = String.format("[%s] [%s] [%s] %s", 
            timestamp, level, module, message);
        
        System.out.println(logEntry);
        
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(logEntry + "\n");
        } catch (Exception e) {
            System.err.println("Ошибка записи лога: " + e.getMessage());
        }
    }
}