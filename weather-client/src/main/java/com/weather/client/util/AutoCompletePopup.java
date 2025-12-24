package com.weather.client.util;

import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Popup;
import java.util.List;

public class AutoCompletePopup<T> {
    private final Popup popup;
    private final ListView<T> listView;
    
    public AutoCompletePopup() {
        popup = new Popup();
        listView = new ListView<>();
        listView.setPrefWidth(300);
        listView.setPrefHeight(150);
        listView.setStyle("-fx-background-color: white; -fx-border-color: #ccc;");
        popup.getContent().add(listView);
    }
    
    public void show(TextField textField, List<T> suggestions) {
        if (suggestions.isEmpty()) {
            hide();
            return;
        }
        
        listView.getItems().clear();
        listView.getItems().addAll(suggestions);
        
        double x = textField.localToScreen(textField.getBoundsInLocal()).getMinX();
        double y = textField.localToScreen(textField.getBoundsInLocal()).getMaxY();
        popup.show(textField, x, y);
    }
    
    public void hide() {
        popup.hide();
    }
    
    public boolean isShowing() {
        return popup.isShowing();
    }
    
    public ListView<T> getListView() {
        return listView;
    }
}