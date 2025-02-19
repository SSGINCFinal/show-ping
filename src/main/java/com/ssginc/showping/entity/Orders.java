package com.ssginc.showping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_no")
    private Long ordersNo;

    // 회원
    // 주문 : 회원은 N : 1의 관계를 가진다.
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", referencedColumnName = "member_no")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "orders_status")
    private OrderStatus ordersStatus;

    @Column(name = "orders_total_price")
    private Long ordersTotalPrice;

    @Column(name = "orders_date")
    private LocalDateTime ordersDate;

    // =========== 관계 연결 ===========

    // 주문내역
    // 주문 : 주문내역은 1 : N의 관계를 가진다.
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    // 결제
    // 주문 : 결제는 1 : 1의 관계를 가진다.
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

}
