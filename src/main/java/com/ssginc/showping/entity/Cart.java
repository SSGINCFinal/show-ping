package com.ssginc.showping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart")
public class Cart {

    @EmbeddedId
    private CartId cartId;

    // 회원
    // 장바구니 : 회원은 N : 1의 관계를 가진다.
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberNo")
    @JoinColumn(name = "member_no", referencedColumnName = "member_no")
    private Member member;

    // 상품
    // 장바구니 : 상품은 N : 1의 관계를 가진다.
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productNo")
    @JoinColumn(name = "product_no", referencedColumnName = "product_no")
    private Product product;

    @Column(name = "cart_product_quantity")
    private Long cartProductQuantity;

}
