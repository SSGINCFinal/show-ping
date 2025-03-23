package com.ssginc.showping.service;


import com.ssginc.showping.dto.object.MailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final Map<String, String> emailCodeStorage = new HashMap<>(); // 이메일 인증 코드 저장소

    // 인증 코드 생성 메서드
    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 숫자 생성
        return String.valueOf(code);
    }

    // 인증 코드 이메일 전송
    public String sendVerificationCode(String email) {
        String code = generateCode();
        emailCodeStorage.put(email, code);  // 이메일과 인증 코드 저장

        System.out.println("이메일: " + email);
        System.out.println("생성된 인증 코드: " + code);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("쇼핑몰 회원가입 인증 코드");
            helper.setText("<h3>인증 코드: <strong>" + code + "</strong></h3>", true);

            mailSender.send(message);
            System.out.println("이메일 전송 성공!");
            return "이메일 전송 완료!";
        } catch (MessagingException e) {
            System.out.println("이메일 전송 실패: " + e.getMessage());
            e.printStackTrace();
            return "이메일 전송 실패!";
        }
    }

    // 인증 코드 검증
    public boolean verifyCode(String email, String inputCode) {
        System.out.println("입력된 이메일: " + email);
        System.out.println("입력된 인증 코드: " + inputCode);
        System.out.println("저장된 인증 코드: " + emailCodeStorage.get(email));

        boolean isValid = emailCodeStorage.containsKey(email) && emailCodeStorage.get(email).equals(inputCode);
        System.out.println("인증 결과: " + isValid);

        return isValid;
    }

    // TOTP 인증 코드 전송
    public void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

}
