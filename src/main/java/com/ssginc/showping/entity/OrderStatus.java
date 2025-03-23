package com.ssginc.showping.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {

    READY("준비 중"),
    TRANSIT("배송 중"),
    COMPLETE("배송 완료");

    private final String orderStatus;

    OrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

}
