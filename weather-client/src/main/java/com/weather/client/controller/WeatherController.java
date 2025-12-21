package com.weather.client.controller;

import com.weather.client.service.ApiService;
import com.weather.client.model.WeatherData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class WeatherController {
    @FXML private ComboBox<String> activityComboBox;
    @FXML private Slider maxWindSlider;
    @FXML private Slider minTempSlider;
    @FXML private Slider maxTempSlider;
    @FXML private Label windValueLabel;
    @FXML private Label minTempValueLabel;
    @FXML private Label maxTempValueLabel;
    
    private ApiService apiService = new ApiService();
    
    @FXML
    public void initialize() {
        ObservableList<String> activities = FXCollections.observableArrayList(
            "Рыбалка", "Бег", "Велосипед", "Пикник", "Фотография", "Поход"
        );
        activityComboBox.setItems(activities);
        activityComboBox.setValue("Рыбалка");
        
        maxWindSlider.setMin(0);
        maxWindSlider.setMax(20);
        maxWindSlider.setValue(10);
        maxWindSlider.setShowTickLabels(true);
        maxWindSlider.setShowTickMarks(true);
        maxWindSlider.setMajorTickUnit(5);
        maxWindSlider.setMinorTickCount(1);
        
        minTempSlider.setMin(-20);
        minTempSlider.setMax(30);
        minTempSlider.setValue(5);
        
        maxTempSlider.setMin(0);
        maxTempSlider.setMax(40);
        maxTempSlider.setValue(25);
        
        maxWindSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            windValueLabel.setText(String.format("%.0f м/с", newVal)));
        
        minTempSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            minTempValueLabel.setText(String.format("%.0f°C", newVal)));
        
        maxTempSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            maxTempValueLabel.setText(String.format("%.0f°C", newVal)));
        
        windValueLabel.setText(String.format("%.0f м/с", maxWindSlider.getValue()));
        minTempValueLabel.setText(String.format("%.0f°C", minTempSlider.getValue()));
        maxTempValueLabel.setText(String.format("%.0f°C", maxTempSlider.getValue()));
    }
    
    @FXML
    private void updatePreferences() {
        String activity = activityComboBox.getValue();
        double maxWind = maxWindSlider.getValue();
        double minTemp = minTempSlider.getValue();
        double maxTemp = maxTempSlider.getValue();
        
        System.out.println("Настройки обновлены:");
        System.out.println("Активность: " + activity);
        System.out.println("Макс. ветер: " + maxWind + " м/с");
        System.out.println("Мин. темп: " + minTemp + "°C");
        System.out.println("Макс. темп: " + maxTemp + "°C");
    
    }
}