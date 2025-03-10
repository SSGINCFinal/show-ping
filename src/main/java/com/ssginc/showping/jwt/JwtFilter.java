package com.ssginc.showping.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // ✅ 인증이 필요 없는 경로 (예외 처리)
        if (requestURI.equals("/") || requestURI.startsWith("/css/") || requestURI.startsWith("/js/") ||
                requestURI.startsWith("/images/") || requestURI.startsWith("/assets/") ||
                requestURI.equals("/api/auth/login") || requestURI.equals("/api/auth/logout") ||
                requestURI.equals("/login/signup") || requestURI.equals("/api/register")) {
            chain.doFilter(request, response);
            return;
        }

        // ✅ Authorization 헤더에서 JWT 가져오기
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);

                UserDetails userDetails = new User(username, "", List.of(new SimpleGrantedAuthority(role)));
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationFilter().attemptAuthentication(request, response)
                );
            }
        } catch (ExpiredJwtException e) {
            System.out.println("❌ Access Token 만료됨");
        }

        chain.doFilter(request, response);
    }
}
