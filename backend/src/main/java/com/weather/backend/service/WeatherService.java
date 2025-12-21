package com.weather.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.backend.dto.CitySuggestion;
import com.weather.backend.entity.WeatherLog;
import com.weather.backend.exception.CityNotFoundException;
import com.weather.backend.exception.WeatherApiException;
import com.weather.backend.repository.WeatherLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {
  private final WeatherLogRepository weatherLogRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${weather.api.key}")
    private String apiKey;
    
    @Value("${weather.api.url}")
    private String apiUrl;

    @Value("${weather.api.forecast-url}")
    private String forecastUrl;

    @Value("${weather.api.geocode-url}")
    private String geocodeUrl;
    
    public WeatherLog getWeather(String city) {
        log.info("Запрос погоды для города: {}", city);
        
        try {
            String url = String.format("%s?q=%s&appid=%s&units=metric&lang=ru", 
                                     apiUrl, city, apiKey);
            
            log.debug("Отправка запроса к OpenWeatherMap: {}", url);
            
            String jsonResponse = restTemplate.getForObject(url, String.class);
            
            // Проверка на несуществующий город
            if (jsonResponse == null || jsonResponse.contains("\"cod\":\"404\"")) {
                log.error("Город не найден: {}", city);
                throw new CityNotFoundException(city);
            }
            
            if (jsonResponse.contains("\"cod\":\"401\"")) {
                log.error("Неверный API ключ для города: {}", city);
                throw new WeatherApiException("Неверный API ключ", city, apiUrl);
            }
            
            // === ПАРСИМ ОСНОВНЫЕ ДАННЫЕ ===
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode mainNode = root.path("main");
            JsonNode weatherNode = root.path("weather").get(0);
            JsonNode windNode = root.path("wind");
            JsonNode cloudsNode = root.path("clouds");
            JsonNode sysNode = root.path("sys");
            
            double temperature = mainNode.path("temp").asDouble();
            double feelsLike = mainNode.path("feels_like").asDouble();
            int humidity = mainNode.path("humidity").asInt();
            int pressure = mainNode.path("pressure").asInt();
            
            double windSpeed = windNode.path("speed").asDouble();
            int windDeg = windNode.path("deg").asInt();
            
            String description = weatherNode.path("description").asText();
            String weatherMain = weatherNode.path("main").asText();
            String icon = weatherNode.path("icon").asText();
            
            int clouds = cloudsNode.path("all").asInt();
            int visibility = root.path("visibility").asInt();
            
            long sunrise = sysNode.path("sunrise").asLong();
            long sunset = sysNode.path("sunset").asLong();
            
            // === ПАРСИМ ОСАДКИ ===
            double precipitation = 0.0;
            JsonNode rainNode = root.path("rain");
            JsonNode snowNode = root.path("snow");
            if (rainNode != null && rainNode.has("1h")) {
                precipitation = rainNode.path("1h").asDouble();
            } else if (snowNode != null && snowNode.has("1h")) {
                precipitation = snowNode.path("1h").asDouble();
            }
            
            // === КОНВЕРТАЦИЯ ГРАДУСОВ В НАПРАВЛЕНИЕ ===
            String windDirection = convertDegToDirection(windDeg);
            
            // === ГЕНЕРАЦИЯ РЕКОМЕНДАЦИЙ ===
            String recommendation = generateDetailedRecommendation(
                temperature, feelsLike, description, humidity,
                windSpeed, clouds, precipitation
            );
            
            // === СОЗДАЕМ И СОХРАНЯЕМ ОБЪЕКТ ===
            WeatherLog weatherLog = new WeatherLog();
            weatherLog.setCity(city);
            weatherLog.setTemperature(temperature);
            weatherLog.setFeelsLike(feelsLike);
            weatherLog.setHumidity(humidity);
            weatherLog.setPressure(pressure);
            weatherLog.setWindSpeed(windSpeed);
            weatherLog.setWindDeg(windDeg);
            weatherLog.setWindDirection(windDirection);
            weatherLog.setClouds(clouds);
            weatherLog.setVisibility(visibility);
            weatherLog.setPrecipitation(precipitation);
            weatherLog.setSunrise(sunrise);
            weatherLog.setSunset(sunset);
            weatherLog.setWeatherMain(weatherMain);
            weatherLog.setWeatherIcon(icon);
            
            // Описание (обрезаем если слишком длинное)
            if (description.length() > 200) {
                description = description.substring(0, 200);
            }
            weatherLog.setDescription(description);
            
            // Рекомендация (обрезаем если слишком длинная)
            if (recommendation.length() > 1000) {
                recommendation = recommendation.substring(0, 997) + "...";
            }
            weatherLog.setRecommendation(recommendation);
            
            WeatherLog savedWeatherLog = weatherLogRepository.save(weatherLog);
            log.info("Погода сохранена в БД для города: {}", city);
            
            // Логируем уведомление вместо WebSocket
            log.info("Уведомление: Погода для {} обновлена: {}°C", city, savedWeatherLog.getTemperature());
            
            return savedWeatherLog;
            
        } catch (CityNotFoundException e) {
            // Пробрасываем дальше для обработки в GlobalExceptionHandler
            throw e;
        } catch (HttpClientErrorException e) {
            log.error("HTTP ошибка API для города {}: {}", city, e.getMessage());
            throw new WeatherApiException("Ошибка при запросе к OpenWeatherMap", city, apiUrl, e);
        } catch (Exception e) {
            log.error("Неизвестная ошибка при получении погоды для {}: {}", city, e.getMessage());
            throw new WeatherApiException("Неизвестная ошибка при получении погоды", city, apiUrl, e);
        }
    }
    
    // ========== КОНВЕРТАЦИЯ ГРАДУСОВ В НАПРАВЛЕНИЕ ВЕТРА ==========
    private String convertDegToDirection(int degrees) {
        String[] directions = {"С", "СВ", "В", "ЮВ", "Ю", "ЮЗ", "З", "СЗ"};
        int index = (int) Math.round((degrees % 360) / 45.0) % 8;
        return directions[index];
    }
    
    // ========== БАЗОВЫЙ МЕТОД ГЕНЕРАЦИИ РЕКОМЕНДАЦИЙ ==========
    private String generateRecommendation(double temp, String description, double windSpeed) {
        StringBuilder recommendation = new StringBuilder();
        
        if (temp < -10) {
            recommendation.append("❄️ Сильный мороз! Теплая одежда обязательна. ");
        } else if (temp < 0) {
            recommendation.append("🥶 Мороз. Теплая куртка, шапка, перчатки. ");
        } else if (temp < 10) {
            recommendation.append("🧥 Прохладно. Куртка и шарф пригодятся. ");
        } else if (temp < 20) {
            recommendation.append("👍 Комфортно. Легкая куртка. ");
        } else if (temp < 30) {
            recommendation.append("☀️ Тепло. Легкая одежда. ");
        } else {
            recommendation.append("🔥 Жарко! Пейте воду, головной убор. ");
        }
        
        if (windSpeed > 15) {
            recommendation.append("💨 Очень сильный ветер! Будьте осторожны. ");
        } else if (windSpeed > 10) {
            recommendation.append("🌬️ Сильный ветер. ");
        } else if (windSpeed > 5) {
            recommendation.append("🍃 Умеренный ветер. ");
        }
        
        String descLower = description.toLowerCase();
        if (descLower.contains("дождь") || descLower.contains("rain")) {
            recommendation.append("☔ Возьмите зонт! ");
        }
        if (descLower.contains("снег") || descLower.contains("snow")) {
            recommendation.append("⛄ Осторожно, скользко! ");
        }
        if (descLower.contains("гроза") || descLower.contains("thunderstorm")) {
            recommendation.append("⛈️ Гроза! Оставайтесь в помещении. ");
        }
        if (descLower.contains("туман") || descLower.contains("fog")) {
            recommendation.append("🌫️ Туман. Будьте осторожны. ");
        }
        
        String result = recommendation.toString();
        if (result.length() > 500) {
            result = result.substring(0, 497) + "...";
        }
        
        return result;
    }
    
    // ========== УЛУЧШЕННЫЙ МЕТОД ГЕНЕРАЦИИ РЕКОМЕНДАЦИЙ ==========
    private String generateDetailedRecommendation(double temp, double feelsLike, 
                                                String description, int humidity,
                                                double windSpeed, int clouds,
                                                double precipitation) {
        StringBuilder recommendation = new StringBuilder();
        
        // Температура
        if (temp < -15) {
            recommendation.append("🚨 Экстремальный холод! Оставайтесь в помещении. ");
        } else if (temp < -5) {
            recommendation.append("❄️ Очень холодно. Теплая одежда, шапка, перчатки. ");
        } else if (temp < 5) {
            recommendation.append("🥶 Холодно. Куртка обязательна. ");
        } else if (temp < 15) {
            recommendation.append("🌥️ Прохладно. Легкая куртка или свитер. ");
        } else if (temp < 25) {
            recommendation.append("😊 Комфортно. Легкая одежда. ");
        } else if (temp < 35) {
            recommendation.append("🔥 Жарко. Легкая одежда, головной убор. ");
        } else {
            recommendation.append("🚨 Очень жарко! Избегайте солнца днем. ");
        }
        
        // Разница между температурой и "ощущается как"
        if (Math.abs(temp - feelsLike) > 3) {
            if (feelsLike < temp) {
                recommendation.append("Ветер делает холоднее. ");
            } else {
                recommendation.append("Влажность делает теплее. ");
            }
        }
        
        // Ветер
        if (windSpeed > 15) {
            recommendation.append("💨 Ураганный ветер! Будьте осторожны. ");
        } else if (windSpeed > 10) {
            recommendation.append("💨 Очень сильный ветер. ");
        } else if (windSpeed > 5) {
            recommendation.append("🍃 Умеренный ветер. ");
        }
        
        // Осадки
        String descLower = description.toLowerCase();
        if (precipitation > 0) {
            if (descLower.contains("снег")) {
                recommendation.append("❄️ Снег. Осторожно, гололед! ");
            } else if (descLower.contains("дождь")) {
                if (precipitation > 10) {
                    recommendation.append("☔️ Сильный дождь! Зонт и дождевик. ");
                } else {
                    recommendation.append("🌧️ Дождь. Возьмите зонт. ");
                }
            }
        }
        
        // Облачность
        if (clouds < 20) {
            recommendation.append("☀️ Солнечно. Солнцезащитный крем. ");
        } else if (clouds > 80) {
            recommendation.append("☁️ Пасмурно. ");
        }
        
        // Влажность
        if (humidity > 85) {
            recommendation.append("💧 Очень влажно. Неприятно. ");
        } else if (humidity > 70) {
            recommendation.append("💧 Влажно. ");
        } else if (humidity < 30) {
            recommendation.append("🏜️ Сухо. Пейте больше воды. ");
        }
        
        // Дополнительные условия
        if (descLower.contains("туман") || descLower.contains("fog")) {
            recommendation.append("🌫️ Туман. Плохая видимость. ");
        }
        if (descLower.contains("гроза") || descLower.contains("thunderstorm")) {
            recommendation.append("⛈️ Гроза! Оставайтесь в помещении. ");
        }
        
        // Финальный совет
        recommendation.append(" Хорошего дня!");
        
        String result = recommendation.toString();
        if (result.length() > 500) {
            result = result.substring(0, 497) + "...";
        }
        
        return result;
    }
    
    // ========== МЕТОД: ПРОГНОЗ НА 5 ДНЕЙ ==========
    public List<WeatherLog> get5DayForecast(String city) {
        log.info("Запрос прогноза на 5 дней для города: {}", city);
        
        try {
            String url = String.format("%s?q=%s&appid=%s&units=metric&lang=ru", 
                                     forecastUrl, city, apiKey);
            
            log.debug("Запрос прогноза к: {}", url);
            
            String jsonResponse = restTemplate.getForObject(url, String.class);
            
            // Проверка на несуществующий город
            if (jsonResponse == null || jsonResponse.contains("\"cod\":\"404\"")) {
                log.error("Город не найден для прогноза: {}", city);
                throw new CityNotFoundException(city);
            }
            
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode list = root.path("list");
            
            List<WeatherLog> forecast = new ArrayList<>();
            
            // Берем прогноз на 12:00 каждого дня (каждые 8 записей = 24 часа / 3 часа)
            for (int i = 0; i < 40 && i < list.size(); i += 8) {
                JsonNode dayData = list.get(i);
                
                WeatherLog dayLog = new WeatherLog();
                dayLog.setCity(city);
                dayLog.setTemperature(dayData.path("main").path("temp").asDouble());
                
                String desc = dayData.path("weather").get(0).path("description").asText();
                if (desc.length() > 200) desc = desc.substring(0, 200);
                dayLog.setDescription(desc);
                
                dayLog.setHumidity(dayData.path("main").path("humidity").asInt());
                dayLog.setPressure(dayData.path("main").path("pressure").asInt());
                dayLog.setWindSpeed(dayData.path("wind").path("speed").asDouble());
                dayLog.setWindDeg(dayData.path("wind").path("deg").asInt());
                dayLog.setWindDirection(convertDegToDirection(dayLog.getWindDeg()));
                dayLog.setClouds(dayData.path("clouds").path("all").asInt());
                
                // Иконка для прогноза
                String weatherIcon = dayData.path("weather").get(0).path("icon").asText();
                dayLog.setWeatherIcon(weatherIcon);
                dayLog.setWeatherMain(dayData.path("weather").get(0).path("main").asText());
                
                // Генерация рекомендации
                String recommendation = generateRecommendation(
                    dayLog.getTemperature(), 
                    desc, 
                    dayLog.getWindSpeed()
                );
                dayLog.setRecommendation(recommendation);
                
                forecast.add(dayLog);
            }
            
            log.info("Прогноз получен: {} дней", forecast.size());
            
            // Логируем вместо WebSocket
            log.info("Уведомление: Прогноз на 5 дней для {} загружен", city);
            
            return forecast;
            
        } catch (CityNotFoundException e) {
            throw e;
        } catch (HttpClientErrorException e) {
            log.error("HTTP ошибка API прогноза для города {}: {}", city, e.getMessage());
            throw new WeatherApiException("Ошибка при запросе прогноза к OpenWeatherMap", city, forecastUrl, e);
        } catch (Exception e) {
            log.error("Неизвестная ошибка при получении прогноза для {}: {}", city, e.getMessage());
            throw new WeatherApiException("Неизвестная ошибка при получении прогноза", city, forecastUrl, e);
        }
    }

    // ========== МЕТОД: ПОИСК ГОРОДОВ ==========
    public List<CitySuggestion> getCitySuggestions(String query) {
        log.info("Поиск городов по запросу: {}", query);
        
        try {
            String url = String.format("%s?q=%s&limit=5&appid=%s", 
                                     geocodeUrl, query, apiKey);
            
            log.debug("Запрос геокодинга к: {}", url);
            
            String jsonResponse = restTemplate.getForObject(url, String.class);
            
            JsonNode root = objectMapper.readTree(jsonResponse);
            
            List<CitySuggestion> suggestions = new ArrayList<>();
            
            for (JsonNode cityNode : root) {
                CitySuggestion suggestion = new CitySuggestion();
                suggestion.setName(cityNode.path("name").asText());
                suggestion.setCountry(cityNode.path("country").asText());
                suggestion.setState(cityNode.path("state").asText());
                suggestion.setLat(cityNode.path("lat").asDouble());
                suggestion.setLon(cityNode.path("lon").asDouble());
                
                suggestions.add(suggestion);
            }
            
            log.info("Найдено городов: {}", suggestions.size());
            
            // Логируем вместо WebSocket
            log.info("Уведомление: Поиск городов по запросу '{}' завершен. Найдено: {}", query, suggestions.size());
            
            return suggestions;
            
        } catch (HttpClientErrorException e) {
            log.error("HTTP ошибка геокодинга для запроса {}: {}", query, e.getMessage());
            throw new WeatherApiException("Ошибка при поиске городов", query, geocodeUrl, e);
        } catch (Exception e) {
            log.error("Неизвестная ошибка при поиске городов для {}: {}", query, e.getMessage());
            throw new WeatherApiException("Неизвестная ошибка при поиске городов", query, geocodeUrl, e);
        }
    }
}