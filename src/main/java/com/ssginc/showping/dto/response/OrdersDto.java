package com.ssginc.showping.dto.response;

import com.ssginc.showping.entity.Orders;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class OrdersDto {
    private Long ordersNo;
    private String ordersStatus;
    private Long ordersTotalPrice;
    private LocalDateTime ordersDate;

    public OrdersDto(Orders order) {
        this.ordersNo = order.getOrdersNo();
        this.ordersStatus = order.getOrdersStatus().name();
        this.ordersTotalPrice = order.getOrdersTotalPrice();
        this.ordersDate = order.getOrdersDate();
    }
}
