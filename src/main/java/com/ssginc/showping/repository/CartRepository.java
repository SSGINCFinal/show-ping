package com.ssginc.showping.repository;

import com.ssginc.showping.entity.Cart;
import com.ssginc.showping.entity.CartId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, CartId> {

    // 특정 회원(memberNo)의 장바구니 조회
    @Query("SELECT c FROM Cart c WHERE c.member.memberNo = :memberNo")
    List<Cart> findByMemberNo(@Param("memberNo") Long memberNo);

    @Query("SELECT c FROM Cart c WHERE c.member.memberNo = :memberNo AND c.product.productNo = :productNo")
    Optional<Cart> findByMemberProductNo(@Param("memberNo") Long memberNo, @Param("productNo") Long productNo);
}
