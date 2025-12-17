package com.weather.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.backend.entity.WeatherLog;
import com.weather.backend.repository.WeatherLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {
    private final WeatherLogRepository weatherLogRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${weather.api.key}")
    private String apiKey;
    
    @Value("${weather.api.url}")
    private String apiUrl;
    
    public WeatherLog getWeather(String city) {
        log.info("Запрос погоды для города: {}", city);
        
        try {
            String url = String.format("%s?q=%s&appid=%s&units=metric&lang=ru", 
                                     apiUrl, city, apiKey);
            
            log.debug("Отправка запроса к OpenWeatherMap: {}", url);
            
            String jsonResponse = restTemplate.getForObject(url, String.class);
            
            JsonNode root = objectMapper.readTree(jsonResponse);
            
            double temperature = root.path("main").path("temp").asDouble();
            double feelsLike = root.path("main").path("feels_like").asDouble();
            int humidity = root.path("main").path("humidity").asInt();
            int pressure = root.path("main").path("pressure").asInt();
            
            double windSpeed = root.path("wind").path("speed").asDouble();
            
            String description = root.path("weather").get(0).path("description").asText();
            String icon = root.path("weather").get(0).path("icon").asText();
            
            String recommendation = generateRecommendation(temperature, description, windSpeed);
            
            WeatherLog weatherLog = new WeatherLog();
            weatherLog.setCity(city);
            weatherLog.setTemperature(temperature);
            
            if (description.length() > 200) {
                description = description.substring(0, 200);
            }
            weatherLog.setDescription(description);
            weatherLog.setRecommendation(recommendation);
            
            weatherLog.setHumidity(humidity);
            weatherLog.setPressure(pressure);
            weatherLog.setWindSpeed(windSpeed);
            
            WeatherLog savedWeatherLog = weatherLogRepository.save(weatherLog);
            log.info("Погода сохранена в БД для города: {}", city);
            
            return savedWeatherLog;
            
        } catch (Exception e) {
            log.error("Ошибка при получении погоды для {}: {}", city, e.getMessage());
            
            return createFallbackLog(city, e.getMessage());
        }
    }
    
    private String generateRecommendation(double temp, String description, double windSpeed) {
        StringBuilder recommendation = new StringBuilder();
        
        if (temp < -10) {
            recommendation.append("❄️ Сильный мороз! Теплая одежда обязательна. ");
        } else if (temp < 0) {
            recommendation.append("🥶 Мороз. Теплая куртка, шапка, перчатки. ");
        } else if (temp < 10) {
            recommendation.append("🧥 Прохладно. Куртка и шарф пригодятся. ");
        } else if (temp < 20) {
            recommendation.append("👍 Комфортно. Легкая куртка. ");
        } else if (temp < 30) {
            recommendation.append("☀️ Тепло. Легкая одежда. ");
        } else {
            recommendation.append("🔥 Жарко! Пейте воду, головной убор. ");
        }
        
        if (windSpeed > 15) {
            recommendation.append("💨 Очень сильный ветер! Будьте осторожны. ");
        } else if (windSpeed > 10) {
            recommendation.append("🌬️ Сильный ветер. ");
        } else if (windSpeed > 5) {
            recommendation.append("🍃 Умеренный ветер. ");
        }
        
        String descLower = description.toLowerCase();
        if (descLower.contains("дождь") || descLower.contains("rain")) {
            recommendation.append("☔ Возьмите зонт! ");
        }
        if (descLower.contains("снег") || descLower.contains("snow")) {
            recommendation.append("⛄ Осторожно, скользко! ");
        }
        if (descLower.contains("гроза") || descLower.contains("thunderstorm")) {
            recommendation.append("⛈️ Гроза! Оставайтесь в помещении. ");
        }
        if (descLower.contains("туман") || descLower.contains("fog")) {
            recommendation.append("🌫️ Туман. Будьте осторожны. ");
        }
        
        String result = recommendation.toString();
        if (result.length() > 500) {
            result = result.substring(0, 497) + "...";
        }
        
        return result;
    }
    
    private WeatherLog createFallbackLog(String city, String error) {
        WeatherLog fallbackLog = new WeatherLog();
        fallbackLog.setCity(city);
        fallbackLog.setTemperature(15.0);
        
        String shortError = error;
        if (error != null && error.length() > 50) {
            shortError = error.substring(0, 47) + "...";
        }
        
        fallbackLog.setDescription("Данные временно недоступны. " + shortError);
        fallbackLog.setRecommendation("Попробуйте позже или проверьте название города.");
        
        fallbackLog.setHumidity(65);
        fallbackLog.setPressure(1013);
        fallbackLog.setWindSpeed(3.5);
        
        return fallbackLog;
    }
}