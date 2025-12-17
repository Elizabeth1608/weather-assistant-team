package com.weather.client.model;

public class UserGoal {
    private String activity;
    private double maxWind;
    private double minTemperature;
    private double maxTemperature;
    private boolean allowRain;
    
    public UserGoal() {
        this.activity = "fishing";
        this.maxWind = 10.0;
        this.minTemperature = 5.0;
        this.maxTemperature = 30.0;
        this.allowRain = false;
    }
    
    public String getActivity() { return activity; }
    public void setActivity(String activity) { this.activity = activity; }
    
    public double getMaxWind() { return maxWind; }
    public void setMaxWind(double maxWind) { this.maxWind = maxWind; }
    
    public double getMinTemperature() { return minTemperature; }
    public void setMinTemperature(double minTemperature) { this.minTemperature = minTemperature; }
    
    public double getMaxTemperature() { return maxTemperature; }
    public void setMaxTemperature(double maxTemperature) { this.maxTemperature = maxTemperature; }
    
    public boolean isAllowRain() { return allowRain; }
    public void setAllowRain(boolean allowRain) { this.allowRain = allowRain; }
}