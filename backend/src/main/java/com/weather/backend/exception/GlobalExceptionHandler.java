package com.weather.backend.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice

public class GlobalExceptionHandler {
     @ExceptionHandler(CityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCityNotFound(CityNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Город не найден");
        body.put("message", ex.getMessage());
        body.put("city", ex.getCity());
        body.put("suggestion", "Попробуйте проверить название города или использовать автодополнение");
        
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(WeatherApiException.class)
    public ResponseEntity<Map<String, Object>> handleWeatherApiError(WeatherApiException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        body.put("error", "Ошибка API погоды");
        body.put("message", ex.getMessage());
        body.put("city", ex.getCity());
        body.put("apiEndpoint", ex.getApiEndpoint());
        body.put("suggestion", "Попробуйте позже или проверьте API ключ");
        
        return new ResponseEntity<>(body, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Map<String, Object>> handleHttpClientError(HttpClientErrorException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", ex.getStatusCode().value());
        body.put("error", "Ошибка внешнего API");
        body.put("message", "OpenWeatherMap вернул ошибку: " + ex.getStatusText());
        
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            body.put("suggestion", "Город не найден в базе OpenWeatherMap");
        } else if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            body.put("suggestion", "Проверьте API ключ в application.properties");
        } else {
            body.put("suggestion", "Попробуйте позже");
        }
        
        return new ResponseEntity<>(body, ex.getStatusCode());
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Внутренняя ошибка сервера");
        body.put("message", "Произошла непредвиденная ошибка: " + ex.getMessage());
        body.put("suggestion", "Обратитесь к администратору или попробуйте позже");
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
