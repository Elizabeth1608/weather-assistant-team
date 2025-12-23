package com.weather.client.ai;

import com.weather.client.model.WeatherData;

public class WeatherAI {
    
    public Recommendation analyzeWithMyData(String activity, WeatherData weather) {
        int score = 0;
        StringBuilder details = new StringBuilder();
        double temp = weather.getTemperature();
        double wind = weather.getWindSpeed();
        double pressure = weather.getPressure();
        double humidity = weather.getHumidity();
        
        switch(activity.toLowerCase()) {
            case "рыбалка":
                return analyzeFishing(temp, wind, pressure, humidity);
                
            case "бег":
                return analyzeRunning(temp, wind, humidity);
                
            case "пикник":
                return analyzePicnic(temp, wind);
                
            case "велосипед":
                return analyzeCycling(temp, wind);
                
            case "поход":
                return analyzeHiking(temp, wind);
                
            default:
                return new Recommendation("Неизвестная активность", 0, "");
        }
    }
    
    private Recommendation analyzeFishing(double temp, double wind, double pressure, double humidity) {
        int score = 0;
        StringBuilder details = new StringBuilder();
        
        if (temp >= 10 && temp <= 25) {
            score += 30;
            details.append("✅ Идеальная температура для рыбалки\n");
        } else if (temp >= 5 && temp <= 30) {
            score += 20;
            details.append("👎 Температура пограничная\n");
        } else {
            score += 5;
            details.append("❌ Температура не подходит\n");
        }
        
        if (wind < 5) {
            score += 30;
            details.append("✅ Слабый ветер - отлично\n");
        } else if (wind < 10) {
            score += 20;
            details.append("👎 Умеренный ветер\n");
        } else {
            score += 5;
            details.append("❌ Слишком ветрено\n");
        }
        
        if (pressure >= 1010 && pressure <= 1020) {
            score += 25;
            details.append("✅ Идеальное давление\n");
        } else if (pressure >= 1000 && pressure <= 1030) {
            score += 15;
            details.append("👎 Давление в норме\n");
        } else {
            score += 10;
            details.append("❌ Давление нестабильное\n");
        }
        
        if (humidity <= 80) {
            score += 15;
            details.append("✅ Влажность комфортная\n");
        } else {
            score += 5;
            details.append("👎 Высокая влажность\n");
        }
        
        String advice;
        if (score >= 80) advice = "Отличный день для рыбалки!";
        else if (score >= 60) advice = "Хороший день для рыбалки";
        else if (score >= 40) advice = "Можно попробовать, но не самый лучший день";
        else advice = "Сегодня лучше остаться дома";
        
        return new Recommendation(advice, score, details.toString());
    }
    
    private Recommendation analyzeRunning(double temp, double wind, double humidity) {
        int score = 0;
        StringBuilder details = new StringBuilder();
        
        if (temp >= 15 && temp <= 20) {
            score += 40;
            details.append("✅ Идеальная температура для бега\n");
        } else if (temp >= 10 && temp <= 25) {
            score += 30;
            details.append("👎 Умеренная температура\n");
        } else {
            score += 10;
            details.append("❌ Экстремальная температура\n");
        }
        
        if (wind < 8) {
            score += 35;
            details.append("✅ Комфортный ветер\n");
        } else if (wind < 15) {
            score += 20;
            details.append("👎 Сильный встречный ветер\n");
        } else {
            score += 5;
            details.append("❌ Слишком ветрено для бега\n");
        }
        
        if (humidity <= 70) {
            score += 25;
            details.append("✅ Комфортная влажность\n");
        } else if (humidity <= 85) {
            score += 15;
            details.append("👎 Повышенная влажность\n");
        } else {
            score += 5;
            details.append("❌ Очень высокая влажность\n");
        }
        
        String advice;
        if (score >= 80) advice = "Идеальные условия для бега!";
        else if (score >= 60) advice = "Хороший день для пробежки";
        else if (score >= 40) advice = "Можно бегать, но осторожно";
        else advice = "Сегодня лучше отдохнуть";
        
        return new Recommendation(advice, score, details.toString());
    }
    
    private Recommendation analyzePicnic(double temp, double wind) {
        int score = 0;
        StringBuilder details = new StringBuilder();
        
        if (temp >= 18 && temp <= 26) {
            score += 50;
            details.append("✅ Идеальная температура для пикника\n");
        } else if (temp >= 15 && temp <= 30) {
            score += 35;
            details.append("👎 Умеренная температура\n");
        } else {
            score += 10;
            details.append("❌ Температура не комфортна\n");
        }
        
        if (wind < 6) {
            score += 50;
            details.append("✅ Штиль - отлично для пикника\n");
        } else if (wind < 12) {
            score += 30;
            details.append("👎 Легкий ветерок\n");
        } else {
            score += 5;
            details.append("❌ Сильный ветер испортит пикник\n");
        }
        
        String advice;
        if (score >= 90) advice = "Прекрасный день для пикника!";
        else if (score >= 70) advice = "Хороший день для пикника";
        else if (score >= 50) advice = "Пикник возможен, но не идеально";
        else advice = "Сегодня лучше пикник отложить";
        
        return new Recommendation(advice, score, details.toString());
    }
    
    private Recommendation analyzeCycling(double temp, double wind) {
        int score = 0;
        StringBuilder details = new StringBuilder();
        
        if (temp >= 16 && temp <= 24) {
            score += 45;
            details.append("✅ Идеальная температура для велосипеда\n");
        } else if (temp >= 12 && temp <= 28) {
            score += 35;
            details.append("👎 Умеренная температура\n");
        } else {
            score += 15;
            details.append("❌ Экстремальная температура\n");
        }
        
        if (wind < 10) {
            score += 40;
            details.append("✅ Комфортный ветер\n");
        } else if (wind < 18) {
            score += 25;
            details.append("👎 Сильный встречный ветер\n");
        } else {
            score += 5;
            details.append("❌ Очень ветрено\n");
        }
        
        score += 15;
        details.append("✅ Дороги сухие\n");
        
        String advice;
        if (score >= 85) advice = "Отличный день для велопрогулки!";
        else if (score >= 65) advice = "Хороший день для катания";
        else if (score >= 45) advice = "Можно кататься, но осторожно";
        else advice = "Сегодня лучше не кататься";
        
        return new Recommendation(advice, score, details.toString());
    }
    
    private Recommendation analyzeHiking(double temp, double wind) {
        int score = 0;
        StringBuilder details = new StringBuilder();
        
        if (temp >= 12 && temp <= 22) {
            score += 40;
            details.append("✅ Идеальная температура для похода\n");
        } else if (temp >= 8 && temp <= 26) {
            score += 30;
            details.append("👎 Умеренная температура\n");
        } else {
            score += 10;
            details.append("❌ Экстремальная температура\n");
        }
        
        if (wind < 12) {
            score += 40;
            details.append("✅ Комфортный ветер для похода\n");
        } else if (wind < 20) {
            score += 25;
            details.append("👎 Сильный ветер на вершинах\n");
        } else {
            score += 5;
            details.append("❌ Опасный ветер\n");
        }
        
        score += 20;
        details.append("✅ Хорошая видимость\n");
        
        String advice;
        if (score >= 90) advice = "Идеальный день для похода!";
        else if (score >= 70) advice = "Хороший день для похода";
        else if (score >= 50) advice = "Поход возможен, но будьте осторожны";
        else advice = "Сегодня лучше не ходить в поход";
        
        return new Recommendation(advice, score, details.toString());
    }
}