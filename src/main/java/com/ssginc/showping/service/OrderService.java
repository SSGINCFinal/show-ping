package com.ssginc.showping.service;

import com.ssginc.showping.dto.response.OrderDetailDto;
import com.ssginc.showping.dto.response.OrdersDto;
import com.ssginc.showping.entity.OrderDetail;
import com.ssginc.showping.entity.Orders;
import com.ssginc.showping.repository.OrderDetailRepository;
import com.ssginc.showping.repository.OrdersRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final OrderDetailRepository orderDetailRepository;

    public OrderService(OrdersRepository ordersRepository, OrderDetailRepository orderDetailRepository) {
        this.ordersRepository = ordersRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public List<OrdersDto> findAllOrdersByMember(Long memberNo) {
        List<Orders> ordersList = ordersRepository.findByMember_MemberNoOrderByOrdersDateDesc(memberNo);
        return ordersList.stream().map(OrdersDto::new).collect(Collectors.toList());
    }

    public List<OrderDetailDto> findOrderDetailsByOrder(Long orderNo) {
        Optional<Orders> order = ordersRepository.findById(orderNo);
        return order.map(o -> orderDetailRepository.findByOrder(o)
                        .stream()
                        .map(OrderDetailDto::new)
                        .collect(Collectors.toList()))
                .orElse(null);
    }
}
