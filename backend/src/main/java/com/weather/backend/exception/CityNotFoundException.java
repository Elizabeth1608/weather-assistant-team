package com.weather.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CityNotFoundException extends RuntimeException {
    private final String city;
    
    public CityNotFoundException(String city) {
        super(String.format("Город '%s' не найден. Проверьте правильность написания.", city));
        this.city = city;
    }
    
    public String getCity() { return city; }
}
