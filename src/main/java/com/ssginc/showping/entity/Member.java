package com.ssginc.showping.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member")
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_no")
    private Long memberNo;

    @NotNull
    @Column(name = "member_id", length = 50, unique = true)
    private String memberId;

    @NotNull
    @Column(name = "member_name", length = 50)
    private String memberName;

    @NotNull
    @Column(name = "member_password")
    private String memberPassword; // → 저장 전 암호화 필수!

    @NotNull
    @Column(name = "member_email", length = 100, unique = true)
    private String memberEmail;

    @Column(name = "member_phone", length = 20, unique = true)
    private String memberPhone;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "member_role")
    private MemberRole memberRole;

    @NotNull
    @Column(name = "stream_key", unique = true)
    private String streamKey;

    @Column(name = "member_point")
    private Long memberPoint;

    @Column(name = "otp_secret_key")
    private String otpSecretKey;

    @NotNull
    @Column(name = "member_address")
    private String memberAddress;

    // =========== 관계 연결 ===========

    // 리뷰
    // 회원 : 리뷰는 1 : N의 관계를 가진다.
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Review> reviews;

    // 장바구니
    // 회원 : 장바구니는 1 : N의 관계를 가진다.
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Cart> carts;

    // 주문
    // 회원 : 주문은 1 : N의 관계를 가진다.
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Orders> orders;

    // 결제
    // 회원 : 주문은 1 : N의 관계를 가진다.
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Payment> payments;

    // 영상
    // 회원 : 영상은 1 : N의 관계를 가진다.
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Stream> streams;

    // 시청
    // 회원 : 시청은 1 : N의 관계를 가진다.
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Watch> watches;

    // 블랙리스트
    // 회원 : 블랙리스트는 1: N의 관계를 가진다.
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BlackList> blackLists;

    // 신고
    // 회원 : 신고는 1 : N의 관계를 가진다.
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Report> reports;

    // 소셜회원
    // 회원 : 소셜회원은 1 : N의 관계를 가진다.
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SocialMember> socialmembers;

    // memberId & memberPassword만 받는 생성자 추가
    public Member(String memberId, String memberPassword) {
        this.memberId = memberId;
        this.memberPassword = memberPassword;
    }


    // 기본값 설정
    @PrePersist
    public void setDefaultValues() {
        if (this.streamKey == null || this.streamKey.isEmpty()) {
            this.streamKey = UUID.randomUUID().toString();
        }
        if (this.memberRole == null) {
            this.memberRole = MemberRole.ROLE_USER;
        }
        if (this.memberPoint == null) {
            this.memberPoint = 0L; // 포인트 기본값 설정
        }
    }
}
