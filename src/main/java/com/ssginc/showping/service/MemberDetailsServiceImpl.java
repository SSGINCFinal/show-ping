package com.ssginc.showping.service;

import com.ssginc.showping.entity.Member;
import com.ssginc.showping.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class MemberDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        // 1. DB에서 회원 정보 조회
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + memberId));

        // 2. UserDetails 객체 생성 후 반환
        return new org.springframework.security.core.userdetails.User(
                member.getMemberId(),   // 로그인 ID
                member.getMemberPassword(), // 암호화된 비밀번호
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + member.getMemberRole().name())) // ROLE_ 접두사 추가
        );
    }
}