package com.ssginc.showping.controller;

import com.ssginc.showping.dto.response.OrderDetailDto;
import com.ssginc.showping.dto.response.OrdersDto;
import com.ssginc.showping.entity.OrderDetail;
import com.ssginc.showping.entity.Orders;
import com.ssginc.showping.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/member/{memberNo}")
    public ResponseEntity<List<OrdersDto>> getAllOrdersByMember(@PathVariable Long memberNo) {
        List<OrdersDto> orders = orderService.findAllOrdersByMember(memberNo);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderNo}/details")
    public ResponseEntity<List<OrderDetailDto>> getOrderDetails(@PathVariable Long orderNo) {
        List<OrderDetailDto> orderDetails = orderService.findOrderDetailsByOrder(orderNo);
        return ResponseEntity.ok(orderDetails);
    }

}
