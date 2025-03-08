package com.ssginc.showping.repository;

import com.ssginc.showping.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    // 특정 회원의 가장 최근 주문 찾기
    Optional<Orders> findTopByMember_MemberNoOrderByOrdersDateDesc(Long memberNo);

    // 특정 회원의 전체 주문 목록 조회
    List<Orders> findByMember_MemberNoOrderByOrdersDateDesc(Long memberNo);
}
