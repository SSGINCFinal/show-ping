package com.ssginc.showping.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MemberController {
    // 로그인 페이지 요청 처리
    @GetMapping("/login")
    public String login() {
        return "login/login";  // 로그인 화면 반환
    }

    // 회원가입 페이지 요청 처리
    @GetMapping("/login/signup")
    public String signup() {
        return "login/signup";  // 회원가입 화면 반환
    }

    // 로그아웃 처리
    @PostMapping("/logout")
    public String logout() {
        // 로그아웃 처리 로직 (세션/토큰 삭제 등)
        return "redirect:/member/login";  // 로그인 페이지로 리다이렉트
    }

    @GetMapping("/user-info")
    public String getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println(userDetails.getUsername());
        String userId = userDetails != null ? userDetails.getUsername() : "guest";
        System.out.println("로그인한 userId = " + userId);
        return "user/userInfo";
    }
    /*
    @GetMapping("/user-info")
    public String getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return "현재 로그인한 사용자: " + userDetails.getUsername();
        }
        return "로그인하지 않은 사용자입니다.";
    }
     */

}
