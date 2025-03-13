package com.ssginc.showping.controller;

import com.ssginc.showping.entity.Member;
import com.ssginc.showping.repository.MemberRepository;
import com.ssginc.showping.service.AdminAuthService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminAuthService adminAuthService;
    private final MemberRepository memberRepository;


    private static final String ISSUER = "ShowPing";

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

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) { // ✅ @RequestBody 추가
        String adminId = request.get("adminId");
        String password = request.get("password");

        if (adminId == null || password == null) {
            return ResponseEntity.status(400).body(Map.of("status", "BAD_REQUEST", "message", "Missing required parameters"));
        }

        boolean isPasswordValid = adminAuthService.verifyPassword(adminId, password);

        if (isPasswordValid) {
            return ResponseEntity.ok(Map.of("status", "2FA_REQUIRED"));
        } else {
            return ResponseEntity.status(401).body(Map.of("status", "LOGIN_FAILED"));
        }
    }

    // ✅ (2) 2FA TOTP 입력 후 인증
    @PostMapping("/verify-totp")
    public ResponseEntity<Map<String, String>> verifyTotp(@RequestBody Map<String, String> request) {
        String adminId = request.get("adminId");
        int totpCode = Integer.parseInt(request.get("totpCode"));

        boolean isTotpValid = adminAuthService.verifyTOTP(adminId, totpCode);

        if (isTotpValid) {
            return ResponseEntity.ok(Map.of("status", "LOGIN_SUCCESS"));
        } else {
            return ResponseEntity.status(401).body(Map.of("status", "TOTP_FAILED"));
        }
    }
}
