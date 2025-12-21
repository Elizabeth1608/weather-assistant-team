package com.weather.backend.exception;

public class WeatherApiException extends RuntimeException {
    private final String city;
    private final String apiEndpoint;
    
    public WeatherApiException(String message, String city, String apiEndpoint) {
        super(String.format("%s (Город: %s, API: %s)", message, city, apiEndpoint));
        this.city = city;
        this.apiEndpoint = apiEndpoint;
    }
    
    public WeatherApiException(String message, String city, String apiEndpoint, Throwable cause) {
        super(String.format("%s (Город: %s, API: %s)", message, city, apiEndpoint), cause);
        this.city = city;
        this.apiEndpoint = apiEndpoint;
    }
    
    public String getCity() { return city; }
    public String getApiEndpoint() { return apiEndpoint; }
}