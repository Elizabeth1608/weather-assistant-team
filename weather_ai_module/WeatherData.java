public class WeatherData {
    private double temperature;
    private double pressure;
    private double windSpeed;
    private int precipitation;
    
    public WeatherData(double temperature, double pressure, double windSpeed, int precipitation) {
        this.temperature = temperature;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.precipitation = precipitation;
    }
    
    public double getTemperature() { return temperature; }
    public double getPressure() { return pressure; }
    public double getWindSpeed() { return windSpeed; }
    public int getPrecipitation() { return precipitation; }
    
    public String toString() {
        return String.format("Темп: %.1f°C, Давл: %.1f, Ветер: %.1f м/с, Осадки: %d", 
            temperature, pressure, windSpeed, precipitation);
    }
}