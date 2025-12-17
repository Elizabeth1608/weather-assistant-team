public class Main {
    public static void main(String[] args) {
        System.out.println("=== WEATHER AI MODULE ===\n");
        
        WeatherAI ai = new WeatherAI();
        WeatherData weather = new WeatherData(20.0, 760.0, 3.0, 0);
        
        System.out.println("Активность: рыбалка");
        System.out.println("Погода: " + weather);
        System.out.println("\nРезультат:");
        
        Recommendation result = ai.analyze("рыбалка", weather);
        System.out.println(result);
        
        System.out.println("\n✅ Модуль готов к использованию");
    }
}