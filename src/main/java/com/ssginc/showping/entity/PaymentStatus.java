package com.ssginc.showping.entity;

import lombok.Getter;

@Getter
public enum PaymentStatus {

    READY("대기"),
    COMPLETE("완료"),
    FAILURE("실패"),
    CANCEL("취소");

    private final String paymentStatus;

    PaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

}
