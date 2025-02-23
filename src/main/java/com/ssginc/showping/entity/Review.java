package com.ssginc.showping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_no")
    private Long reviewNo;

    // 회원
    // 리뷰 : 회원은 N : 1의 관계를 가진다.
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", referencedColumnName = "member_no")
    private Member member;

    // 상품
    // 리뷰 : 상품은 N : 1의 관계를 가진다.
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_no", referencedColumnName = "product_no")
    private Product product;

    @Column(name = "review_rating")
    private Long reviewRating;

    @Column(name = "review_comment", length = 200)
    private String reviewComment;

    @Column(name = "review_create_at")
    private LocalDateTime reviewCreateAt;

    @Column(name = "review_url", length = 100)
    private String reviewUrl;

}
