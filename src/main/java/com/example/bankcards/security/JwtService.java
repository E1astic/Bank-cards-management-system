package com.example.bankcards.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Duration lifetime;

    private SecretKey getSecretAsKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private Map<String, Object> initClaims(CustomUserDetails userDetails) {
        return new HashMap<>() {{
            put("role", userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("ROLE_USER"));
            put("userId", userDetails.getUserId());
        }};
    }

    public String generateToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = initClaims(userDetails);
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + lifetime.toMillis());
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(issuedDate)
                .expiration(expiredDate)
                .signWith(getSecretAsKey(secret))
                .compact();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretAsKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsernameFromToken(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractRoleFromToken(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public Long extractUserIdFromToken(String token) {
        return extractClaims(token).get("userId", Long.class);
    }
}
