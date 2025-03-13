package com.ssginc.showping.controller;

import com.ssginc.showping.entity.Member;
import com.ssginc.showping.jwt.JwtUtil;
import com.ssginc.showping.repository.MemberRepository;
import com.ssginc.showping.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    /**
     * ✅ 관리자 TOTP 설정 정보 제공 (Secret Key 반환)
     */
    @GetMapping("/totp-setup/{adminId}")
    public ResponseEntity<Map<String, String>> getTotpSetup(@PathVariable String adminId) {
        Member admin = memberRepository.findByMemberId(adminId).orElse(null);
        if (admin == null || admin.getOtpSecretKey() == null) {
            return ResponseEntity.status(400).body(Map.of("status", "ERROR", "message", "Admin not found or TOTP not set"));
        }

        // ✅ Secret Key를 반환하여 사용자가 직접 입력 가능하도록 설정
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "secretKey", admin.getOtpSecretKey() // ✅ QR 코드 대신 Secret Key 반환
        ));
    }

    /**
     * ✅ (1) 로그인 처리 (관리자인 경우 2FA 진행)
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        String adminId = request.get("adminId");
        String password = request.get("password");

        if (adminId == null || password == null) {
            return ResponseEntity.status(400).body(Map.of("status", "BAD_REQUEST", "message", "Missing required parameters"));
        }

        // ✅ 기존 MemberService의 로그인 기능 활용
        ResponseEntity<?> loginResponse = memberService.login(new Member(adminId, password), null);

        if (loginResponse.getStatusCode().is2xxSuccessful()) {
            Map<String, String> responseBody = (Map<String, String>) loginResponse.getBody();
            if ("2FA_REQUIRED".equals(responseBody.get("status"))) {
                return ResponseEntity.ok(Map.of(
                        "status", "2FA_REQUIRED",
                        "accessToken", responseBody.get("accessToken"),
                        "refreshToken", responseBody.get("refreshToken")
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "status", "LOGIN_SUCCESS",
                        "accessToken", responseBody.get("accessToken"),
                        "refreshToken", responseBody.get("refreshToken")
                ));
            }
        } else {
            return ResponseEntity.status(401).body(Map.of("status", "LOGIN_FAILED"));
        }
    }

    /**
     * ✅ (2) 2FA TOTP 입력 후 인증 (MemberService의 verifyTOTP 사용)
     */
    @PostMapping("/verify-totp")
    public ResponseEntity<Map<String, String>> verifyTotp(@RequestBody Map<String, String> request) {
        String adminId = request.get("adminId");
        int totpCode = Integer.parseInt(request.get("totpCode"));

        ResponseEntity<Map<String, String>> response = memberService.verifyTOTP(adminId, totpCode);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("✅ 2FA 검증 성공!");

            // ✅ Access Token 발급 (관리자 또는 일반 사용자 모두 발급)
            String accessToken = jwtUtil.generateAccessToken(adminId, "ROLE_ADMIN"); // 관리자라면 ROLE_ADMIN
            String refreshToken = jwtUtil.generateRefreshToken(adminId);

            // ✅ 기존 응답 + Access Token 추가
            Map<String, String> responseBody = new HashMap<>(response.getBody());
            responseBody.put("accessToken", accessToken);
            responseBody.put("refreshToken", refreshToken);

            return ResponseEntity.ok(responseBody);
        } else {
            return response;
        }
    }
}
