package com.ssginc.showping.service;

import com.ssginc.showping.dto.object.MemberDTO;
import com.ssginc.showping.entity.Member;
import com.ssginc.showping.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean isDuplicateId(String memberId) {
        // memberId로 회원을 조회하고, 결과가 있으면 중복된 ID라는 의미
        return memberRepository.existsByMemberId(memberId);
    }
    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    // 회원가입 처리
    @Transactional
    public String signup(Member member) {
        // 회원 ID 중복 체크
        if (memberRepository.findByMemberId(member.getMemberId()).isPresent()) {
            return "이미 존재하는 회원 ID입니다.";  // 중복 시 메시지 반환
        }

        // 비밀번호 암호화 로직 (예시로 bcrypt 사용)
        // member.setMemberPassword(passwordEncoder.encode(member.getMemberPassword()));
        String encryptedPassword = passwordEncoder.encode(member.getMemberPassword());
        member.setMemberPassword(encryptedPassword);

        memberRepository.save(member);  // 회원 저장
        return "회원가입이 완료되었습니다.";  // 성공 메시지 반환
    }

    public boolean registerMember(MemberDTO memberDTO) {
        if (memberRepository.existsByMemberId(memberDTO.getMemberId())) {
            return false; // 아이디 중복 시 가입 불가
        }

        Member member = Member.builder()
                .memberName(memberDTO.getMemberName())
                .memberId(memberDTO.getMemberId())
                .memberEmail(memberDTO.getMemberEmail())
                .memberPassword(passwordEncoder.encode(memberDTO.getMemberPassword())) // 비밀번호 암호화
                .memberAddress(memberDTO.getMemberAddress())
                .memberPhone(memberDTO.getMemberPhone())
                .build();

        memberRepository.save(member);
        return true;
    }

    //
}