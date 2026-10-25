
package net.engineeringdigest.journalApp.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private final SecretKey signingKey;

    public JwtUtils(SecretKey jwtSigningKey) {
        this.signingKey = jwtSigningKey;
    }

    // Existing token generation for normal login
    public String generateToken(Authentication authentication) {

        long now = System.currentTimeMillis();

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring(5))
                .distinct()
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(authentication.getName())
                .claim("roles", roles)
                .issuedAt(new Date(now))
                .expiration(new Date(now + 1000L * 60 * 60))
                .signWith(signingKey)
                .compact();
    }

    // New token generation exclusively for password reset
    public String generatePasswordResetToken(String username) {

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(username)
                .claim("purpose", "password_reset")
                .issuedAt(new Date(now))
                .expiration(new Date(now + 1000L * 60 * 10))
                .signWith(signingKey)
                .compact();
    }

    public String validatePasswordResetToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String purpose = claims.get("purpose", String.class);

            if (!"password_reset".equals(purpose)) {
                return null;
            }

            return claims.getSubject();

        } catch (Exception e) {
            // Invalid signature, expired token, malformed token, etc.
            return null;
        }
    }
}
