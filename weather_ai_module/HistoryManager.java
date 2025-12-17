import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private static final String HISTORY_FILE = "weather_history.csv";
    
    public void saveToHistory(String activity, WeatherData weather, Recommendation recommendation) {
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
    
    public List<String> getHistory() {
        List<String> history = new ArrayList<>();
        File file = new File(HISTORY_FILE);
        
        if (!file.exists()) return history;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                history.add(line);
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения истории: " + e.getMessage());
        }
        return history;
    }
    
    public void printHistory() {
        List<String> history = getHistory();
        System.out.println("\n=== ИСТОРИЯ ЗАПРОСОВ (" + history.size() + " записей) ===");
        for (String record : history) {
            System.out.println(record);
        }
    }
}