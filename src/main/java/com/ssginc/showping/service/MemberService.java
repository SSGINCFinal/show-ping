package com.ssginc.showping.service;

import com.ssginc.showping.dto.object.MemberDTO;
import com.ssginc.showping.entity.Member;
import com.ssginc.showping.entity.MemberRole;
import com.ssginc.showping.jwt.JwtUtil;
import com.ssginc.showping.repository.MemberRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.IGoogleAuthenticator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final GoogleAuthenticator googleAuthenticator;

    /**
     * ✅ 로그인 처리 메서드 (컨트롤러에서 호출)
     */
    public ResponseEntity<?> login(Member member, HttpServletResponse response) {
        System.out.println("📢 로그인 요청: " + member.getMemberId());

        String memberId = member.getMemberId();
        String memberPassword = member.getMemberPassword();
        Authentication authentication;

        // 인증 시도
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(memberId, memberPassword));
        } catch (BadCredentialsException e) {
            System.out.println("❌ 로그인 실패: 잘못된 ID 또는 비밀번호");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "아이디 또는 비밀번호가 올바르지 않습니다."));
        }

        // ✅ SecurityContext에 사용자 정보 저장 (중요)
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("✅ SecurityContext에 사용자 설정 완료: " + authentication.getName());

        // 사용자 정보 조회
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (userDetails == null) {
            System.out.println("❌ 로그인 실패: 사용자 정보 없음");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "사용자 정보를 찾을 수 없습니다."));
        }

        // 역할(Role) 가져오기
        String role = userDetails.getAuthorities().isEmpty() ? "ROLE_USER" : userDetails.getAuthorities().iterator().next().getAuthority();

        // ✅ 관리자(`ROLE_ADMIN`)이면 2차 인증 필요
        if ("ROLE_ADMIN".equals(role)) {
            System.out.println("🔹 관리자 계정 로그인 → 2FA 필요");

            // ✅ JWT 발급 (이 단계에서는 2FA 미완료 상태)
            String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername(), role);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());
            refreshTokenService.saveRefreshToken(memberId, refreshToken);

            System.out.println("✅ 생성된 JWT Access Token: " + accessToken);
            System.out.println("✅ 생성된 JWT Refresh Token: " + refreshToken);

            return ResponseEntity.ok(Map.of(
                    "status", "2FA_REQUIRED",  // ✅ 프론트엔드에서 OTP 입력 요청
                    "accessToken", accessToken, // ✅ 2FA 후 최종 사용
                    "refreshToken", refreshToken // ✅ Redis 저장
            ));
        }

        // ✅ 일반 사용자(`ROLE_USER`)는 2FA 없이 바로 로그인 성공
        System.out.println("✅ 일반 사용자 로그인 성공!");

        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername(), role);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        System.out.println("✅ 생성된 JWT Access Token: " + accessToken);
        System.out.println("✅ 생성된 JWT Refresh Token: " + refreshToken);

        refreshTokenService.saveRefreshToken(memberId, refreshToken);

        return ResponseEntity.ok(Map.of(
                "status", "LOGIN_SUCCESS",
                "accessToken", accessToken,
                "refreshToken", refreshToken
        ));
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


    public void logout(String username, HttpServletResponse response) {
        // Refresh Token 삭제
        refreshTokenService.deleteRefreshToken(username);

        // JWT Access Token 삭제 (쿠키 제거)
        response.setHeader("Set-Cookie", "accessToken=; HttpOnly; Secure; Path=/; Max-Age=0");
        response.setHeader("Set-Cookie", "refreshToken=; HttpOnly; Secure; Path=/; Max-Age=0");

        System.out.println("✅ 로그아웃 완료: JWT 쿠키 및 Refresh Token 삭제됨");
    }

    public boolean isDuplicateId(String memberId) {
        // memberId로 회원을 조회하고, 결과가 있으면 중복된 ID라는 의미
        return memberRepository.existsByMemberId(memberId);
    }

    public boolean isDuplicateEmail(String memberEmail) {
        return memberRepository.existsByMemberEmail(memberEmail);
    }

    public Member findMemberById(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + memberId));
    }

    public ResponseEntity<Map<String, String>> verifyTOTP(String memberId, int totpCode) {
        Member member = memberRepository.findByMemberId(memberId).orElse(null);

        if (member == null) {
            System.out.println("❌ 회원을 찾을 수 없음!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "사용자를 찾을 수 없습니다."));
        }

        // ✅ 일반 사용자(`ROLE_USER`)는 2FA 검증 없이 바로 로그인 성공 처리
        if (!"ROLE_ADMIN".equals(member.getMemberRole().name())) {
            return ResponseEntity.ok(Map.of("status", "LOGIN_SUCCESS"));
        }

        // ✅ OTP 검증
        boolean isTotpValid = googleAuthenticator.authorize(member.getOtpSecretKey(), totpCode);
        if (isTotpValid) {
            System.out.println("✅ 관리자 2차 인증 성공!");
            return ResponseEntity.ok(Map.of("status", "LOGIN_SUCCESS"));
        } else {
            System.out.println("❌ TOTP 인증 실패!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "TOTP_FAILED"));
        }
    }

    /**
     * ✅ 사용자 비밀번호 검증 메서드
     */
    public boolean verifyPassword(String memberId, String password) {
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);

        if (optionalMember.isEmpty()) {
            System.out.println("❌ 사용자 없음: " + memberId);
            return false;
        }

        Member member = optionalMember.get();
        boolean isMatch = passwordEncoder.matches(password, member.getMemberPassword());

        if (isMatch) {
            System.out.println("✅ 비밀번호 일치! 로그인 가능: " + memberId);
        } else {
            System.out.println("❌ 비밀번호 불일치: " + memberId);
        }

        return isMatch;
    }

}