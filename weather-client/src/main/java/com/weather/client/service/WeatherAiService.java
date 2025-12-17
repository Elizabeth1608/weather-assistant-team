package com.weather.client.service;

import com.weather.client.config.Config;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class WeatherAiService {
    private static final Gson gson = new Gson();
    
    public String analyzeActivity(String city, String activity, 
                                  double temperature, double windSpeed, 
                                  double humidity, String conditions) {
        try {
            URL url = new URL(Config.API_BASE_URL + "/api/ai/analyze");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            
            JsonObject request = new JsonObject();
            request.addProperty("city", city);
            request.addProperty("activity", activity);
            request.addProperty("temperature", temperature);
            request.addProperty("windSpeed", windSpeed);
            request.addProperty("humidity", humidity);
            request.addProperty("conditions", conditions);
            
            String jsonInput = gson.toJson(request);
            
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                return response.toString();
            }
        } catch (Exception e) {
            System.err.println("Ошибка при анализе активности: " + e.getMessage());
        }
        
        return getSimpleRecommendation(activity, temperature, windSpeed, conditions);
    }
    
    private String getSimpleRecommendation(String activity, double temp, 
                                          double wind, String conditions) {
        StringBuilder recommendation = new StringBuilder();
        
        switch (activity.toLowerCase()) {
            case "рыбалка":
            case "fishing":
                if (wind > 10) {
                    recommendation.append("❌ Не рекомендуется: сильный ветер (").append(wind).append(" м/с)");
                } else if (temp < 5) {
                    recommendation.append("❌ Не рекомендуется: слишком холодно (").append(temp).append("°C)");
                } else {
                    recommendation.append("✅ Хорошие условия для рыбалки");
                }
                break;
                
            case "бег":
            case "running":
                if (temp > 30) {
                    recommendation.append("❌ Осторожно: жарко для бега (").append(temp).append("°C)");
                } else if (conditions.contains("дождь")) {
                    recommendation.append("⚠️ Можно бегать, но осторожно: дождь");
                } else {
                    recommendation.append("✅ Отличная погода для бега");
                }
                break;
                
            default:
                recommendation.append("✅ Условия подходящие для ").append(activity);
        }
        
        return recommendation.toString();
    }
}