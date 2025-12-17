package com.weather.client.service;

import com.weather.client.config.Config;
import com.weather.client.model.WeatherData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ApiService {
    private static final Gson gson = new Gson();
    
    public WeatherData getCurrentWeather(String city) {
        System.out.println("\n=== ЗАПРОС ПОГОДЫ ===");
        System.out.println("Город: " + city);
        
        try {
            String encodedCity = URLEncoder.encode(city, "UTF-8");
            String urlString = Config.WEATHER_ENDPOINT + "?city=" + encodedCity;
            
            System.out.println("Запрос к: " + urlString);
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            System.out.println("Код ответа: " + responseCode);
            
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                String jsonResponse = response.toString();
                System.out.println("Получен ответ от сервера:");
                System.out.println(jsonResponse);
                
                return parseWeatherResponse(jsonResponse, city);
            } else {
                System.out.println("ОШИБКА: Код " + responseCode);
                BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorResponse.append(line);
                }
                errorReader.close();
                System.out.println("Ошибка: " + errorResponse.toString());
            }
        } catch (Exception e) {
            System.out.println("Исключение: " + e.getMessage());
            e.printStackTrace();
        }
        
        return createTestData(city);
    }
    
    private WeatherData parseWeatherResponse(String jsonResponse, String city) {
        try {
            JsonObject json = gson.fromJson(jsonResponse, JsonObject.class);
            WeatherData weather = new WeatherData();
            
            weather.setCity(city);
            
            if (json.has("temperature")) {
                weather.setTemperature(json.get("temperature").getAsDouble());
            }
            
            if (json.has("description")) {
                weather.setDescription(json.get("description").getAsString());
            }
            
            if (json.has("humidity") && !json.get("humidity").isJsonNull()) {
                weather.setHumidity(json.get("humidity").getAsDouble());
            }
            
            if (json.has("pressure") && !json.get("pressure").isJsonNull()) {
                weather.setPressure(json.get("pressure").getAsDouble());
            }
            
            if (json.has("windSpeed") && !json.get("windSpeed").isJsonNull()) {
                weather.setWindSpeed(json.get("windSpeed").getAsDouble());
            }
            
            if (weather.getTemperature() != 0) {
                weather.setFeelsLike(weather.getTemperature() - 2);
            }
            
            String desc = weather.getDescription();
            if (desc != null) {
                if (desc.toLowerCase().contains("ясн") || desc.toLowerCase().contains("clear")) {
                    weather.setIcon("01d");
                } else if (desc.toLowerCase().contains("облач") || desc.toLowerCase().contains("cloud")) {
                    weather.setIcon("03d");
                } else if (desc.toLowerCase().contains("дожд") || desc.toLowerCase().contains("rain")) {
                    weather.setIcon("10d");
                } else {
                    weather.setIcon("02d");
                }
            }
            
            System.out.println("Парсинг успешен:");
            System.out.println("- Город: " + weather.getCity());
            System.out.println("- Температура: " + weather.getTemperature());
            System.out.println("- Описание: " + weather.getDescription());
            System.out.println("- Влажность: " + weather.getHumidity());
            System.out.println("- Давление: " + weather.getPressure());
            System.out.println("- Ветер: " + weather.getWindSpeed());
            
            return weather;
            
        } catch (Exception e) {
            System.out.println("Ошибка парсинга JSON: " + e.getMessage());
            return createTestData(city);
        }
    }
    
    private WeatherData createTestData(String city) {
        WeatherData test = new WeatherData();
        test.setCity(city);
        test.setTemperature(15.0);
        test.setFeelsLike(14.0);
        test.setHumidity(65.0);
        test.setPressure(1013.0);
        test.setWindSpeed(3.5);
        test.setDescription("Тестовые данные");
        test.setIcon("01d");
        return test;
    }
}