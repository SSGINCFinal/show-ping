package com.ssginc.showping.controller;

import com.ssginc.showping.dto.object.MailDto;
import com.ssginc.showping.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    // 이메일 인증 코드 보내기
    @PostMapping("/send-code")
    public String sendCode(@RequestBody MailDto mailDto) {
        return mailService.sendVerificationCode(mailDto.getEmail());
    }

    // 인증 코드 확인하기
    @PostMapping("/verify-code")
    public boolean verifyCode(@RequestBody MailDto mailDto) {
        return mailService.verifyCode(mailDto.getEmail(), mailDto.getEmailCode());
    }
}
