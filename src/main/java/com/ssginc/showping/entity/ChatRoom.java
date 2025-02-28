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
@Table(name = "chatroom")
public class ChatRoom {

    @Id
    @Column(name = "chatroom_no")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomNo;

    // 영상
    // 채팅방 : 영상은 1 : 1의 관계를 가진다.
    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stream_no", referencedColumnName = "stream_no")
    private Stream stream;

    @NotNull
    @Column(name = "chatroom_created_at")
    private LocalDateTime chatRoomCreatedAt;

    @Column(name = "chatroom_max_participants")
    private Long chatRoomMaxParticipants;

}
