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
    
    public String toString() {
        return advice + "\nОценка: " + score + "/100\n" + details;
    }
}