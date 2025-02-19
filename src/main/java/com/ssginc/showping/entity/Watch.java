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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "watch")
public class Watch {

    @Id
    @Column(name = "watch_no")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long watchNo;

    // 회원
    // 시청 : 회원은 N : 1의 관계를 가진다.
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", referencedColumnName = "member_no")
    private Member member;

    // 영상
    // 시청 : 영상은 N : 1의 관계를 가진다.
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stream_no", referencedColumnName = "stream_no")
    private Stream stream;

    @NotNull
    @Column(name = "watch_time")
    private LocalDateTime watchTime;

}
