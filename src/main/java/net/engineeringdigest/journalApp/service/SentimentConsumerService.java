package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.entities.SentimentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SentimentConsumerService {
    @Autowired
    private BrevoEmailService brevoEmailService ;

    @KafkaListener(topics = "weekly-sentiments", groupId = "sentiment-group")
    public void consume(SentimentData sentimentData) {
        sendEmail(sentimentData);
    }

    public void sendEmail(SentimentData sentimentData) {
        brevoEmailService.sendEmail(sentimentData.getEmail(), "Dear "+sentimentData.getActualName()+",This notfication is from JournalApp", sentimentData.getSentiment());
    }
}