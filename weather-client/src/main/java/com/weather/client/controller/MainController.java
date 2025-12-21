package com.weather.client.controller;

import com.weather.client.model.WeatherData;
import com.weather.client.service.ApiService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class MainController {
    
    @FXML private TextField cityInput;
    @FXML private Button searchButton;
    @FXML private Label cityLabel;
    @FXML private Label tempLabel;
    @FXML private Label feelsLikeLabel;
    @FXML private Label humidityLabel;
    @FXML private Label pressureLabel;
    @FXML private Label windLabel;
    @FXML private Label descriptionLabel;
    @FXML private ImageView weatherIcon;
    @FXML private Label recommendationLabel;
    
    private final ApiService apiService = new ApiService();
    
    @FXML
    public void initialize() {
        System.out.println("🌤️ Погодный Ассистент запущен");
        
        // Назначаем обработчик
        searchButton.setOnAction(event -> searchWeather());
        
        // Автозапуск при нажатии Enter в поле ввода
        cityInput.setOnAction(event -> searchWeather());
        
        // Красивая анимация при запуске
        animateWelcome();
    }
    
    private void animateWelcome() {
        cityInput.setPromptText("Начните вводить город...");
        cityInput.requestFocus();
    }
    
    private void searchWeather() {
        String city = cityInput.getText().trim();
        
        if (city.isEmpty()) {
            showAlert("Ошибка", "Введите название города", Alert.AlertType.WARNING);
            return;
        }
        
        // Показываем загрузку
        searchButton.setDisable(true);
        searchButton.setText("⌛ Загрузка...");
        recommendationLabel.setText("⏳ Получаем данные о погоде...");
        
        // Анимация поиска
        cityLabel.setText("📍 " + city);
        tempLabel.setText("--°C");
        
        new Thread(() -> {
            try {
                WeatherData weather = apiService.getCurrentWeather(city);
                
                // Обновляем UI в UI-потоке
                javafx.application.Platform.runLater(() -> {
                    if (weather != null) {
                        updateWeatherUI(weather);
                        showSuccessAlert("Погода успешно загружена для " + city);
                    } else {
                        showErrorUI();
                        showAlert("Ошибка", 
                                "Не удалось получить погоду для города: " + city + 
                                "\nПроверьте подключение к серверу или название города.", 
                                Alert.AlertType.ERROR);
                    }
                    
                    // Восстанавливаем кнопку
                    searchButton.setDisable(false);
                    searchButton.setText("🔍 Поиск");
                });
                
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    showErrorUI();
                    showAlert("Критическая ошибка", 
                            "Ошибка приложения: " + e.getMessage(), 
                            Alert.AlertType.ERROR);
                    searchButton.setDisable(false);
                    searchButton.setText("🔍 Поиск");
                });
            }
        }).start();
    }
    
    private void updateWeatherUI(WeatherData weather) {
        try {
            // Форматируем название города
            String formattedCity = "📍 " + weather.getCity();
            if (weather.getCity().length() > 15) {
                formattedCity = "📍 " + weather.getCity().substring(0, 15) + "...";
            }
            cityLabel.setText(formattedCity);
            
            // Температура с цветом
            double temp = weather.getTemperature();
            tempLabel.setText(String.format("%.0f°C", temp));
            
            // Цвет температуры в зависимости от значения
            if (temp < 0) {
                tempLabel.setStyle("-fx-text-fill: #29B6F6;"); // голубой для мороза
            } else if (temp < 10) {
                tempLabel.setStyle("-fx-text-fill: #42A5F5;"); // синий для прохлады
            } else if (temp < 20) {
                tempLabel.setStyle("-fx-text-fill: #2196F3;"); // основной синий
            } else if (temp < 30) {
                tempLabel.setStyle("-fx-text-fill: #FF9800;"); // оранжевый для тепла
            } else {
                tempLabel.setStyle("-fx-text-fill: #F44336;"); // красный для жары
            }
            
            // Остальные данные
            feelsLikeLabel.setText(String.format("%.0f°C", weather.getFeelsLike()));
            humidityLabel.setText(String.format("%.0f%%", weather.getHumidity()));
            pressureLabel.setText(String.format("%.0f hPa", weather.getPressure()));
            windLabel.setText(String.format("%.1f м/с", weather.getWindSpeed()));
            
            // Описание с эмодзи
            String description = weather.getDescription();
            String emoji = getWeatherEmoji(description);
            descriptionLabel.setText(emoji + " " + capitalizeFirstLetter(description));
            
            // Загружаем иконку
            if (weather.getIcon() != null) {
                String iconUrl = "https://openweathermap.org/img/wn/" + weather.getIcon() + "@2x.png";
                Image image = new Image(iconUrl, 100, 100, true, true);
                weatherIcon.setImage(image);
                
                // Если иконка не загрузилась, показываем эмодзи
                image.errorProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal) {
                        weatherIcon.setImage(null);
                    }
                });
            }
            
            // Генерируем красивую рекомендацию
            generateBeautifulRecommendation(weather);
            
        } catch (Exception e) {
            System.err.println("Ошибка обновления UI: " + e.getMessage());
        }
    }
    
    private String getWeatherEmoji(String description) {
        String desc = description.toLowerCase();
        if (desc.contains("ясн") || desc.contains("солн") || desc.contains("clear")) return "☀️";
        if (desc.contains("облач") || desc.contains("cloud")) return "⛅";
        if (desc.contains("дожд") || desc.contains("rain")) return "🌧️";
        if (desc.contains("снег") || desc.contains("snow")) return "⛄";
        if (desc.contains("гроз") || desc.contains("thunder")) return "⛈️";
        if (desc.contains("туман") || desc.contains("fog")) return "🌫️";
        return "🌈";
    }
    
    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
    
    private void generateBeautifulRecommendation(WeatherData weather) {
        StringBuilder rec = new StringBuilder();
        double temp = weather.getTemperature();
        double wind = weather.getWindSpeed();
        double humidity = weather.getHumidity();
        String desc = weather.getDescription().toLowerCase();
        
        rec.append("💡 На основе текущей погоды:\n\n");
        
        rec.append("👕 Одежда: ");
        if (temp < -5) rec.append("Термобельё, пуховик, шапка, шарф, варежки\n");
        else if (temp < 5) rec.append("Тёплая куртка, шапка, перчатки\n");
        else if (temp < 15) rec.append("Куртка, свитер, джинсы\n");
        else if (temp < 25) rec.append("Футболка, кофта, ветровка\n");
        else rec.append("Футболка, шорты, головной убор\n");
        
        rec.append("\n🎯 Активности: ");
        if (desc.contains("дожд") || desc.contains("снег")) {
            rec.append("Отличный день для дома: книги, фильмы, хобби\n");
        } else if (temp > 25) {
            rec.append("Пляж, бассейн, пикник в тени\n");
        } else if (temp > 15 && !desc.contains("облач")) {
            rec.append("Прогулка, велосипед, пикник\n");
        } else {
            rec.append("Кафе, музеи, шоппинг\n");
        }
        
        rec.append("\n✨ Советы: ");
        if (wind > 10) rec.append("Сильный ветер, будьте осторожны. ");
        if (humidity > 80) rec.append("Высокая влажность. ");
        if (temp > 30) rec.append("Пейте больше воды. ");
        if (temp < 0) rec.append("Теплее одевайтесь. ");
        
        recommendationLabel.setText(rec.toString());
    }
    
    private void showErrorUI() {
        cityLabel.setText("❌ Ошибка");
        tempLabel.setText("--°C");
        tempLabel.setStyle("-fx-text-fill: #F44336;");
        feelsLikeLabel.setText("--°C");
        humidityLabel.setText("--%");
        pressureLabel.setText("-- hPa");
        windLabel.setText("-- м/с");
        descriptionLabel.setText("❌ Не удалось получить данные");
        weatherIcon.setImage(null);
        recommendationLabel.setText("Проверьте подключение к серверу и повторите попытку.");
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccessAlert(String message) {
        // Можно сделать красивый Toast, но пока просто лог
        System.out.println("✅ " + message);
    }
}