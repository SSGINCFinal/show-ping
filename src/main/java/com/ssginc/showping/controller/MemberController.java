package com.ssginc.showping.controller;

import com.ssginc.showping.dto.object.MemberDTO;
import com.ssginc.showping.entity.Member;
import com.ssginc.showping.jwt.JwtUtil;
import com.ssginc.showping.repository.MemberRepository;
import com.ssginc.showping.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MemberController {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @Autowired
    public MemberController(JwtUtil jwtUtil, MemberRepository memberRepository, MemberService memberService) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
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
        return memberService.authenticate(memberId, password, redirectAttributes); // ✅ 서비스에서 로그인 처리
    }

    @GetMapping("/user-info")
    public String getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println(userDetails.getUsername());
        String userId = userDetails != null ? userDetails.getUsername() : "guest";
        System.out.println("로그인한 userId = " + userId);
        return "user/userInfo";
    }

    @PostMapping("/register")
    public String registerMember(@RequestBody MemberDTO memberDto, RedirectAttributes redirectAttributes) throws Exception {
        System.out.println(memberDto.toString());
        try {
            // 회원가입 처리 (회원 정보 DB 저장)
            Member member = memberService.registerMember(memberDto);
            // 성공 시 메시지와 함께 로그인 페이지로 리다이렉트
            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");
            return "redirect:/login";
        } catch (Exception e) {
            // 회원가입 실패 시 예외 처리
            redirectAttributes.addFlashAttribute("message", "회원가입에 실패했습니다. 다시 시도해주세요.");
            return "redirect:/login/signup";  // 실패 시 회원가입 페이지로 리다이렉트
        }
    }

    @GetMapping("/check-duplicate")
    public ResponseEntity<?> checkDuplicate(@RequestParam("id") String memberId) {
        // ID 중복 확인 로직을 추가
        boolean isDuplicate = memberService.isDuplicateId(memberId);

        // 중복 여부에 따라 응답 처리
        if (isDuplicate) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("중복된 아이디입니다");
        } else {
            return ResponseEntity.ok("사용 가능한 아이디입니다.");
        }
    }

    @GetMapping("/check-email-duplicate")
    public ResponseEntity<?> checkEmailDuplicate(@RequestParam("email") String memberEmail) {
        // 이메일 중복 확인 로직을 추가
        boolean isDuplicate = memberService.isDuplicateEmail(memberEmail);

        // 중복 여부에 따라 응답 처리
        if (isDuplicate) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("중복된 이메일입니다");
        } else {
            return ResponseEntity.ok("사용 가능한 이메일입니다.");
        }
    }

}
