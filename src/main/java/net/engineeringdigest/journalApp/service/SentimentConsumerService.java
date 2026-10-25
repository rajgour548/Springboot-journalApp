
package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.dto.DashboardStatsDTO;
import net.engineeringdigest.journalApp.dto.WeeklySentimentAnalysisDTO;
import net.engineeringdigest.journalApp.entities.SentimentData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SentimentConsumerService {

    @Autowired
    private BrevoEmailService brevoEmailService;

    @KafkaListener(
            topics = "weekly-sentiment-events",
            groupId = "sentiment-group"
    )
    public void consume(SentimentData sentimentData) {
        sendEmail(sentimentData);
    }

    public void sendEmail(SentimentData data) {

        WeeklySentimentAnalysisDTO weekly = data.getWeeklyAnalysis();
        DashboardStatsDTO dashboard = data.getDashboardStats();

        StringBuilder message = new StringBuilder();

        message.append("Hi ")
                .append(data.getActualName())
                .append(",\n\n");

        message.append("YOUR WEEKLY JOURNAL ANALYSIS\n");
        message.append("===========================\n\n");

        message.append("Entries analyzed: ")
                .append(weekly.getEntriesAnalyzed())
                .append("\n");

        message.append("Dominant sentiment: ")
                .append(weekly.getDominantSentiment() != null
                        ? weekly.getDominantSentiment()
                        : "No sentiment data")
                .append("\n\n");

        message.append("Weekly sentiment distribution:\n");

        for (Map.Entry<String, Integer> entry :
                weekly.getSentimentDistribution().entrySet()) {

            message.append("- ")
                    .append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\n");
        }

        message.append("\nWeekly emotion distribution:\n");

        if (weekly.getEmotionDistribution().isEmpty()) {
            message.append("- No recorded emotions\n");
        } else {
            for (Map.Entry<String, Integer> entry :
                    weekly.getEmotionDistribution().entrySet()) {

                message.append("- ")
                        .append(entry.getKey())
                        .append(": ")
                        .append(entry.getValue())
                        .append("\n");
            }
        }

        message.append("\nDaily mood trend:\n");

        for (Map.Entry<String, Map<String, Integer>> day :
                weekly.getDailyMoodTrend().entrySet()) {

            message.append(day.getKey()).append(": ");

            message.append(
                    day.getValue().toString()
            ).append("\n");
        }

        message.append("\nYOUR JOURNAL DASHBOARD\n");
        message.append("======================\n\n");

        message.append("Total journal entries: ")
                .append(dashboard.getTotalEntries())
                .append("\n");

        message.append("Entries this month: ")
                .append(dashboard.getEntriesThisMonth())
                .append("\n");

        message.append("Overall mood: ")
                .append(dashboard.getOverallMood())
                .append("\n");

        message.append("Current writing streak: ")
                .append(dashboard.getCurrentStreak())
                .append(" days\n");

        message.append("\nComplete-history mood distribution:\n");

        for (Map.Entry<String, Long> entry :
                dashboard.getMoodDistribution().entrySet()) {

            message.append("- ")
                    .append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\n");
        }

        message.append("\nComplete-history frequent emotions:\n");

        for (Map.Entry<String, Long> entry :
                dashboard.getFrequentEmotions().entrySet()) {

            message.append("- ")
                    .append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\n");
        }

        message.append("\nKeep writing and reflecting!\n")
                .append("Team JournalApp");

        String subject = "Your Weekly Journal & Sentiment Report";

        brevoEmailService.sendEmail(
                data.getEmail(),
                subject,
                message.toString()
        );
    }
}
