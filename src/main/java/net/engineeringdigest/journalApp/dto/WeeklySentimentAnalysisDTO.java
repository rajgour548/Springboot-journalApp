
package net.engineeringdigest.journalApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklySentimentAnalysisDTO {

    private int entriesAnalyzed;

    private String dominantSentiment;

    private Map<String, Integer> sentimentDistribution;

    private Map<String, Integer> emotionDistribution;

    private Map<String, Map<String, Integer>> dailyMoodTrend;

    private List<String> journalTitles;
}
