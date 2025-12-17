package com.weather.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_log")
@Data
public class WeatherLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private Double temperature;
    
    @Column(length = 500) 
    private String description;
    
    @Column(length = 1000) 
    private String recommendation;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "humidity")
    private Integer humidity;
    
    @Column(name = "wind_speed")
    private Double windSpeed;
    
    @Column(name = "pressure")
    private Integer pressure;
    
    public WeatherLog() {
        this.createdAt = LocalDateTime.now();
    }
    
    public WeatherLog(String city, Double temperature, String description, 
                     String recommendation) {
        this.city = city;
        this.temperature = temperature;
        this.description = description;
        this.recommendation = recommendation;
        this.createdAt = LocalDateTime.now();
    }
    
    public WeatherLog(String city, Double temperature, String description, 
                     String recommendation, Integer humidity, 
                     Double windSpeed, Integer pressure) {
        this.city = city;
        this.temperature = temperature;
        this.description = description;
        this.recommendation = recommendation;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.pressure = pressure;
        this.createdAt = LocalDateTime.now();
    }
}