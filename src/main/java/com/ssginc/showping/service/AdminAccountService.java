package com.ssginc.showping.service;

import com.ssginc.showping.entity.Member;
import com.ssginc.showping.entity.MemberRole;
import com.ssginc.showping.repository.MemberRepository;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import com.warrenstrange.googleauth.IGoogleAuthenticator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminAccountService implements CommandLineRunner {


    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final MailService mailService;

    private final IGoogleAuthenticator googleAuthenticator;

    @Override
    public void run(String... args) { // Spring Boot 실행 시 자동 실행
        Member admin = createAdminAccount();
        if (admin != null) {
            System.out.println("관리자 계정이 성공적으로 생성되었습니다: " + admin.getMemberId());
        } else {
            System.out.println("관리자 계정이 이미 존재합니다. 생성되지 않았습니다.");
        }
    }

    public Member createAdminAccount() {
        String adminId = "ShowPing_Admin";
        String adminEmail = "admin@showping.com";

        if (memberRepository.findByMemberId(adminId).isEmpty()) {

            GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
            String secretKey = key.getKey();

            // 관리자 계정 생성
            Member admin = Member.builder()
                    .memberId(adminId)
                    .memberName("Administrator")
                    .memberPassword(passwordEncoder.encode("showpingzzang"))
                    .memberEmail(adminEmail)
                    .memberRole(MemberRole.ROLE_ADMIN)
                    .memberAddress("Admin Address")
                    .streamKey(UUID.randomUUID().toString()) // 스트림 키 자동 생성
                    .otpSecretKey(secretKey)
                    .build();

            memberRepository.save(admin);
            System.out.println("✅ 관리자 계정 생성 완료!: " + adminId);
            System.out.println(" 관리자 계정 비밀키: " + secretKey);

            // ✅ TOTP 등록 이메일 전송
            sendTOTPRegistrationMail(admin, key);

            return admin;
        } else {
            System.out.println("🔹 관리자 계정 이미 존재!");
            return null;
        }
    }
    private void sendTOTPRegistrationMail(Member admin, GoogleAuthenticatorKey key) {
        String qrCodeUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL("ShowPing", admin.getMemberId(), key);
        mailService.send(admin.getMemberEmail(), "ShowPing 관리자 2FA 등록", "TOTP QR 코드: " + qrCodeUrl);
        System.out.println("📧 관리자 2FA 등록 이메일 전송 완료!");
    }
}
