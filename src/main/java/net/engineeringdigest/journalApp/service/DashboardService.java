
package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.dto.DashboardStatsDTO;
import net.engineeringdigest.journalApp.entities.JournalEntry;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.engineeringdigest.journalApp.Enum.Sentiment;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Set;

@Service
public class DashboardService {

    private final JournalEntryService journalEntryService;

    public DashboardService(JournalEntryService journalEntryService) {
        this.journalEntryService = journalEntryService;
    }

    public DashboardStatsDTO getDashboardStats(String username) {

        List<JournalEntry> entries =
                journalEntryService.getAll(username);

        //calculate total entries and entries this month
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.now();

        long totalEntries = entries.size();

        long entriesThisMonth = entries.stream()
                .filter(entry -> entry.getDate() != null)
                .filter(entry -> YearMonth.from(
                        entry.getDate().toLocalDate()
                ).equals(currentMonth))
                .count();

        //calculate overall mood
        Map<String, Long> moodDistribution = new LinkedHashMap<>();

        for (Sentiment sentiment : Sentiment.values()) {
            moodDistribution.put(sentiment.name(), 0L);
        }

        for (JournalEntry entry : entries) {
            if (entry.getSentiment() != null) {
                String mood = entry.getSentiment().name();
                moodDistribution.put(
                        mood,
                        moodDistribution.get(mood) + 1
                );
            }
        }

        String overallMood = "NO_DATA";
        long highestCount = 0;

        for (Map.Entry<String, Long> mood : moodDistribution.entrySet()) {
            if (mood.getValue() > highestCount) {
                highestCount = mood.getValue();
                overallMood = mood.getKey();
            }
        }

        //calculate frequent emotions
        Map<String, Long> frequentEmotions = new LinkedHashMap<>();

        for (JournalEntry entry : entries) {
            if (entry.getEmotions() == null) {
                continue;
            }

            for (String emotion : entry.getEmotions()) {
                if (emotion != null) {
                    frequentEmotions.put(
                            emotion,
                            frequentEmotions.getOrDefault(emotion, 0L) + 1
                    );
                }
            }
        }

        Map<String, Long> sortedEmotions = new LinkedHashMap<>();

        frequentEmotions.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(
                        Comparator.reverseOrder()))
                .forEach(entry ->
                        sortedEmotions.put(entry.getKey(), entry.getValue()));


        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setTotalEntries(totalEntries);
        stats.setEntriesThisMonth(entriesThisMonth);
        stats.setOverallMood(overallMood);
        stats.setMoodDistribution(moodDistribution);
        stats.setFrequentEmotions(sortedEmotions);
        stats.setCurrentStreak(calculateCurrentStreak(entries));

        return stats;
    }


    private int calculateCurrentStreak(List<JournalEntry> entries) {

        Set<LocalDate> writingDays = new HashSet<>();

        for (JournalEntry entry : entries) {
            if (entry.getDate() != null) {
                writingDays.add(entry.getDate().toLocalDate());
            }
        }

        LocalDate today = LocalDate.now();
        LocalDate streakDate;

        if (writingDays.contains(today)) {
            streakDate = today;
        } else if (writingDays.contains(today.minusDays(1))) {
            streakDate = today.minusDays(1);
        } else {
            return 0;
        }

        int streak = 0;

        while (writingDays.contains(streakDate)) {
            streak++;
            streakDate = streakDate.minusDays(1);
        }

        return streak;
    }

}
