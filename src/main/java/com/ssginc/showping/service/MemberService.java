package com.ssginc.showping.service;

import com.ssginc.showping.dto.object.MemberDTO;
import com.ssginc.showping.entity.Member;
import com.ssginc.showping.entity.MemberRole;
import com.ssginc.showping.jwt.JwtUtil;
import com.ssginc.showping.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.UUID;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    /**
     * ✅ 로그인 처리 메서드 (컨트롤러에서 호출)
     */
    public boolean login(Member member, HttpServletResponse response) {
        System.out.println("📢 로그인 요청: " + member.getMemberId() + " " + member.getMemberPassword());

        String memberId = member.getMemberId();
        String memberPassword = member.getMemberPassword();
        Authentication authentication;

        // 인증 시도
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(memberId, memberPassword));
        } catch (BadCredentialsException e) {
            System.out.println("❌ 로그인 실패: 잘못된 ID 또는 비밀번호");
            return false;
        }

        // 사용자 정보 조회
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (userDetails == null) {
            System.out.println("❌ 로그인 실패: 사용자 정보 없음");
            return false;
        }

        // 역할(Role) 가져오기
        String role = userDetails.getAuthorities().isEmpty() ? "ROLE_USER" : userDetails.getAuthorities().iterator().next().getAuthority();

        // ✅ JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername(), role);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        System.out.println("생성된 JWT Access 토큰: " + accessToken);
        System.out.println("생성된 JWT Refresh 토큰: " + refreshToken);
        System.out.println("현재 회원 권한: " + authentication.getAuthorities());

        // ✅ HTTPOnly, Secure 쿠키에 JWT 저장
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS 환경에서만 전송
        cookie.setPath("/");
        cookie.setMaxAge(86400); // 1일 (초 단위)
        response.addCookie(cookie);

        return true;
    }

    @Transactional
    public Member registerMember(MemberDTO memberDTO) throws Exception {

        Member member = Member.builder()
                .memberName(memberDTO.getMemberName())
                .memberId(memberDTO.getMemberId())
                .memberEmail(memberDTO.getMemberEmail())
                .memberPassword(passwordEncoder.encode(memberDTO.getMemberPassword()))
                .memberAddress(memberDTO.getMemberAddress())
                .memberPhone(memberDTO.getMemberPhone())
                .memberRole(MemberRole.ROLE_USER)
                .streamKey(UUID.randomUUID().toString())
                .memberPoint(0L)
                .build();

        try {
            return memberRepository.save(member);
        } catch (Exception e) {
            throw new Exception("회원 등록 중 오류가 발생했습니다.", e);
        }
    }

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


    public void logout(HttpServletResponse response) {
        // ✅ 쿠키에서 JWT 제거
        Cookie cookie = new Cookie("accessToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료
        response.addCookie(cookie);

        System.out.println("✅ 로그아웃 완료: JWT 쿠키 삭제됨");
    }

    /**
     * ✅ 현재 로그인한 사용자 정보 조회
     */
    public Map<String, String> getUserInfo(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            String role = authentication.getAuthorities().iterator().next().getAuthority();

            System.out.println("✅ SecurityContext에서 가져온 사용자: " + username + " | 역할: " + role);
            return Map.of("username", username, "role", role);
        }
        return Map.of("error", "로그인 정보 없음");
    }

    public boolean isDuplicateId(String memberId) {
        // memberId로 회원을 조회하고, 결과가 있으면 중복된 ID라는 의미
        return memberRepository.existsByMemberId(memberId);
    }

}