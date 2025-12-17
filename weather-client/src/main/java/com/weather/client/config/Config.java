package com.weather.client.config;

public class Config {
    public static final String API_BASE_URL = "http://localhost:8085";
    
    public static final String WEATHER_ENDPOINT = API_BASE_URL + "/api/weather";
    
    public static final String FORECAST_ENDPOINT = API_BASE_URL + "/api/weather/forecast";
    public static final String RECOMMENDATION_ENDPOINT = API_BASE_URL + "/api/weather/recommendation";
}