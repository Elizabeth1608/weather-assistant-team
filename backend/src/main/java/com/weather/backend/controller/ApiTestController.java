package com.weather.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController

public class ApiTestController {
    
    @Value("${weather.api.key}")
    private String apiKey;
    
    @GetMapping("/test-api")
    public String testApi() {
        try {
            String url = "https://api.openweathermap.org/data/2.5/weather?q=Moscow&appid=" + apiKey + "&units=metric&lang=ru";
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            
            if (response == null) {
                return "API вернул пустой ответ";
            }
            
            int length = Math.min(response.length(), 100);
            return "✅ API работает! Ответ: " + response.substring(0, length) + "...";
            
        } catch (Exception e) {
            return "Ошибка при запросе к API: " + e.getClass().getSimpleName() + " - " + e.getMessage();
        }
    }
}
