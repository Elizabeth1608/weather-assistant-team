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
    private LocalDateTime createdAt;
    
    @Column(name = "humidity")
    private Integer humidity;
    
    @Column(name = "wind_speed")
    private Double windSpeed;
    
    @Column(name = "pressure")
    private Integer pressure;
    
    @Column(name = "feels_like")
    private Double feelsLike;
    
    @Column(name = "visibility")
    private Integer visibility;
    
    @Column(name = "clouds")
    private Integer clouds;
    
    @Column(name = "wind_deg")
    private Integer windDeg;
    
    @Column(name = "wind_direction")
    private String windDirection;
    
    @Column(name = "precipitation")
    private Double precipitation;
    
    @Column(name = "sunrise")
    private Long sunrise;
    
    @Column(name = "sunset")
    private Long sunset;
    
    @Column(name = "weather_icon")
    private String weatherIcon;
    
    @Column(name = "weather_main")
    private String weatherMain;
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
   
    public Long getId() { return id; }
    public String getCity() { return city; }
    public Double getTemperature() { return temperature; }
    public String getDescription() { return description; }
    public String getRecommendation() { return recommendation; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Integer getHumidity() { return humidity; }
    public Double getWindSpeed() { return windSpeed; }
    public Integer getPressure() { return pressure; }
    public Double getFeelsLike() { return feelsLike; }
    public Integer getVisibility() { return visibility; }
    public Integer getClouds() { return clouds; }
    public Integer getWindDeg() { return windDeg; }
    public String getWindDirection() { return windDirection; }
    public Double getPrecipitation() { return precipitation; }
    public Long getSunrise() { return sunrise; }
    public Long getSunset() { return sunset; }
    public String getWeatherIcon() { return weatherIcon; }
    public String getWeatherMain() { return weatherMain; }
    
    public void setId(Long id) { this.id = id; }
    public void setCity(String city) { this.city = city; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public void setDescription(String description) { this.description = description; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setHumidity(Integer humidity) { this.humidity = humidity; }
    public void setWindSpeed(Double windSpeed) { this.windSpeed = windSpeed; }
    public void setPressure(Integer pressure) { this.pressure = pressure; }
    public void setFeelsLike(Double feelsLike) { this.feelsLike = feelsLike; }
    public void setVisibility(Integer visibility) { this.visibility = visibility; }
    public void setClouds(Integer clouds) { this.clouds = clouds; }
    public void setWindDeg(Integer windDeg) { this.windDeg = windDeg; }
    public void setWindDirection(String windDirection) { this.windDirection = windDirection; }
    public void setPrecipitation(Double precipitation) { this.precipitation = precipitation; }
    public void setSunrise(Long sunrise) { this.sunrise = sunrise; }
    public void setSunset(Long sunset) { this.sunset = sunset; }
    public void setWeatherIcon(String weatherIcon) { this.weatherIcon = weatherIcon; }
    public void setWeatherMain(String weatherMain) { this.weatherMain = weatherMain; }
   
}