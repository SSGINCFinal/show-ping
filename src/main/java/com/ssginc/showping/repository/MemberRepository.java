package com.ssginc.showping.repository;

import com.ssginc.showping.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByMemberId(String memberId);
    Optional<Member> findByMemberId(String memberId);
    Member save(Member member);
    boolean existsByMemberEmail(String memberEmail);
}
