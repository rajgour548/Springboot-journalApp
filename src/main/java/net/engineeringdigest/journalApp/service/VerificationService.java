package net.engineeringdigest.journalApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private BrevoEmailService brevoEmailService;

    public String generateAndSendCode(String email) {
        String code = String.valueOf((int)(Math.random() * 900000) + 100000); // 6-digit
        redisService.set("verify_code_" + email, code, 300L); // valid 5 min

        String subject = "Your Verification Code";
        String content = "Your verification code is: " + code;

        brevoEmailService.sendEmail(email, subject, content);

        return code;
    }

    public boolean verifyCode(String email, String code) {
        String stored = redisService.get("verify_code_" + email, String.class);
        return stored != null && stored.equals(code);
    }
}
