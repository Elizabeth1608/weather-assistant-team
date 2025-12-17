public class WeatherAI {
    
    public Recommendation analyze(String activity, WeatherData weather) {
        switch(activity.toLowerCase()) {
            case "рыбалка":
                return analyzeFishing(weather);
            case "бег":
                return analyzeRunning(weather);
            case "пикник":
                return analyzePicnic(weather);
            case "велосипед":
                return analyzeCycling(weather);
            case "поход":
                return analyzeHiking(weather);
            default:
                return new Recommendation("Неизвестная активность", 0, "");
        }
    }
    
    private Recommendation analyzeFishing(WeatherData w) {
        int score = 0;
        StringBuilder details = new StringBuilder();
        
        if (w.getPressure() >= 750 && w.getPressure() <= 770) {
            score += 40;
            details.append("✅ Давление идеальное: ").append(w.getPressure()).append(" мм\n");
        }
        
        if (w.getWindSpeed() < 5) {
            score += 30;
            details.append("✅ Ветер слабый: ").append(w.getWindSpeed()).append(" м/с\n");
        }
        
        if (w.getTemperature() >= 10 && w.getTemperature() <= 25) {
            score += 20;
            details.append("✅ Температура комфортная: ").append(w.getTemperature()).append("°C\n");
        }
        
        if (w.getPrecipitation() == 0) {
            score += 10;
            details.append("✅ Без осадков\n");
        }
        
        String advice = score >= 70 ? "🎣 ОТЛИЧНО! ЕХАТЬ НА РЫБАЛКУ!" : 
                       score >= 40 ? "⚠️ МОЖНО ПОПРОБОВАТЬ" : "❌ НЕ РЕКОМЕНДУЕТСЯ";
        
        return new Recommendation(advice, score, details.toString());
    }
    
    private Recommendation analyzeRunning(WeatherData w) {
        if (w.getTemperature() >= 10 && w.getTemperature() <= 22 && 
            w.getWindSpeed() < 7 && w.getPrecipitation() == 0) {
            return new Recommendation("🏃 ОТЛИЧНО ДЛЯ БЕГА!", 90, 
                "Темп: " + w.getTemperature() + "°C, ветер: " + w.getWindSpeed() + " м/с");
        }
        return new Recommendation("❌ НЕ БЕЖАТЬ", 30, "Условия не подходят");
    }
    
    private Recommendation analyzePicnic(WeatherData w) {
        if (w.getTemperature() >= 18 && w.getTemperature() <= 28 && 
            w.getWindSpeed() < 6 && w.getPrecipitation() == 0) {
            return new Recommendation("🧺 ИДЕАЛЬНО ДЛЯ ПИКНИКА!", 95, "");
        }
        return new Recommendation("❌ НЕ ЕХАТЬ НА ПИКНИК", 40, "");
    }
    
    private Recommendation analyzeCycling(WeatherData w) {
        if (w.getTemperature() >= 15 && w.getTemperature() <= 25 && 
            w.getWindSpeed() < 10 && w.getPrecipitation() == 0) {
            return new Recommendation("🚴 ОТЛИЧНО ДЛЯ ВЕЛОСИПЕДА!", 85, "");
        }
        return new Recommendation("❌ НЕ ЕХАТЬ НА ВЕЛОСИПЕДЕ", 35, "");
    }
    
    private Recommendation analyzeHiking(WeatherData w) {
        if (w.getTemperature() >= 10 && w.getTemperature() <= 20 && 
            w.getWindSpeed() < 8 && w.getPrecipitation() == 0) {
            return new Recommendation("🥾 ОТЛИЧНО ДЛЯ ПОХОДА!", 88, "");
        }
        return new Recommendation("❌ НЕ ИДТИ В ПОХОД", 38, "");
    }
}