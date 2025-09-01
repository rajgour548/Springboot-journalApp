package net.engineeringdigest.journalApp.scheduler;

import net.engineeringdigest.journalApp.Cache.AppCache;
import net.engineeringdigest.journalApp.Enum.Sentiment;
import net.engineeringdigest.journalApp.entities.JournalEntry;
import net.engineeringdigest.journalApp.entities.SentimentData;
import net.engineeringdigest.journalApp.entities.User;
import net.engineeringdigest.journalApp.repository.UserRepositoryImpl;
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
    private EmailService emailService;

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

       if(mostFrequentSentiment != null) {
           SentimentData sentimentData = SentimentData.builder().email(user.getEmail()).sentiment("Sentiment for last 7 days" + mostFrequentSentiment).build();
           try {
               kafkaTemplate.send("weekly-sentiments", sentimentData.getEmail(), sentimentData).get();
           } catch (Exception e) {

               emailService.sendEmail(sentimentData.getEmail(), "Sentiment for previous week", sentimentData.getSentiment());
           }


       }


        }
    }

    @Scheduled(cron="0 0/5 * * * ?")
    public void clearAppCache(){
appCache.init();
    }

}
