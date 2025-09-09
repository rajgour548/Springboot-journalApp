package net.engineeringdigest.journalApp.scheduler;

import net.engineeringdigest.journalApp.Cache.AppCache;
import net.engineeringdigest.journalApp.Enum.Sentiment;
import net.engineeringdigest.journalApp.entities.JournalEntry;
import net.engineeringdigest.journalApp.entities.SentimentData;
import net.engineeringdigest.journalApp.entities.User;
import net.engineeringdigest.journalApp.repository.UserRepositoryImpl;
import net.engineeringdigest.journalApp.service.BrevoEmailService;
import net.engineeringdigest.journalApp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserScheduler{

    @Autowired
    private AppCache appCache;

    @Autowired
    private BrevoEmailService brevoEmailService;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private KafkaTemplate<String,SentimentData> kafkaTemplate;



    @Scheduled(cron="0/30 * * * * ?")
    public void fetchUsersAndSendSaMail(){
        List<User> users = userRepository.getUserForSA();
        
        for(User user : users){
            List<JournalEntry> journalEntries = user.getJournalEntries();
            List<Sentiment> sentiments = journalEntries.stream().filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))).map(x->x.getSentiment()).collect(Collectors.toList());
           Map<Sentiment,Integer> sentimentCounts = new HashMap<>();

           for(Sentiment sentiment : sentiments){
               if(sentiment != null){
                   sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment,0)+1);
               }
           }
           Sentiment mostFrequentSentiment = null;
           int maxCount = 0;
           for(Map.Entry<Sentiment,Integer> entry : sentimentCounts.entrySet()){
               if(entry.getValue() > maxCount){
                   maxCount = entry.getValue();
                   mostFrequentSentiment = entry.getKey();
               }
           }

            if (mostFrequentSentiment != null) {
                String message = "";
                String subject = "Sentiment Analysis (" + user.getActualName() + ")";

                if ("positive".equalsIgnoreCase(String.valueOf(mostFrequentSentiment))) {
                    message = String.format(
                            "Hey %s,\n\n" +
                                    "We loved reading your recent entry—it’s full of positive energy! 🎉\n" +
                                    "Keep holding onto that spirit and continue spreading the good vibes.\n" +
                                    "Remember, every step you take with a smile makes your journey even more beautiful.\n\n" +
                                    "Stay awesome,\n" +
                                    "Team Journal",
                            user.getActualName()
                    );
                } else if ("neutral".equalsIgnoreCase(String.valueOf(mostFrequentSentiment))) {
                    message = String.format(
                            "Hey %s,\n\n" +
                                    "We noticed your latest entry had a calm and neutral tone.\n" +
                                    "Life has its ups and downs, and sometimes being steady is a victory in itself.\n" +
                                    "Take this moment to reflect, recharge, and prepare for the brighter days ahead.\n\n" +
                                    "You’re doing great,\n" +
                                    "Team Journal",
                            user.getActualName()
                    );
                } else if ("negative".equalsIgnoreCase(String.valueOf(mostFrequentSentiment))) {
                    message = String.format(
                            "Hey %s,\n\n" +
                                    "Your recent entry reflected some heavy feelings, and that’s okay—it’s natural to experience difficult days.\n" +
                                    "Remember, you’re not alone in this journey. Take small steps, be kind to yourself, and don’t hesitate to lean on friends, family, or support whenever needed.\n" +
                                    "Better days are coming 🌈.\n\n" +
                                    "With care,\n" +
                                    "Team Journal",
                            user.getActualName()
                    );
                } else {
                    // fallback if sentiment is unexpected
                    message = String.format(
                            "Hey %s,\n\n" +
                                    "This notification is from JournalApp as you opted for Sentiment Analysis.\n" +
                                    "Your Overall Mood in the previous week is: %s.\n\n" +
                                    "Keep writing,\n" +
                                    "Team Journal",
                            user.getActualName(), mostFrequentSentiment
                    );
                }

                SentimentData sentimentData = SentimentData.builder()
                        .actualName(user.getActualName())
                        .email(user.getEmail())
                        .sentiment(message)
                        .build();

                try {
                    // Try Kafka first
                    kafkaTemplate.send("weekly-sentiments", sentimentData.getEmail(), sentimentData).get();
                } catch (Exception e) {
                    // Fallback: send email if Kafka fails
                    brevoEmailService.sendEmail(sentimentData.getEmail(), subject, sentimentData.getSentiment());
                }
            }
        }
    }

    @Scheduled(cron="0 0/5 * * * ?")
    public void clearAppCache(){
appCache.init();
    }
}

