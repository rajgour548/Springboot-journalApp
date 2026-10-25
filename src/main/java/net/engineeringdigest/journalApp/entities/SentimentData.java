
package net.engineeringdigest.journalApp.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.engineeringdigest.journalApp.dto.DashboardStatsDTO;
import net.engineeringdigest.journalApp.dto.WeeklySentimentAnalysisDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SentimentData {

    private String actualName;

    private String email;

    private WeeklySentimentAnalysisDTO weeklyAnalysis;

    private DashboardStatsDTO dashboardStats;
}
