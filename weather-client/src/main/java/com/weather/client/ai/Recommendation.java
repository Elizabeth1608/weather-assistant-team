package com.weather.client.ai;

public class Recommendation {
    private String advice;
    private int score;
    private String details;
    
    public Recommendation(String advice, int score, String details) {
        this.advice = advice;
        this.score = score;
        this.details = details;
    }
    
    public String getAdvice() { return advice; }
    public int getScore() { return score; }
    public String getDetails() { return details; }
    
    public void setAdvice(String advice) { this.advice = advice; }
    public void setScore(int score) { this.score = score; }
    public void setDetails(String details) { this.details = details; }
}