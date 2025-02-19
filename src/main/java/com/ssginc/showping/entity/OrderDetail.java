package com.ssginc.showping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orderdetail")
public class OrderDetail {

    @EmbeddedId
    private OrderDetailId orderDetailId;

    // 상품
    // 주문내역 : 상품은 N : 1의 관계를 가진다.
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productNo")
    @JoinColumn(name = "product_no", referencedColumnName = "product_no")
    private Product product;

    // 주문
    // 주문내역 : 주문은 N : 1의 관계를 가진다.
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ordersNo")
    @JoinColumn(name = "orders_no", referencedColumnName = "orders_no")
    private Orders order;

    @Column(name = "order_detail_quantity")
    private Long orderDetailQuantity;

    @Column(name = "order_detail_total_price")
    private Long orderDetailTotalPrice;

}
