package com.ssginc.showping.entity;

import lombok.Getter;

@Getter
public enum PaymentMethod {

    CARD("카드"),
    ACCOUNT("계좌이체"),
    VIRTUAL("가상계좌"),
    MOBILE("휴대폰결제"),
    SIMPLE("간편결제");

    private final String paymentMethod;

    PaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

}
