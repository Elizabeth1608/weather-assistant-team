package com.weather.client.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private static final String DB_URL = "jdbc:sqlite:weather.db";
    private static DatabaseService instance;
    
    private DatabaseService() {
        initializeDatabase();
    }
    
    public static synchronized DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }
    
    private void initializeDatabase() {
    String sql = "CREATE TABLE IF NOT EXISTS saved_cities (" +
                 "    name TEXT PRIMARY KEY," +
                 "    last_accessed TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                 ")";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("База данных инициализирована");
        } catch (SQLException e) {
            System.err.println("Ошибка инициализации БД: " + e.getMessage());
        }
    }
    
    public void saveCity(String name) {
        String sql = "INSERT OR REPLACE INTO saved_cities (name) VALUES (?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            
            System.out.println("Город сохранен в БД: " + name);
        } catch (SQLException e) {
            System.err.println("Ошибка сохранения города: " + e.getMessage());
        }
    }
    
    public String getLastCity() {
        String sql = "SELECT name FROM saved_cities ORDER BY last_accessed DESC LIMIT 1";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения города: " + e.getMessage());
        }
        return null;
    }
    
    public List<String> getAllCities() {
        List<String> cities = new ArrayList<>();
        String sql = "SELECT name FROM saved_cities ORDER BY last_accessed DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                cities.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения списка городов: " + e.getMessage());
        }
        return cities;
    }
}