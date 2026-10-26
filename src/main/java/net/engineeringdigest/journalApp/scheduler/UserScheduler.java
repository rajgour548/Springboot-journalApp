
package net.engineeringdigest.journalApp.scheduler;

import net.engineeringdigest.journalApp.Cache.AppCache;
import net.engineeringdigest.journalApp.Enum.Sentiment;
import net.engineeringdigest.journalApp.dto.DashboardStatsDTO;
import net.engineeringdigest.journalApp.dto.WeeklySentimentAnalysisDTO;
import net.engineeringdigest.journalApp.entities.JournalEntry;
import net.engineeringdigest.journalApp.entities.SentimentData;
import net.engineeringdigest.journalApp.entities.User;
import net.engineeringdigest.journalApp.repository.UserRepositoryImpl;
import net.engineeringdigest.journalApp.service.DashboardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserScheduler {

    private static final String SENTIMENT_TOPIC =
            "weekly-sentiment-events";

    @Autowired
    private AppCache appCache;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private KafkaTemplate<String, SentimentData> kafkaTemplate;

    //this is for testing it will run every minute -> @Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "0 0 9 ? * MON")
    public void fetchUsersAndSendSaMail() {

        List<User> users = userRepository.getUserForSA();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        LocalDate today = now.toLocalDate();

        for (User user : users) {

            List<JournalEntry> allEntries = user.getJournalEntries();

            if (allEntries == null || allEntries.isEmpty()) {
                continue;
            }

            // 1. Select entries from the last seven days.
            List<JournalEntry> weeklyEntries = allEntries.stream()
                    .filter(entry -> entry.getDate() != null)
                    .filter(entry ->
                            !entry.getDate().isBefore(sevenDaysAgo)
                                    && !entry.getDate().isAfter(now))
                    .collect(Collectors.toList());

            if (weeklyEntries.isEmpty()) {
                continue;
            }

            // 2. Calculate weekly sentiment distribution.
            Map<String, Integer> sentimentDistribution =
                    new LinkedHashMap<>();

            for (Sentiment sentiment : Sentiment.values()) {
                sentimentDistribution.put(sentiment.name(), 0);
            }

            for (JournalEntry entry : weeklyEntries) {
                Sentiment sentiment = entry.getSentiment();

                if (sentiment != null) {
                    sentimentDistribution.put(
                            sentiment.name(),
                            sentimentDistribution.getOrDefault(
                                    sentiment.name(), 0
                            ) + 1
                    );
                }
            }

            // 3. Find the dominant weekly sentiment.
            String dominantSentiment = null;
            int highestCount = 0;

            for (Map.Entry<String, Integer> entry
                    : sentimentDistribution.entrySet()) {

                if (entry.getValue() > highestCount) {
                    highestCount = entry.getValue();
                    dominantSentiment = entry.getKey();
                }
            }

            // 4. Calculate weekly emotion distribution.
            Map<String, Integer> emotionDistribution =
                    new HashMap<>();

            for (JournalEntry entry : weeklyEntries) {
                if (entry.getEmotions() == null) {
                    continue;
                }

                for (String emotion : entry.getEmotions()) {
                    if (emotion != null && !emotion.trim().isEmpty()) {
                        emotionDistribution.put(
                                emotion,
                                emotionDistribution.getOrDefault(emotion, 0) + 1
                        );
                    }
                }
            }

            // Sort emotions from most frequent to least frequent.
            emotionDistribution = emotionDistribution.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue(
                            Comparator.reverseOrder()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (first, second) -> first,
                            LinkedHashMap::new
                    ));

            // 5. Calculate daily mood trend.
            Map<String, Map<String, Integer>> dailyMoodTrend =
                    new LinkedHashMap<>();

            for (int i = 6; i >= 0; i--) {
                LocalDate day = today.minusDays(i);
                String dayKey = day.toString();

                Map<String, Integer> dailyCounts = new LinkedHashMap<>();

                for (Sentiment sentiment : Sentiment.values()) {
                    dailyCounts.put(sentiment.name(), 0);
                }

                dailyMoodTrend.put(dayKey, dailyCounts);
            }

            for (JournalEntry entry : weeklyEntries) {
                if (entry.getSentiment() == null) {
                    continue;
                }

                String dayKey = entry.getDate().toLocalDate().toString();
                Map<String, Integer> dailyCounts =
                        dailyMoodTrend.get(dayKey);

                if (dailyCounts != null) {
                    String mood = entry.getSentiment().name();

                    dailyCounts.put(
                            mood,
                            dailyCounts.getOrDefault(mood, 0) + 1
                    );
                }
            }

            // 6. Include titles, but not private journal content.
            List<String> journalTitles = weeklyEntries.stream()
                    .map(JournalEntry::getTitle)
                    .filter(title -> title != null && !title.trim().isEmpty())
                    .collect(Collectors.toList());

            WeeklySentimentAnalysisDTO weeklyAnalysis =
                    WeeklySentimentAnalysisDTO.builder()
                            .entriesAnalyzed(weeklyEntries.size())
                            .dominantSentiment(dominantSentiment)
                            .sentimentDistribution(sentimentDistribution)
                            .emotionDistribution(emotionDistribution)
                            .dailyMoodTrend(dailyMoodTrend)
                            .journalTitles(journalTitles)
                            .build();

            // 7. Reuse existing complete-history dashboard calculations.
            DashboardStatsDTO dashboardStats =
                    dashboardService.getDashboardStats(user.getUserName());

            // 8. Create the Kafka event.
            SentimentData event = SentimentData.builder()
                    .actualName(user.getActualName())
                    .email(user.getEmail())
                    .weeklyAnalysis(weeklyAnalysis)
                    .dashboardStats(dashboardStats)
                    .build();

            // 9. Publish asynchronously. No direct-email fallback.
            kafkaTemplate.send(
                    SENTIMENT_TOPIC,
                    event.getEmail(),
                    event
            ).addCallback(
                    new ListenableFutureCallback<
                            SendResult<String, SentimentData>>() {

                        @Override
                        public void onSuccess(
                                SendResult<String, SentimentData> result) {
                            System.out.println(
                                    "Weekly sentiment event published for "
                                            + event.getEmail()
                            );
                        }

                        @Override
                        public void onFailure(Throwable ex) {
                            System.err.println(
                                    "Failed to publish weekly sentiment event for "
                                            + event.getEmail()
                            );
                            ex.printStackTrace();
                        }
                    }
            );
        }
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void clearAppCache() {
        appCache.init();
    }
}
