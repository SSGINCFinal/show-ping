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

    @NotNull
    @Column(name = "member_address")
    private String memberAddress;

    // =========== 관계 연결 ===========
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Cart> carts;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Orders> orders;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Payment> payments;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Stream> streams;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Watch> watches;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BlackList> blackLists;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Report> reports;

    // ⭐ 기본값 설정
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
