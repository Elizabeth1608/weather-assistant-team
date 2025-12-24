package com.weather.client.model;

import java.time.LocalDate;

public class ForecastData {
    private LocalDate date;
    private double tempMin;
    private double tempMax;
    private double tempAvg;
    private String description;
    private String icon;
    
    // ДОБАВЬТЕ ГЕТТЕР ДЛЯ ДНЯ НЕДЕЛИ (удобно для отображения)
    public String getDayOfWeek() {
        if (date == null) return "";
        String[] days = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};
        return days[date.getDayOfWeek().getValue() - 1];
    }
    
    public String getFormattedDate() {
        if (date == null) return "";
        return date.toString();
    }
    
    // Геттеры и сеттеры (оставьте как есть)
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public double getTempMin() { return tempMin; }
    public void setTempMin(double tempMin) { this.tempMin = tempMin; }
    
    public double getTempMax() { return tempMax; }
    public void setTempMax(double tempMax) { this.tempMax = tempMax; }
    
    public double getTempAvg() { return tempAvg; }
    public void setTempAvg(double tempAvg) { this.tempAvg = tempAvg; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}