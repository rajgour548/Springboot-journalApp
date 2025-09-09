package net.engineeringdigest.journalApp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class BrevoEmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.api.url}")
    private String apiUrl;

    @Value("${brevo.sender.email}")
    private String senderEmail; // must be verified in Brevo

    @Value("${brevo.sender.name}")
    private String senderName;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendEmail(String toEmail, String subject, String content) {
        // Build sender map
        Map<String, Object> sender = new HashMap<>();
        sender.put("name", senderName);
        sender.put("email", senderEmail);

        // Build recipient map
        Map<String, Object> recipient = new HashMap<>();
        recipient.put("email", toEmail);

        // Build request body
        Map<String, Object> body = new HashMap<>();
        body.put("sender", sender);

        List<Map<String, Object>> toList = new ArrayList<>();
        toList.add(recipient);
        body.put("to", toList);

        body.put("subject", subject);
        body.put("textContent", content);

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // Call Brevo API
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("✅ Email sent to " + toEmail);
        } else {
            System.out.println("❌ Email failed: " + response.getBody());
        }
    }
}
