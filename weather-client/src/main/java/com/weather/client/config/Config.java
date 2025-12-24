package com.weather.client.config;

public class Config {
    public static final String API_BASE_URL = "http://localhost:8085";
    
    // Используйте эти эндпоинты (они правильные)
    public static final String WEATHER_ENDPOINT = "http://localhost:8085/api/weather/current";
    public static final String FORECAST_ENDPOINT = "http://localhost:8085/api/weather/forecast";
    
    // ДОБАВЬТЕ ЭТОТ ЭНДПОИНТ ДЛЯ АВТОДОПОЛНЕНИЯ:
    public static final String SUGGESTIONS_ENDPOINT = "http://localhost:8085/api/weather/suggestions";
    
    public static final String RECOMMENDATION_ENDPOINT = API_BASE_URL + "/api/weather/recommendation";
}