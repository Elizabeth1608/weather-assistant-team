package com.weather.client.ai;

public class SimpleWeatherData {
    private double temperature;
    private double pressure;
    private double windSpeed;
    private int precipitation;
    private double humidity;
    
    public SimpleWeatherData(double temperature, double pressure, 
                           double windSpeed, int precipitation) {
        this.temperature = temperature;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.precipitation = precipitation;
    }
    
    public double getTemperature() { return temperature; }
    public double getPressure() { return pressure; }
    public double getWindSpeed() { return windSpeed; }
    public int getPrecipitation() { return precipitation; }
    
    public void setHumidity(double humidity) { this.humidity = humidity; }
    public double getHumidity() { return humidity; }
}