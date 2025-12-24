package com.weather.client.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.weather.client.config.Config;
import com.weather.client.model.CitySuggestion;
import com.weather.client.model.ForecastData;
import com.weather.client.model.WeatherData;

public class ApiService {
    private static final Gson gson = new Gson();
    
    public WeatherData getCurrentWeather(String city) {
        System.out.println("\n ЗАПРОС ПОГОДЫ");
        System.out.println("Город: " + city);
        
        WeatherData result = null;
        
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
                System.out.println("СЫРОЙ ОТВЕТ ОТ СЕРВЕРА");
                System.out.println(jsonResponse);
                System.out.println("КОНЕЦ ОТВЕТА");
                
                System.out.println("\n АНАЛИЗ JSON");
                JsonObject json = gson.fromJson(jsonResponse, JsonObject.class);
                System.out.println("Есть поле 'humidity'? " + json.has("humidity"));
                System.out.println("Есть поле 'pressure'? " + json.has("pressure"));
                System.out.println("Есть поле 'windSpeed'? " + json.has("windSpeed"));
                System.out.println("Есть поле 'temperature'? " + json.has("temperature"));
                System.out.println("Есть поле 'description'? " + json.has("description"));
                
                if (json.has("humidity")) {
                    System.out.println("humidity value: " + json.get("humidity"));
                    System.out.println("humidity is null? " + json.get("humidity").isJsonNull());
                }
                
                result = parseWeatherResponse(jsonResponse, city);
            }
        } catch (Exception e) {
            System.out.println("Исключение: " + e.getMessage());
            e.printStackTrace();
        }
        
        if (result == null) {
            System.out.println("Возвращаем тестовые данные");
            result = createTestData(city);
        }
        
        return result;
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
    
    public List<ForecastData> get5DayForecast(String city) {
        System.out.println("\n=== ЗАПРОС ПРОГНОЗА НА 5 ДНЕЙ ===");
        System.out.println("Город: " + city);
        
        List<ForecastData> result = null;
        
        try {
            String encodedCity = URLEncoder.encode(city, "UTF-8");
            String urlString = Config.FORECAST_ENDPOINT + "?city=" + encodedCity;
            
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
                System.out.println("СЫРОЙ ОТВЕТ ПРОГНОЗА");
                System.out.println(jsonResponse.substring(0, Math.min(500, jsonResponse.length())) + "...");
                System.out.println("КОНЕЦ ОТВЕТА");
                
                result = parseForecastResponse(jsonResponse, city);
            }
        } catch (Exception e) {
            System.out.println("Исключение при запросе прогноза: " + e.getMessage());
            e.printStackTrace();
        }
        
        if (result == null) {
            System.out.println("Возвращаем тестовые данные прогноза");
            result = createTestForecastData();
        }
        
        return result; 
    }
    
    private List<ForecastData> parseForecastResponse(String jsonResponse, String city) {
        List<ForecastData> forecastList = new ArrayList<>();
        
        try {
            JsonArray jsonArray = gson.fromJson(jsonResponse, JsonArray.class);
            
            System.out.println("\n=== ПАРСИНГ ПРОГНОЗА ===");
            System.out.println("Получено дней: " + jsonArray.size());
            
            int daysToShow = Math.min(jsonArray.size(), 4);
            
            for (int i = 0; i < daysToShow; i++) {
                JsonObject dayJson = jsonArray.get(i).getAsJsonObject();
                ForecastData forecast = new ForecastData();
                
                forecast.setDate(LocalDate.now().plusDays(i + 1));
                
                if (dayJson.has("temperature")) {
                    double temp = dayJson.get("temperature").getAsDouble();
                    forecast.setTempAvg(temp);
                    forecast.setTempMin(temp - 2);
                    forecast.setTempMax(temp + 2);
                } else if (dayJson.has("temp")) {
                    double temp = dayJson.get("temp").getAsDouble();
                    forecast.setTempAvg(temp);
                    forecast.setTempMin(temp - 2);
                    forecast.setTempMax(temp + 2);
                }
                
                if (dayJson.has("description")) {
                    forecast.setDescription(dayJson.get("description").getAsString());
                }
                
                if (dayJson.has("weatherIcon")) {
                    forecast.setIcon(dayJson.get("weatherIcon").getAsString());
                } else if (dayJson.has("icon")) {
                    forecast.setIcon(dayJson.get("icon").getAsString());
                }
                
                if (forecast.getIcon() == null || forecast.getIcon().isEmpty()) {
                    if (forecast.getDescription() != null) {
                        String desc = forecast.getDescription().toLowerCase();
                        if (desc.contains("ясн") || desc.contains("clear")) {
                            forecast.setIcon("01d");
                        } else if (desc.contains("облач") || desc.contains("cloud")) {
                            forecast.setIcon("03d");
                        } else if (desc.contains("дожд") || desc.contains("rain")) {
                            forecast.setIcon("10d");
                        } else if (desc.contains("снег") || desc.contains("snow")) {
                            forecast.setIcon("13d");
                        } else {
                            forecast.setIcon("02d");
                        }
                    }
                }
                
                System.out.println("День " + (i + 1) + ": " + 
                    forecast.getDayOfWeek() + " - " + 
                    forecast.getTempAvg() + "°C, " + 
                    forecast.getDescription());
                
                forecastList.add(forecast);
            }
            
            return forecastList;
            
        } catch (Exception e) {
            System.out.println("Ошибка парсинга прогноза: " + e.getMessage());
            e.printStackTrace();
            return createTestForecastData();
        }
    }
    
    private List<ForecastData> createTestForecastData() {
        List<ForecastData> testData = new ArrayList<>();
        
        for (int i = 1; i <= 5; i++) {
            ForecastData day = new ForecastData();
            day.setDate(LocalDate.now().plusDays(i));
            
            double baseTemp = 15.0 + (Math.random() * 10 - 5);
            day.setTempAvg(baseTemp);
            day.setTempMin(baseTemp - 2);
            day.setTempMax(baseTemp + 2);
            
            String[] descriptions = {"Ясно", "Облачно", "Небольшой дождь", "Пасмурно", "Снег"};
            String desc = descriptions[i % descriptions.length];
            day.setDescription(desc);
            
            String[] icons = {"01d", "02d", "03d", "04d", "10d"};
            day.setIcon(icons[i % icons.length]);
            
            testData.add(day);
        }
        
        return testData;
    }
    
public List<CitySuggestion> getCitySuggestions(String query) {
    System.out.println("\n=== ПОИСК ГОРОДОВ ===");
    System.out.println("Запрос: " + query);
    
    List<CitySuggestion> suggestions = new ArrayList<>();
    
    try {
        if (query.length() < 2) {
            return suggestions;
        }
        
        String encodedQuery = URLEncoder.encode(query, "UTF-8");
        String urlString = "http://localhost:8085/api/weather/suggestions?query=" + encodedQuery;
        
        System.out.println("Запрос к: " + urlString);
        
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        
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
            System.out.println("Ответ сервера: " + jsonResponse);
            
            JsonArray jsonArray = gson.fromJson(jsonResponse, JsonArray.class);
            
            for (int i = 0; i < jsonArray.size() && i < 5; i++) {
                JsonObject cityJson = jsonArray.get(i).getAsJsonObject();
                CitySuggestion suggestion = new CitySuggestion();
                
                if (cityJson.has("name")) {
                    suggestion.setName(cityJson.get("name").getAsString());
                }
                
                if (cityJson.has("country")) {
                    suggestion.setCountry(cityJson.get("country").getAsString());
                }
                
                if (cityJson.has("state")) {
                    suggestion.setState(cityJson.get("state").getAsString());
                }
                
                if (cityJson.has("lat")) {
                    suggestion.setLat(cityJson.get("lat").getAsDouble());
                }
                
                if (cityJson.has("lon")) {
                    suggestion.setLon(cityJson.get("lon").getAsDouble());
                }
                
                suggestions.add(suggestion);
                System.out.println("Найден город: " + suggestion.getDisplayName());
            }
            
            System.out.println("Найдено " + suggestions.size() + " городов");
            
        } else {
            System.out.println("Ошибка сервера: код " + responseCode);
        }
    } catch (Exception e) {
        System.out.println("Ошибка при поиске городов: " + e.getMessage());
    }
    
    return suggestions;
    }
}