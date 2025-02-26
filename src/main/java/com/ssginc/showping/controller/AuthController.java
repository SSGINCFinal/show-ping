package com.ssginc.showping.controller;

import com.ssginc.showping.entity.Member;
import com.ssginc.showping.jwt.JwtUtil;
import com.ssginc.showping.service.MemberDetailsServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MemberDetailsServiceImpl memberDetailsService;


    /**
     * ✅ 로그인 API (JWT 발급)
     */
//    @PostMapping("/login")
//    @ResponseBody
//    public Map<String, String> login(Member member, HttpServletResponse response) {
//        System.out.println("login call=======================");
//        System.out.println("📢 로그인 요청: " + member.getMemberId() + " " +  member.getMemberPassword());
//
//        String memberId = member.getMemberId();
//        String memberPassword = member.getMemberPassword();
//        Authentication authentication;
//
//        // 인증 시도
//        try {
//            authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(memberId, memberPassword));
//            //userDetailsService.loadUserByUsername(memberId) 내부적으로 호출하여 id/pw맞는지 검증해줌.
//        } catch (BadCredentialsException e) {
//            return Map.of("error", "로그인 실패: 잘못된 사용자 이름 또는 비밀번호");
//        }
//        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@");
//        System.out.println(authentication.getPrincipal());
//        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@");
//
//        // 사용자 정보 조회
//        // ✅ 인증 성공 → SecurityContext에 저장될 정보 가져오기
//
//        /// /////////////////////////////// 실습 부분 //////////////////////////////////////////////
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        /// /////////////////////////////// 실습 부분 //////////////////////////////////////////////
//
//        if (userDetails == null) {
//            return Map.of("error", "로그인 실패: 사용자 정보를 찾을 수 없습니다");
//        }
//
//        System.out.println(userDetails.getUsername() + "==============\n" +
//                userDetails.getPassword() + "==============\n" +
//                userDetails.getAuthorities() + "==============\n");
//
//        // 역할(Role) 가져오기
//        String role = userDetails.getAuthorities().isEmpty() ? "ROLE_USER"
//                : userDetails.getAuthorities().iterator().next().getAuthority();
//
//        // ✅ JWT 토큰 생성
//        String token = jwtUtil.generateAccessToken(userDetails.getUsername(), role);
//
//        // ✅ HTTPOnly, Secure 쿠키에 JWT 저장
///// /////////////////////////////// 실습 부분 //////////////////////////////////////////////
//        Cookie cookie = new Cookie("accessToken", token);
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // HTTPS 환경에서만 전송
//        cookie.setPath("/");
//        cookie.setMaxAge(86400); // 1일 (초 단위)
//        response.addCookie(cookie);
///// /////////////////////////////// 실습 부분 //////////////////////////////////////////////
//
//        System.out.println("✅ 발급된 JWT: " + token);
//
//        response.setStatus(HttpServletResponse.SC_OK);
//        response.setHeader("Location", "/");
//        return Map.of("message", "성공", "token", token);
//    }

    @PostMapping("/login")
    public String login(Member member, HttpServletResponse response) {
        System.out.println("login call=======================");
        System.out.println("📢 로그인 요청: " + member.getMemberId() + " " + member.getMemberPassword());

        String memberId = member.getMemberId();
        String memberPassword = member.getMemberPassword();
        Authentication authentication;

        // 인증 시도
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(memberId, memberPassword));
        } catch (BadCredentialsException e) {
            // 로그인 실패 시 메시지 처리
            System.out.println("❌ 로그인 실패: 잘못된 ID 또는 비밀번호");
            return "redirect:/login?error=true";  // 로그인 페이지로 리다이렉트
        }

        // 사용자 정보 조회
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (userDetails == null) {
            System.out.println("❌ 로그인 실패: 사용자 정보 없음");
            return "redirect:/login?error=true";  // 로그인 페이지로 리다이렉트
        }

        // 역할(Role) 가져오기
        String role = userDetails.getAuthorities().isEmpty() ? "ROLE_USER" : userDetails.getAuthorities().iterator().next().getAuthority();

        // ✅ JWT 토큰 생성
        String Accesstoken = jwtUtil.generateAccessToken(userDetails.getUsername(), role);
        String Refreshtoken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        System.out.println("생성된 JWT Access 토큰: " + Accesstoken);
        System.out.println("생성된 JWT Refresh 토큰: " + Refreshtoken);

        System.out.println("현재 회원 권한: " + authentication.getAuthorities());

        // ✅ HTTPOnly, Secure 쿠키에 JWT 저장
        Cookie cookie = new Cookie("accessToken", Accesstoken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS 환경에서만 전송
        cookie.setPath("/");
        cookie.setMaxAge(86400); // 1일 (초 단위)
        response.addCookie(cookie);

        // 로그인 후 홈 페이지로 리다이렉트
        return "redirect:/";  // 홈 페이지로 리다이렉트
    }



    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        // ✅ 쿠키에서 JWT 제거
        Cookie cookie = new Cookie("accessToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료
        response.addCookie(cookie);

        System.out.println("✅ 로그아웃 완료: JWT 쿠키 삭제됨");
        return "redirect:/";  // 홈페이지로 이동
    }

    /**
     * ✅ 로그아웃 API (JWT 삭제)
     */
//    @PostMapping("/logout")
//    @ResponseBody
//    public Map<String, String> logout(HttpServletResponse response) {
//        // ✅ 쿠키에서 JWT 제거
///// /////////////////////////////// 실습 부분 //////////////////////////////////////////////
//        Cookie cookie = new Cookie("accessToken", "");
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
//        cookie.setPath("/");
//        cookie.setMaxAge(0); // 즉시 만료
//        response.addCookie(cookie);
///// /////////////////////////////// 실습 부분 //////////////////////////////////////////////
//
//        return Map.of("message", "로그아웃 성공!");
//    }

    /**
     * ✅ 로그인한 사용자 정보 조회 API (SecurityContext에서 가져오기)
     */
    @GetMapping("/user-info3")
    @ResponseBody
    public Map<String, String> getUserInfo(Authentication authentication) {

        // ✅ SecurityContextHolder에서 사용자 정보 가져오기
/// /////////////////////////////// 실습 부분 //////////////////////////////////////////////
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            String role = authentication.getAuthorities().iterator().next().getAuthority();

            System.out.println("✅ SecurityContext에서 가져온 사용자: " + username + " | 역할: " + role);
            return Map.of("username", username, "role", role);
        }
/// /////////////////////////////// 실습 부분 //////////////////////////////////////////////
        // ✅ 인증되지 않은 경우
        return Map.of("error", "로그인 정보 없음");
    }

}

