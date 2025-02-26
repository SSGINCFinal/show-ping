package com.ssginc.showping.jwt;

import io.jsonwebtoken.ExpiredJwtException;
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
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.access.expiration}") long accessTokenExpiration,
                   @Value("${jwt.refresh.expiration}") long refreshTokenExpiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
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

        // Access Token 생성 (30분 만료)
    public String generateAccessToken(String memberId, String role) {

        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("generateAccessToken 실행됨");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        return Jwts.builder()
                .setSubject(memberId) // 사용자 ID 저장
                .claim("role", role)  // 권한(role) 추가
                .setIssuedAt(new Date()) // 토큰 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration)) // Access Token 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256) // 서명
                .compact();

    }

    // ✅ Refresh Token 생성 (7일 만료)
    public String generateRefreshToken(String memberId) {

        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("generateRefreshToken 실행됨");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        return Jwts.builder()
                .setSubject(memberId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration)) // Refresh Token 만료 시간
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // **JWT에서 사용자 ID 추출
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // JWT에서 권한 추출
    public String getRoleFromToken(String token) {
        String role = (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
        return role;
    }

    // JWT 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // JWT 만료 여부 확인
    public boolean idTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true; // 이미 만료됨
        }
    }
}
