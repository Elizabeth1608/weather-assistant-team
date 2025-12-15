package com.weather.backend.controller;

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
     private final WeatherService weatherService;
    
    @GetMapping
    public WeatherLog getWeather(@RequestParam String city) {
        return weatherService.getWeather(city);
    }
}
