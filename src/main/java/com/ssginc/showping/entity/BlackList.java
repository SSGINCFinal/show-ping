package com.ssginc.showping.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "blacklist")
public class BlackList {

    @Id
    @Column(name = "blacklist_no")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blackListNo;

    // 회원
    // 블랙리스트 : 회원은 N : 1의 관계를 가진다.
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", referencedColumnName = "member_no")
    private Member member;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "blacklist_delete")
    private BlackListDelete blackListDelete;

}
