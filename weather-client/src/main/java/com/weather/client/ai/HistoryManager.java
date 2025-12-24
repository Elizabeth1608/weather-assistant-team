package com.weather.client.ai;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private static final String HISTORY_FILE = "weather_history.csv";
    
    public void saveToHistory(String activity, SimpleWeatherData weather, Recommendation recommendation) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        String record = String.format("%s,%s,%.1f,%.1f,%.1f,%d,\"%s\",%d\n",
            timestamp,
            activity,
            weather.getTemperature(),
            weather.getPressure(),
            weather.getWindSpeed(),
            weather.getPrecipitation(),
            recommendation.getAdvice().replace("\"", "'"),
            recommendation.getScore()
        );
        
        try (FileWriter writer = new FileWriter(HISTORY_FILE, true)) {
            writer.write(record);
        } catch (IOException e) {
            System.out.println("Ошибка сохранения истории: " + e.getMessage());
        }
    }
    
    public void saveFromMainController(String activity, 
                                       com.weather.client.model.WeatherData weather,
                                       Recommendation recommendation) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        String record = String.format("%s,%s,%.1f,%.1f,%.1f,%d,\"%s\",%d\n",
            timestamp,
            activity,
            weather.getTemperature(),
            weather.getPressure(),
            weather.getWindSpeed(),
            0,
            recommendation.getAdvice().replace("\"", "'"),
            recommendation.getScore()
        );
        
        try (FileWriter writer = new FileWriter(HISTORY_FILE, true)) {
            writer.write(record);
            System.out.println("Запись сохранена в историю: " + activity + ", " + weather.getCity());
        } catch (IOException e) {
            System.out.println("Ошибка сохранения истории: " + e.getMessage());
        }
    }
    
    public List<String> getHistory() {
        List<String> history = new ArrayList<>();
        File file = new File(HISTORY_FILE);
        
        if (!file.exists()) {
            System.out.println("Файл истории не найден: " + HISTORY_FILE);
            return history;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                history.add(line);
                count++;
            }
            System.out.println("Прочитано " + count + " записей из истории");
        } catch (IOException e) {
            System.out.println("Ошибка чтения истории: " + e.getMessage());
        }
        return history;
    }
    
    public void printHistory() {
        List<String> history = getHistory();
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📊 ИСТОРИЯ ЗАПРОСОВ (" + history.size() + " записей)");
        System.out.println("=".repeat(50));
        
        if (history.isEmpty()) {
            System.out.println("История пуста. Выполните запросы погоды.");
            return;
        }
        
        System.out.println("Время | Активность | Температура | Давление | Ветер | Осадки | Рекомендация | Оценка");
        System.out.println("-".repeat(80));
        
        for (String record : history) {
            String[] parts = record.split(",");
            if (parts.length >= 8) {
                System.out.printf("%s | %s | %.1f°C | %.1f hPa | %.1f м/с | %s | %s | %d/100\n",
                    parts[0],
                    parts[1],
                    Double.parseDouble(parts[2]),
                    Double.parseDouble(parts[3]), 
                    Double.parseDouble(parts[4]),
                    parts[5], 
                    parts[6].replace("\"", ""), 
                    Integer.parseInt(parts[7])
                );
            }
        }
        System.out.println("=".repeat(50));
    }
    
    public void clearHistory() {
        File file = new File(HISTORY_FILE);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("История очищена");
            } else {
                System.out.println("Не удалось очистить историю");
            }
        }
    }
    
    public List<String> getRecentHistory(int limit) {
        List<String> allHistory = getHistory();
        if (allHistory.size() <= limit) {
            return allHistory;
        }
        return allHistory.subList(allHistory.size() - limit, allHistory.size());
    }
}