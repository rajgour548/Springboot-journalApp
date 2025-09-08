package net.engineeringdigest.journalApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RedisService redisService;

    public String generateAndSendCode(String email) {
        String code = String.valueOf((int)(Math.random()*900000) + 100000); // 6-digit
        redisService.set("verify_code_" + email, code, 300L); // valid 5 min

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your Verification Code");
        message.setText("Your verification code is: " + code);
        mailSender.send(message);

        return code;
    }

    public boolean verifyCode(String email, String code) {
        String stored = redisService.get("verify_code_" + email, String.class);
        return stored != null && stored.equals(code);
    }
}
