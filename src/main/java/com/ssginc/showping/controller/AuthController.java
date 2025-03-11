package com.ssginc.showping.controller;

import com.ssginc.showping.entity.Member;
import com.ssginc.showping.service.MemberService;
import com.ssginc.showping.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController  // ✅ JSON 응답을 반환하도록 변경
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    private final RefreshTokenService refreshTokenService;

    /**
     * ✅ 로그인 처리 (Access Token & Refresh Token 반환)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Member member, HttpServletResponse response) {
        return memberService.login(member, response);
    }

    /**
     * ✅ 로그아웃 처리 (Access Token 삭제, Refresh Token 삭제)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        System.out.println("📢 로그아웃 요청 도착!");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String refreshToken = authentication.getName();
            System.out.println("📢 로그아웃 처리 중: " + refreshToken);

            refreshTokenService.deleteRefreshToken(refreshToken); // ✅ Redis에서 Refresh Token 삭제
            memberService.logout(refreshToken, response); // ✅ Access Token 삭제 (쿠키 삭제)

            System.out.println("✅ 로그아웃 완료!");
        } else {
            System.out.println("🚨 로그아웃 실패: 인증된 사용자 없음");
        }

        // ✅ 200 OK 응답 반환 (리다이렉트 없음)
        return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
    }

    /**
     * ✅ 현재 로그인한 사용자 정보 조회
     */
    @GetMapping("/user-info")
    public ResponseEntity<Map<String, String>> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body(Map.of("message", "인증되지 않은 사용자입니다."));
        }

        String username = authentication.getName();
        return ResponseEntity.ok(Map.of("username", username));
    }

    @GetMapping("/refresh-token-check")
    public ResponseEntity<?> checkRefreshToken(@RequestParam String username) {
        String token = refreshTokenService.checkRefreshToken(username);
        if (token != null) {
            return ResponseEntity.ok(Map.of("refreshToken", token));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "저장된 Refresh Token 없음"));
        }
    }
}
