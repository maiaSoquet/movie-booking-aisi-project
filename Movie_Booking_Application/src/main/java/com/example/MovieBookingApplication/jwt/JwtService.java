package com.example.MovieBookingApplication.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @PostConstruct
    public void init() {
        validateConfiguration();
    }

    private void validateConfiguration() {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalStateException("JWT secret key must be configured in application.properties");
        }
        if (jwtExpiration == null || jwtExpiration <= 0) {
            throw new IllegalStateException("JWT expiration must be a positive value");
        }
    }

    public String extractUsername(String jwtToken) {
        return extractClaims(jwtToken, Claims::getSubject);
    }

    private <T> T extractClaims(String jwtToken, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwtToken) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JWT: " + e.getMessage(), e);
        }
    }

    public SecretKey getSignInKey() {
        try {
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

            // Validate key length (256 bits = 32 bytes)
            if (keyBytes.length < 32) {
                throw new IllegalArgumentException("JWT secret key must be at least 256 bits (32 characters)");
            }

            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signing key", e);
        }
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
        try {
            final String username = extractUsername(jwtToken);
            return (username != null &&
                    username.equals(userDetails.getUsername()) &&
                    !isTokenExpired(jwtToken));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String jwtToken) {
        try {
            return extractExpiration(jwtToken).before(new Date());
        } catch (Exception e) {
            return true;  // If we can't parse expiration, consider it expired
        }
    }

    private Date extractExpiration(String jwtToken) {
        return extractClaims(jwtToken, Claims::getExpiration);
    }
}