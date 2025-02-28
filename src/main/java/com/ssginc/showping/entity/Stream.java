package com.ssginc.showping.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stream")
public class Stream {

    @Id
    @Column(name = "stream_no")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long streamNo;

    // 회원
    // 영상 : 회원은 N : 1의 관계를 가진다.
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", referencedColumnName = "member_no")
    private Member member;

    // 상품
    // 영상 : 상품은 1 : 1의 관계를 가진다.
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_no", referencedColumnName = "product_no")
    private Product product;

    @NotNull
    @Column(name = "stream_title")
    private String streamTitle;

    @Column(name = "stream_description", length = 500)
    private String streamDescription;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "stream_status")
    private StreamStatus streamStatus;

    @Column(name = "stream_enroll_time")
    private LocalDateTime streamEnrollTime;

    @Column(name = "stream_start_time")
    private LocalDateTime streamStartTime;

    @Column(name = "stream_end_time")
    private LocalDateTime streamEndTime;

    // =========== 관계 연결 ===========

    // 시청
    // 영상 : 시청은 1 : N의 관계를 가진다.
    @OneToMany(mappedBy = "stream", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Watch> watches;

    // 채팅방
    // 영상 : 채팅방은 1 : N의 관계를 가진다.
    @OneToMany(mappedBy = "stream", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChatRoom> chatRooms;

}
