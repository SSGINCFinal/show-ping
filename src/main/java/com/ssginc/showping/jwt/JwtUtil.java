package com.ssginc.showping.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final Key key;
    private final long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

//    // **JWT 생성 (Role 포함)**
//    public String generateToken(String memberId, String role) {
//        return Jwts.builder()
//                .setSubject(memberId)
//                .addClaims(Map.of("role",role))
//                .setExpiration(new Date(System.currentTimeMillis() + expiration))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//    }

        // **JWT 생성 (Role 포함)**
    public String generateToken(String memberId, String role) {
        return Jwts.builder()
                .setSubject(memberId) // 사용자 ID 저장
                .claim("role", role)  // 권한(role) 추가
                .setIssuedAt(new Date()) // 토큰 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256) // 서명
                .compact();
    }

    // **JWT 검증 및 파싱**
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getRoleFromToken(String token) {
        String role = (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
        return role;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
