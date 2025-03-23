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
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_no")
    private Long paymentNo;

    // 회원
    // 결제 : 회원은 N : 1의 관계를 가진다.
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no")
    private Member member;

    // 주문
    // 결제 : 주문은 1 : 1의 관계를 가진다.
    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_no", referencedColumnName = "orders_no")
    private Orders order;

    @Column(name = "payment_amount")
    private Long paymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "payment_pg_provider", length = 50)
    private String paymentPgProvider;

    @Column(name = "payment_pg_tid", length = 100)
    private String paymentPgTid;

    @Column(name = "payment_create_at")
    private LocalDateTime paymentCreateAt;

    @Column(name = "payment_update_at")
    private LocalDateTime paymentUpdateAt;

}
