package com.weather.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.backend.entity.WeatherLog;
import com.weather.backend.repository.WeatherLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeatherService {
    private final WeatherLogRepository weatherLogRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${weather.api.key}")
    private String apiKey;
    
    @Value("${weather.api.url}")
    private String apiUrl;
    
    public WeatherLog getWeather(String city) {
        try {
            // 1. Формируем URL запроса
            String url = String.format("%s?q=%s&appid=%s&units=metric&lang=ru", 
                                     apiUrl, city, apiKey);
            
            // 2. Отправляем запрос к OpenWeatherMap
            String jsonResponse = restTemplate.getForObject(url, String.class);
            
            // 3. Парсим JSON ответ
            JsonNode root = objectMapper.readTree(jsonResponse);
            
            // 4. Извлекаем данные
            double temperature = root.path("main").path("temp").asDouble();
            String description = root.path("weather").get(0).path("description").asText();
            
            // 5. Генерируем рекомендацию
            String recommendation = generateRecommendation(temperature, description);
            
            // 6. Создаём объект и сохраняем в БД
            WeatherLog log = new WeatherLog();
            log.setCity(city);
            log.setTemperature(temperature);
            log.setDescription(description);
            log.setRecommendation(recommendation); 
            
            return weatherLogRepository.save(log);
            
        } catch (Exception e) {
            // Если ошибка API - возвращаем заглушку
            return createFallbackLog(city, e.getMessage());
        }
    }
    
    private String generateRecommendation(double temp, String description) {
 
    if (temp < -10) return "Сильный мороз! Оденьтесь очень тепло!";
    if (temp < 0) return "Мороз. Теплая куртка, шапка, перчатки.";
    if (temp < 10) return "Прохладно. Куртка и шарф пригодятся.";
    if (temp < 20) return "Комфортно. Можно в легкой одежде.";
    if (temp > 30) return "Жарко! Пейте воду, носите головной убор.";
    
    // Проверка описания погоды (независимо от температуры)
    String descLower = description.toLowerCase();
    if (descLower.contains("дождь") || descLower.contains("rain")) 
        return "Возьмите зонт!";
    if (descLower.contains("снег") || descLower.contains("snow")) 
        return "Осторожно, скользко!";
    if (descLower.contains("гроза") || descLower.contains("thunderstorm")) 
        return "Гроза! Оставайтесь в помещении.";
    if (descLower.contains("туман") || descLower.contains("fog")) 
        return "Туман. Будьте осторожны на дороге.";
    
    return "Хорошая погода!";
}
    
    private WeatherLog createFallbackLog(String city, String error) {
        WeatherLog log = new WeatherLog();
        log.setCity(city);
        log.setTemperature(null);
        log.setDescription("Ошибка API: " + error);
        log.setRecommendation("Попробуйте позже");
        return weatherLogRepository.save(log);
    }
}
