package com.ssginc.showping.dto.response;

import com.ssginc.showping.entity.OrderDetail;
import lombok.Getter;

@Getter
public class OrderDetailDto {
    private String productName;
    private Long orderDetailQuantity;
    private Long orderDetailTotalPrice;

    public OrderDetailDto(OrderDetail orderDetail) {
        this.productName = orderDetail.getProduct().getProductName();
        this.orderDetailQuantity = orderDetail.getOrderDetailQuantity();
        this.orderDetailTotalPrice = orderDetail.getOrderDetailTotalPrice();
    }
}
