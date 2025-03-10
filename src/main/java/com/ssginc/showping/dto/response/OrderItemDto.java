package com.ssginc.showping.dto.response;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long productNo;
    private Long quantity;
    private Long totalPrice;
}
