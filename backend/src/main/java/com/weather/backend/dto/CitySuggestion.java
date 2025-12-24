package com.weather.backend.dto;

import lombok.Data;

@Data

public class CitySuggestion {
    private String name;     
    private String country;   
    private String state;     
    private Double lat;    
    private Double lon; 

    public String getName() { return name; }
    public String getCountry() { return country; }
    public String getState() { return state; }
    public Double getLat() { return lat; }
    public Double getLon() { return lon; }
    
    public void setName(String name) { this.name = name; }
    public void setCountry(String country) { this.country = country; }
    public void setState(String state) { this.state = state; }
    public void setLat(Double lat) { this.lat = lat; }
    public void setLon(Double lon) { this.lon = lon; }
}
