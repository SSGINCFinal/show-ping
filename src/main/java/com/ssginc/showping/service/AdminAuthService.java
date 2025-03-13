package com.ssginc.showping.service;

import com.ssginc.showping.entity.Member;
import com.ssginc.showping.jwt.JwtUtil;
import com.ssginc.showping.repository.MemberRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final GoogleAuthenticator googleAuthenticator;
    private final JwtUtil jwtUtil;

    public boolean verifyPassword(String adminId, String rawPassword) {
        Optional<Member> admin = memberRepository.findByMemberId(adminId);
        return admin.isPresent() && passwordEncoder.matches(rawPassword, admin.get().getMemberPassword());
    }

    public boolean verifyTOTP(String adminId, int totpCode) {
        Member admin = memberRepository.findByMemberId(adminId).orElse(null);
        if (admin == null || admin.getOtpSecretKey() == null) {
            return false;
        }
        return googleAuthenticator.authorize(admin.getOtpSecretKey(), totpCode); // ✅ OTP 검증
    }

}
