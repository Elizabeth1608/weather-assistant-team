package com.weather.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "weather_log") // Имя таблицы в БД
@Data // Автоматически создаст геттеры, сеттеры, toString

public class WeatherLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String city;
    private Double temperature;
    private String description;
    private String recommendation; 
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

}
