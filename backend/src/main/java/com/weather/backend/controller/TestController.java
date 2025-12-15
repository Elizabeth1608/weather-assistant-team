package com.weather.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class TestController {
      @GetMapping("/test")
    public String test() {
        return "Бэкенд работает! Порт 8085.";
    }
}
