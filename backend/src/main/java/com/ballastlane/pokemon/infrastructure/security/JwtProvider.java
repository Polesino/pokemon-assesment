package com.ballastlane.pokemon.infrastructure.security;

import com.ballastlane.pokemon.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider implements JwtTokenProvider {
    private final SecretKey secretKey;

    public JwtProvider(
            @Value("${security.jwt.secret:ballast-lane-pokemon-assessment-development-secret-key}") String secret
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.username())
                .claim("role", user.role())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            claimsFrom(token);
            return true;
        } catch (RuntimeException exception) {
            return false;
        }
    }

    public String getUsername(String token) {
        return claimsFrom(token).getSubject();
    }

    public String getRole(String token) {
        return claimsFrom(token).get("role", String.class);
    }

    private Claims claimsFrom(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
