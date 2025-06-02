package org.example.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationTimeMillis;

    private SecretKey key;

    @PostConstruct
    public void init() {
        // 비밀 키는 Base64로 인코딩된 문자열을 사용하는 것이 일반적입니다.
        // 예를 들어, Base64.getDecoder().decode(secret) 와 같이 디코딩 후 사용합니다.
        // 현재는 secret.getBytes()를 사용하고 있지만, 더 안전한 키 생성 방법은 Base64 인코딩을 거치는 것입니다.
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public Instant getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration().toInstant();
    }

    private Boolean isTokenExpired(String token) {
        final Instant expiration = getExpirationDateFromToken(token);
        return expiration.isBefore(Instant.now());
    }

    public String generateToken(String userId, String userRole) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userRole", userRole);
        return doGenerateToken(claims, userId);
    }

    private String doGenerateToken(Map<String, Object> claims, String username) {
        final Instant createdDate = Instant.now();
        final Instant expirationDate = createdDate.plusMillis(expirationTimeMillis);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(Date.from(createdDate))
                .setExpiration(Date.from(expirationDate))
                .signWith(key)
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            // 토큰을 파싱할 수 있는지 확인하여 유효성 검사
            getAllClaimsFromToken(token); // 이 호출에서 verifyWith(key)를 통해 서명 검증이 이루어집니다.
            return !isTokenExpired(token); // 토큰이 만료되지 않았는지 추가 확인
        } catch (Exception e) {
            // JWT 관련 예외 발생 시 (예: SignatureException, ExpiredJwtException 등)
            // 토큰이 유효하지 않다고 판단
            return false;
        }
    }
}