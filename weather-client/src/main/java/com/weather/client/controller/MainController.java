package com.weather.client.controller;

import com.weather.client.model.WeatherData;
import com.weather.client.service.ApiService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
        System.out.println("MainController инициализирован");
        searchButton.setOnAction(event -> searchWeather());
        cityInput.setText("Москва");
    }
    
    private void searchWeather() {
        String city = cityInput.getText().trim();
        if (city.isEmpty()) return;
        
        System.out.println("Поиск погоды для: " + city);
        searchButton.setDisable(true);
        searchButton.setText("Загрузка...");
        
        try {
            WeatherData weather = apiService.getCurrentWeather(city);
            if (weather != null) {
                updateWeatherUI(weather);
            } else {
                showError();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            searchButton.setDisable(false);
            searchButton.setText("Поиск");
        }
    }
    
    private void updateWeatherUI(WeatherData weather) {
        
        cityLabel.setText(weather.getCity());
        tempLabel.setText(String.format("%.1f°C", weather.getTemperature()));
        feelsLikeLabel.setText(String.format("Ощущается: %.1f°C", weather.getFeelsLike()));
        humidityLabel.setText(String.format("Влажность: %.0f%%", weather.getHumidity()));
        pressureLabel.setText(String.format("Давление: %.0f hPa", weather.getPressure()));
        windLabel.setText(String.format("Ветер: %.1f м/с", weather.getWindSpeed()));
        descriptionLabel.setText(weather.getDescription());
        recommendationLabel.setText("Рекомендация: " + getRecommendationText(weather));
        
        if (weather.getIcon() != null) {
            try {
                String iconUrl = "https://openweathermap.org/img/wn/" + weather.getIcon() + "@2x.png";
                Image image = new Image(iconUrl, true);
                weatherIcon.setImage(image);
            } catch (Exception e) {
                System.out.println("Не загрузилась иконка");
            }
        }
    }
    
    private String getRecommendationText(WeatherData weather) {

        StringBuilder rec = new StringBuilder();
        
        if (weather.getTemperature() > 25) rec.append("Жарко, пейте воду. ");
        if (weather.getTemperature() < 5) rec.append("Холодно, одевайтесь теплее. ");
        if (weather.getWindSpeed() > 10) rec.append("Сильный ветер. ");
        if (weather.getDescription().contains("дождь")) rec.append("Возьмите зонт. ");
        
        return rec.length() > 0 ? rec.toString() : "Хорошая погода!";
    }
    
    private void showError() {
        cityLabel.setText("Ошибка");
        tempLabel.setText("--");
        descriptionLabel.setText("Не удалось получить данные");
        recommendationLabel.setText("Проверьте подключение к серверу");
    }
}