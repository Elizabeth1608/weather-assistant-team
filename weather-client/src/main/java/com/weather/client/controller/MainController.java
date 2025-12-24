package com.weather.client.controller;

import com.weather.client.model.CitySuggestion;
import com.weather.client.model.WeatherData;
import com.weather.client.model.ForecastData;
import com.weather.client.service.ApiService;
import com.weather.client.service.DatabaseService;
import com.weather.client.ai.WeatherAI;
import com.weather.client.ai.Recommendation;
import com.weather.client.ai.WeatherLogger;
import com.weather.client.ai.HistoryManager;
import com.weather.client.util.AutoCompletePopup;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class MainController {
    
    @FXML private TextField cityInput;
    @FXML private Button searchButton;
    @FXML private ComboBox<String> activityComboBox;
    @FXML private Label cityLabel;
    @FXML private Label tempLabel;
    @FXML private Label feelsLikeLabel;
    @FXML private Label humidityLabel;
    @FXML private Label pressureLabel;
    @FXML private Label windLabel;
    @FXML private Label descriptionLabel;
    @FXML private ImageView weatherIcon;
    @FXML private Label recommendationLabel;
    @FXML private HBox forecastContainer;
    @FXML private Label forecastStatusLabel;
    
    private final ApiService apiService = new ApiService();
    private final DatabaseService dbService = DatabaseService.getInstance();
    private final WeatherAI weatherAI = new WeatherAI();
    private final WeatherLogger logger = WeatherLogger.getInstance();
    private final HistoryManager historyManager = new HistoryManager();
    private final AutoCompletePopup<CitySuggestion> autoCompletePopup = new AutoCompletePopup<>();
    
    @FXML
    public void initialize() {
        System.out.println("🌤️ Погодный Ассистент запущен");
        logger.logInfo("APP", "Приложение запущено");
        
        // 1. Пробуем загрузить последний город из БД
        loadLastCity();
        
        if (activityComboBox != null) {
            activityComboBox.setItems(FXCollections.observableArrayList(
                "🎣 Рыбалка", 
                "🏃 Бег", 
                "🌳 Пикник", 
                "🚴 Велосипед", 
                "👢 Поход"
            ));
            activityComboBox.setValue("🎣 Рыбалка");
            System.out.println("ComboBox заполнен");
            logger.logInfo("UI", "ComboBox активностей заполнен");
        } else {
            System.err.println("ОШИБКА: activityComboBox не найден!");
            logger.logError("UI", "ComboBox активностей не найден в FXML");
        }
        
        searchButton.setOnAction(event -> searchWeather());
        cityInput.setOnAction(event -> searchWeather());
        
        setupAutocomplete();
        
        setupAutoCompletePopup();
        
        activityComboBox.setOnAction(event -> {
            String selectedActivity = activityComboBox.getValue();
            System.out.println("Выбрана активность: " + selectedActivity);
            logger.logInfo("UI", "Пользователь выбрал активность: " + selectedActivity);
            
            if (cityInput.getText() != null && !cityInput.getText().isEmpty()) {
                WeatherData currentWeather = extractCurrentWeatherFromUI();
                if (currentWeather != null) {
                    generateAIRecommendation(currentWeather);
                }
            }
        });
        
        if (forecastContainer != null) {
            forecastContainer.getChildren().clear();
        }
        
        animateWelcome();
    }
    
    private void loadLastCity() {
        String lastCity = dbService.getLastCity();
        if (lastCity != null && !lastCity.isEmpty()) {
            cityInput.setText(lastCity);
            System.out.println("Загружен последний город из БД: " + lastCity);
            logger.logInfo("DB", "Загружен последний город из БД: " + lastCity);

        } else {
            System.out.println("В базе данных нет сохраненных городов");
            logger.logInfo("DB", "В базе данных нет сохраненных городов");
        }
    }
    
    private void animateWelcome() {
        cityInput.setPromptText("Начните вводить город...");
        cityInput.requestFocus();
    }
    
    private void setupAutocomplete() {
        cityInput.textProperty().addListener((obs, oldText, newText) -> {
 
            autoCompletePopup.hide();
            
            if (newText.length() >= 2) {

                new Thread(() -> {
                    try {
                        Thread.sleep(300);
                        
                        List<CitySuggestion> suggestions = apiService.getCitySuggestions(newText);
                        
                        javafx.application.Platform.runLater(() -> {
                            if (!suggestions.isEmpty()) {
                                showCitySuggestions(suggestions);
                            }
                        });
                        
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        logger.logError("AUTOCOMPLETE", "Поиск прерван: " + e.getMessage());
                    }
                }).start();
            }
        });
        
        cityInput.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                autoCompletePopup.hide();
            }
        });
    }
    
    private void showCitySuggestions(List<CitySuggestion> suggestions) {
        logger.logInfo("AUTOCOMPLETE", "Показаны подсказки: " + suggestions.size() + " городов");
        
        System.out.println("\n=== ПОДСКАЗКИ ГОРОДОВ (" + suggestions.size() + ") ===");
        for (int i = 0; i < suggestions.size(); i++) {
            CitySuggestion city = suggestions.get(i);
            System.out.println((i+1) + ". " + city.getDisplayName());
        }
        
        autoCompletePopup.show(cityInput, suggestions);
        
        if (suggestions.size() == 1) {
            CitySuggestion city = suggestions.get(0);
            String currentText = cityInput.getText();
            
            if (!currentText.equalsIgnoreCase(city.getName())) {
                javafx.application.Platform.runLater(() -> {
                    cityInput.setText(city.getName());
                    cityInput.positionCaret(city.getName().length());
                    logger.logInfo("AUTOCOMPLETE", "Автозаполнение: " + city.getName());
                });
            }
        }
    }
    
    private void setupAutoCompletePopup() {
        autoCompletePopup.getListView().setOnMouseClicked(e -> {
            CitySuggestion selected = autoCompletePopup.getListView().getSelectionModel().getSelectedItem();
            if (selected != null) {
                cityInput.setText(selected.getName());
                autoCompletePopup.hide();
                cityInput.requestFocus();
                cityInput.positionCaret(cityInput.getText().length());
                logger.logInfo("AUTOCOMPLETE", "Выбран город: " + selected.getName());
            }
        });
    }
    
    private void searchWeather() {
        String city = cityInput.getText().trim();
        
        if (city.isEmpty()) {
            logger.logWarning("SEARCH", "Пустой запрос города");
            showAlert("Ошибка", "Введите название города", Alert.AlertType.WARNING);
            return;
        }
        
        logger.logInfo("SEARCH", "Поиск погоды для города: " + city);
        
        searchButton.setDisable(true);
        searchButton.setText("⌛ Загрузка...");
        recommendationLabel.setText("⏳ Получаем данные о погоде...");
        forecastStatusLabel.setText("⏳ Загружаем прогноз...");
        
        if (forecastContainer != null) {
            forecastContainer.getChildren().clear();
        }
        
        cityLabel.setText("📍 " + city);
        tempLabel.setText("--°C");
        
        new Thread(() -> {
            try {
                WeatherData weather = apiService.getCurrentWeather(city);
                
                List<ForecastData> forecast = apiService.get5DayForecast(city);
                
                javafx.application.Platform.runLater(() -> {
                    if (weather != null) {
 
                        logger.logInfo("SEARCH", "Погода успешно получена для: " + city);
                        
                        updateWeatherUI(weather);
                        
                        String selected = activityComboBox.getValue();
                        String activity = extractActivityName(selected);
                        
                        Recommendation aiRec = weatherAI.analyzeWithMyData(activity, weather);
                        
                        historyManager.saveFromMainController(activity, weather, aiRec);
                        
                        historyManager.printHistory();
                        
                        dbService.saveCity(city);
                        System.out.println("Город сохранен в БД: " + city);
                        logger.logInfo("DB", "Город сохранен в БД: " + city);

                        
                        if (forecast != null && !forecast.isEmpty()) {
  
                            int daysToShow = Math.min(forecast.size(), 5);
                            List<ForecastData> forecastToShow = forecast.subList(0, daysToShow);
                            displayForecast(forecastToShow);
                            
                            System.out.println("Показываем " + daysToShow + " дня прогноза");
                            logger.logInfo("FORECAST", "Прогноз загружен на " + daysToShow + " дня");
                        } else {
                            forecastStatusLabel.setText("Прогноз недоступен");
                            logger.logWarning("FORECAST", "Нет данных прогноза");
                        }
                        
                        showSuccessAlert("Погода успешно загружена для " + city);
                    } else {
                        logger.logError("SEARCH", "Не удалось получить погоду для: " + city);
                        showErrorUI();
                        showAlert("Ошибка", 
                                "Не удалось получить погоду для города: " + city + 
                                "\nПроверьте подключение к серверу или название города.", 
                                Alert.AlertType.ERROR);
                    }
                    
                    searchButton.setDisable(false);
                    searchButton.setText("🔍 Поиск");
                });
                
            } catch (Exception e) {
                logger.logError("SEARCH", "Ошибка при запросе погоды: " + e.getMessage());
                e.printStackTrace();
                
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
    
    private String extractActivityName(String selected) {
        if (selected == null) return "рыбалка";
        
        String activity = selected.replace("🎣", "")
                                  .replace("🏃", "")
                                  .replace("🧺", "")
                                  .replace("🚴", "")
                                  .replace("🥾", "")
                                  .trim()
                                  .toLowerCase();
        
        if (activity.isEmpty()) {
            return "рыбалка";
        }
        
        return activity;
    }
    
    private void updateWeatherUI(WeatherData weather) {
        try {
            String formattedCity = "📍 " + weather.getCity();
            if (weather.getCity().length() > 15) {
                formattedCity = "📍 " + weather.getCity().substring(0, 15) + "...";
            }
            cityLabel.setText(formattedCity);
            
            double temp = weather.getTemperature();
            tempLabel.setText(String.format("%.0f°C", temp));
            
            if (temp < 0) {
                tempLabel.setStyle("-fx-text-fill: #29B6F6;"); 
            } else if (temp < 10) {
                tempLabel.setStyle("-fx-text-fill: #42A5F5;"); 
            } else if (temp < 20) {
                tempLabel.setStyle("-fx-text-fill: #2196F3;"); 
            } else if (temp < 30) {
                tempLabel.setStyle("-fx-text-fill: #FF9800;");
            } else {
                tempLabel.setStyle("-fx-text-fill: #F44336;"); 
            }
            
            feelsLikeLabel.setText(String.format("%.0f°C", weather.getFeelsLike()));
            humidityLabel.setText(String.format("%.0f%%", weather.getHumidity()));
            pressureLabel.setText(String.format("%.0f hPa", weather.getPressure()));
            windLabel.setText(String.format("%.1f м/с", weather.getWindSpeed()));
            
            String description = weather.getDescription();
            String emoji = getWeatherEmoji(description);
            descriptionLabel.setText(emoji + " " + capitalizeFirstLetter(description));
            
            if (weather.getIcon() != null) {
                String iconUrl = "https://openweathermap.org/img/wn/" + weather.getIcon() + "@2x.png";
                Image image = new Image(iconUrl, 100, 100, true, true);
                weatherIcon.setImage(image);
                
                image.errorProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal) {
                        weatherIcon.setImage(null);
                    }
                });
            }
            
            generateAIRecommendation(weather);
            
            logger.logInfo("UI", "Интерфейс обновлен для города: " + weather.getCity());
            
        } catch (Exception e) {
            logger.logError("UI", "Ошибка обновления UI: " + e.getMessage());
            System.err.println("Ошибка обновления UI: " + e.getMessage());
        }
    }
    
    private WeatherData extractCurrentWeatherFromUI() {
        try {
            if (cityLabel.getText().equals("Ошибка")) {
                return null;
            }
            
            WeatherData weather = new WeatherData();
            weather.setCity(cityLabel.getText().replace("📍 ", ""));
            
            String tempText = tempLabel.getText().replace("°C", "").trim();
            weather.setTemperature(Double.parseDouble(tempText));
            
            String feelsText = feelsLikeLabel.getText().replace("°C", "").trim();
            weather.setFeelsLike(Double.parseDouble(feelsText));
            
            String humidityText = humidityLabel.getText().replace("%", "").trim();
            weather.setHumidity(Double.parseDouble(humidityText));
            
            String pressureText = pressureLabel.getText().replace("hPa", "").trim();
            weather.setPressure(Double.parseDouble(pressureText));
            
            String windText = windLabel.getText().replace("м/с", "").trim();
            weather.setWindSpeed(Double.parseDouble(windText));
            
            weather.setDescription(descriptionLabel.getText());
            
            return weather;
            
        } catch (Exception e) {
            System.err.println("Не удалось извлечь данные из UI: " + e.getMessage());
            return null;
        }
    }
    
    private void generateAIRecommendation(WeatherData weather) {
        try {
            String selected = activityComboBox.getValue();
            String activity = extractActivityName(selected);
            
            logger.logInfo("AI", "Анализ погоды для активности: " + activity);
            Recommendation aiRec = weatherAI.analyzeWithMyData(activity, weather);
            
            StringBuilder result = new StringBuilder();
            result.append("🤖 AI РЕКОМЕНДАЦИЯ\n");
            result.append("Активность: ").append(selected).append("\n");
            result.append("✅ ").append(aiRec.getAdvice()).append("\n");
            result.append("📊 Оценка: ").append(aiRec.getScore()).append("/100\n");
            result.append("📝 Детали:\n").append(aiRec.getDetails());
            
            recommendationLabel.setText(result.toString());
            
            logger.logInfo("AI", "Рекомендация готова. Оценка: " + aiRec.getScore() + "/100");
            
        } catch (Exception e) {
            logger.logError("AI", "Ошибка AI анализа: " + e.getMessage());
            System.err.println("Ошибка AI: " + e.getMessage());

            recommendationLabel.setText("🤖 AI временно недоступен\n\n" +
                                   "Проверьте подключение к интернету\n" +
                                   "и повторите попытку позже.");
        }
    }
    
    
    private void displayForecast(List<ForecastData> forecastList) {
        if (forecastContainer == null) {
            logger.logError("FORECAST", "Контейнер прогноза не найден в FXML");
            System.out.println("Ошибка: forecastContainer не найден в FXML");
            return;
        }
        
        forecastContainer.getChildren().clear();
        
        if (forecastList == null || forecastList.isEmpty()) {
            forecastStatusLabel.setText("Нет данных прогноза");
            logger.logWarning("FORECAST", "Пустой список прогноза");
            return;
        }
        
        for (int i = 0; i < forecastList.size(); i++) {
            ForecastData forecast = forecastList.get(i);
            VBox dayCard = createForecastCard(forecast, i);
            forecastContainer.getChildren().add(dayCard);
        }
        
        forecastStatusLabel.setText("Прогноз на " + forecastList.size() + " дня");
        logger.logInfo("FORECAST", "Прогноз отображен на " + forecastList.size() + " дня");
    }
    
    private VBox createForecastCard(ForecastData forecast, int dayIndex) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: #F5FBFF; -fx-background-radius: 10; " +
              "-fx-border-color: #BBDEFB; -fx-border-radius: 10; -fx-border-width: 1; " +
              "-fx-padding: 15; -fx-alignment: center; -fx-pref-width: 110;");
        
        String[] dayNames = {"Сегодня", "Завтра", "Послезавтра", "Через 2 дня", "Через 3 дня"};
        String dayName = dayIndex < dayNames.length ? dayNames[dayIndex] : "День " + (dayIndex + 1);
        
        Label dayLabel = new Label(dayName);
        dayLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #666; -fx-font-size: 14;");
        
        double temp = forecast.getTempAvg();
        Label tempLabel = new Label(String.format("%.0f°C", temp));
        
        String tempColor;
        if (temp < -10) tempColor = "#29B6F6";
        else if (temp < 0) tempColor = "#42A5F5";
        else if (temp < 10) tempColor = "#2196F3";
        else if (temp < 20) tempColor = "#4CAF50";
        else if (temp < 30) tempColor = "#FF9800"; 
        else tempColor = "#F44336"; 
        
        tempLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: " + tempColor + ";");
        
        ImageView iconView = new ImageView();
        iconView.setFitWidth(40);
        iconView.setFitHeight(40);
        
        if (forecast.getIcon() != null && !forecast.getIcon().isEmpty()) {
            try {
                String iconUrl = "https://openweathermap.org/img/wn/" + forecast.getIcon() + "@2x.png";
                logger.logInfo("ICON", "Загрузка иконки прогноза: " + iconUrl);
                System.out.println("Загрузка иконки прогноза: " + iconUrl);
                Image icon = new Image(iconUrl, 40, 40, true, true);
                iconView.setImage(icon);
            } catch (Exception e) {
                logger.logError("ICON", "Не удалось загрузить иконку прогноза: " + e.getMessage());
                System.out.println("Не удалось загрузить иконку прогноза: " + e.getMessage());
            }
        }
        
        String description = forecast.getDescription();
        if (description != null && description.length() > 15) {
            description = description.substring(0, 15) + "...";
        }
        
        Label descLabel = new Label(description != null ? description : "--");
        descLabel.setStyle("-fx-text-fill: #444; -fx-font-size: 11; -fx-wrap-text: true;");
        descLabel.setMaxWidth(100);
        
        card.getChildren().addAll(dayLabel, tempLabel, iconView, descLabel);
        
        return card;
    }
    
    private String getWeatherEmoji(String description) {
        String desc = description.toLowerCase();
        if (desc.contains("ясн") || desc.contains("солн") || desc.contains("clear")) return "🌞";
        if (desc.contains("облач") || desc.contains("cloud")) return "⛅";
        if (desc.contains("дожд") || desc.contains("rain")) return "💦";
        if (desc.contains("снег") || desc.contains("snow")) return "⛄";
        if (desc.contains("гроз") || desc.contains("thunder")) return "⛈️";
        if (desc.contains("туман") || desc.contains("fog")) return "🌫️";
        return "🌈";
    }
    
    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
    
    private void showErrorUI() {
        cityLabel.setText("Ошибка");
        tempLabel.setText("--°C");
        tempLabel.setStyle("-fx-text-fill: #F44336;");
        feelsLikeLabel.setText("--°C");
        humidityLabel.setText("--%");
        pressureLabel.setText("-- hPa");
        windLabel.setText("-- м/с");
        descriptionLabel.setText("Не удалось получить данные");
        weatherIcon.setImage(null);
        recommendationLabel.setText("Проверьте подключение к серверу и повторите попытку.");
        forecastStatusLabel.setText("Прогноз не загружен");
        
        if (forecastContainer != null) {
            forecastContainer.getChildren().clear();
        }
        
        logger.logError("UI", "Отображение экрана ошибки");
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        
        String logLevel = (type == Alert.AlertType.ERROR) ? "ERROR" : 
                         (type == Alert.AlertType.WARNING) ? "WARN" : "INFO";
        logger.logInfo("ALERT", logLevel + ": " + title + " - " + message);
    }
    
    private void showSuccessAlert(String message) {
        System.out.println("✅ " + message);
        logger.logInfo("SUCCESS", message);
    }
}