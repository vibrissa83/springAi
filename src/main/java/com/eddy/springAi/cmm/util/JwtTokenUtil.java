package com.eddy.springAi.cmm.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenUtil {

    private final Key key;
    private final long expirationTime;

    public JwtTokenUtil(
            @Value("${app.jwt.secret-key}") String secretKey,
            @Value("${app.jwt.expiration-time}") String expirationTime) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.expirationTime = parseExpirationTime(expirationTime);
    }

    // 토큰 생성
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 사용자명 추출
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // 유효성 검사
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 내부적으로 Claims 반환
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // "30m" 같은 시간 문자열을 밀리초로 변환
    private long parseExpirationTime(String time) {
        if (time.endsWith("m")) {
            return Long.parseLong(time.replace("m", "")) * 1000 * 60;
        } else if (time.endsWith("s")) {
            return Long.parseLong(time.replace("s", "")) * 1000;
        } else {
            throw new IllegalArgumentException("Invalid expiration time format. Use 'm' for minutes or 's' for seconds.");
        }
    }

}
