package org.example.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.model.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;

@Component
@Slf4j
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

    public String generateToken(Member member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userRole", member.getUserRole());
        claims.put("localeCode", member.getLocaleCode());
        claims.put("memberName",member.getMemberName());
        return doGenerateToken(claims, member.getUserId());
    }

    private String doGenerateToken(Map<String, Object> claims, String userId) {
        final Instant createdDate = Instant.now();
        final Instant expirationDate = createdDate.plusMillis(expirationTimeMillis);

        return Jwts.builder()
                .claims(claims)
                .subject(userId)
                .issuedAt(Date.from(createdDate))
                .expiration(Date.from(expirationDate))
                .signWith(key)
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        try {
            // 토큰을 파싱할 수 있는지 확인하여 유효성 검사
            return !isTokenExpired(token) && getUsernameFromToken(token).equals(username); // 토큰이 만료되지 않았는지 추가 확인
        } catch (ExpiredJwtException e) {
            log.error("## Token Expired : {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}