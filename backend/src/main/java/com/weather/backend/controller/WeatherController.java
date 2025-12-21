package com.weather.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.weather.backend.entity.WeatherLog;
import com.weather.backend.service.WeatherService;

import lombok.RequiredArgsConstructor; 

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor


public class WeatherController {
    
     @Autowired
    private WeatherService weatherService;

    @GetMapping("/current")
    public WeatherLog getCurrentWeather(@RequestParam String city) {
        return weatherService.getWeather(city);
    }

    @GetMapping("/forecast")
    public List<WeatherLog> getForecast(@RequestParam String city) {
        return weatherService.get5DayForecast(city);
    }

    @GetMapping("/suggestions")
    public List<com.weather.backend.dto.CitySuggestion> getSuggestions(@RequestParam String query) {
        return weatherService.getCitySuggestions(query);
    }
}