package com.ssginc.showping.controller;

import com.ssginc.showping.entity.Member;
import com.ssginc.showping.jwt.JwtUtil;
import com.ssginc.showping.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MemberController {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Autowired
    public MemberController(JwtUtil jwtUtil, MemberRepository memberRepository) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
    }
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
    @PostMapping("/logout3")
    public String logout() {
        // 로그아웃 처리 로직 (세션/토큰 삭제 등)
        return "redirect:/member/login";  // 로그인 페이지로 리다이렉트
    }

    @PostMapping("/login/authenticate")
    public String authenticate(String memberId, String password, RedirectAttributes redirectAttributes) {
        Member member = memberRepository.findByMemberId(memberId).orElse(null);

        if (member == null || !member.getMemberPassword().equals(password)) {
            System.out.println("로그인 실패: 아이디 또는 비밀번호 오류");
            redirectAttributes.addFlashAttribute("message", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "redirect:/login";  // 로그인 실패 시 다시 로그인 페이지로 이동
        }

        String role = member.getMemberRole().name();
        String token = jwtUtil.generateAccessToken(memberId, role);

        System.out.println("로그인 성공! 토큰: " + token);
        redirectAttributes.addFlashAttribute("message", "로그인 성공!");
        redirectAttributes.addFlashAttribute("token", token);

        return "redirect:/login";  // 로그인 성공 후 리다이렉트
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
