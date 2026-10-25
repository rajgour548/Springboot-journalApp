
package net.engineeringdigest.journalApp.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Locale;

@Service
public class VerificationService {

    private static final long CODE_TTL_SECONDS = 300L;
    private static final long RESEND_COOLDOWN_SECONDS = 60L;
    private static final int MAX_ATTEMPTS = 5;

    private static final String CODE_PREFIX = "verify_code_";
    private static final String ATTEMPTS_PREFIX = "verify_attempts_";
    private static final String COOLDOWN_PREFIX = "verify_cooldown_";

    private final RedisService redisService;
    private final BrevoEmailService brevoEmailService;
    private final SecureRandom secureRandom = new SecureRandom();

    public VerificationService(
            RedisService redisService,
            BrevoEmailService brevoEmailService) {
        this.redisService = redisService;
        this.brevoEmailService = brevoEmailService;
    }

    public void generateAndSendCode(String email) {
        String normalizedEmail = normalizeEmail(email);

        String codeKey = CODE_PREFIX + normalizedEmail;
        String attemptsKey = ATTEMPTS_PREFIX + normalizedEmail;
        String cooldownKey = COOLDOWN_PREFIX + normalizedEmail;

        // Only one code-generation request per email per cooldown.
        boolean reserved = redisService.setIfAbsent(
                cooldownKey, "1", RESEND_COOLDOWN_SECONDS);

        if (!reserved) {
            throw new IllegalStateException(
                    "Please wait before requesting another verification code.");
        }

        String code = String.valueOf(
                100000 + secureRandom.nextInt(900000));

        try {
            // Replace any previous code and reset previous attempts.
            redisService.delete(attemptsKey);
            redisService.set(codeKey, code, CODE_TTL_SECONDS);

            brevoEmailService.sendEmail(
                    normalizedEmail,
                    "Your Verification Code",
                    "Your verification code is: " + code);

        } catch (Exception e) {
            // Clean up if storing or sending the code fails.
            redisService.delete(codeKey);
            redisService.delete(attemptsKey);
            redisService.delete(cooldownKey);

            throw new IllegalStateException(
                    "Could not send verification code", e);
        }
    }

    public boolean verifyCode(String email, String code) {
        if (email == null || code == null
                || !code.matches("\\d{6}")) {
            return false;
        }

        String normalizedEmail = normalizeEmail(email);

        long result = redisService.verifyAndConsumeCode(
                CODE_PREFIX + normalizedEmail,
                ATTEMPTS_PREFIX + normalizedEmail,
                code,
                MAX_ATTEMPTS,
                CODE_TTL_SECONDS);

        // Only result 1 means verification succeeded.
        return result == 1;
    }

    private String normalizeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}
