
package net.engineeringdigest.journalApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DashboardStatsDTO {

    private long totalEntries;
    private long entriesThisMonth;
    private String overallMood;
    private Map<String, Long> moodDistribution;
    private int currentStreak;
    private Map<String, Long> frequentEmotions;

}
