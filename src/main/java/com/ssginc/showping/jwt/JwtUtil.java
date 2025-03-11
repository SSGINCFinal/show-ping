package com.ssginc.showping.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

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

    /**
     * ✅ Access Token 생성
     */
    public String generateAccessToken(String memberId, String role) {
        return Jwts.builder()
                .setSubject(memberId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * ✅ Refresh Token 생성
     */
    public String generateRefreshToken(String memberId) {
        return Jwts.builder()
                .setSubject(memberId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * ✅ JWT에서 사용자 ID 추출
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * ✅ JWT에서 역할(Role) 추출
     */
    public String getRoleFromToken(String token) {
        return (String) parseClaims(token).get("role");
    }

    /**
     * ✅ 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("❌ JWT 만료됨");
        } catch (MalformedJwtException e) {
            System.out.println("❌ 잘못된 JWT 형식");
        } catch (SignatureException e) {
            System.out.println("❌ JWT 서명 검증 실패");
        } catch (Exception e) {
            System.out.println("❌ JWT 검증 실패");
        }
        return false;
    }

    /**
     * ✅ JWT 만료 여부 확인
     */
    public boolean isTokenExpired(String token) {
        try {
            return parseClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true; // 이미 만료됨
        }
    }

    /**
     * ✅ JWT Claims 파싱
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
