package net.engineeringdigest.journalApp.dto;

import java.util.List;

public class MoodAnalysisDTO {

    private String sentiment;
    private List<String> emotions;

    public MoodAnalysisDTO() {
    }

    public MoodAnalysisDTO(String sentiment, List<String> emotions) {
        this.sentiment = sentiment;
        this.emotions = emotions;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public List<String> getEmotions() {
        return emotions;
    }

    public void setEmotions(List<String> emotions) {
        this.emotions = emotions;
    }
}