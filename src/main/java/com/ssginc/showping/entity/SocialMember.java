package com.ssginc.showping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "socialmember")
@Builder
public class SocialMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_no")
    private Long socialNo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", referencedColumnName = "member_no")
    private Member member;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "social_type")
    private SocialType socialType;

    @NotNull
    @Column(name = "social_id", length = 50, unique = true)
    private String socialId;

    @Column(name = "social_email", length = 100, unique = true)
    private String socialEmail;


}
