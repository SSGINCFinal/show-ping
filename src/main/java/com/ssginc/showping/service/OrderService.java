package com.ssginc.showping.service;

import com.ssginc.showping.dto.request.OrderRequestDto;
import com.ssginc.showping.dto.response.OrderDetailDto;
import com.ssginc.showping.dto.response.OrdersDto;
import com.ssginc.showping.entity.*;
import com.ssginc.showping.repository.MemberRepository;
import com.ssginc.showping.repository.OrderDetailRepository;
import com.ssginc.showping.repository.OrdersRepository;
import com.ssginc.showping.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {


    private final OrdersRepository ordersRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public OrderService(OrdersRepository ordersRepository, OrderDetailRepository orderDetailRepository,
                        MemberRepository memberRepository, ProductRepository productRepository) {
        this.ordersRepository = ordersRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
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

    @Transactional
    public void createOrder(OrderRequestDto orderRequestDto) {
        System.out.println("OrderRequestDto 데이터: " + orderRequestDto);

        // 회원 조회 (예외 발생 가능)
        Member member = memberRepository.findById(orderRequestDto.getMemberNo())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다: " + orderRequestDto.getMemberNo()));

        // 주문 저장
        Orders order = new Orders();
        order.setMember(member);
        order.setOrdersTotalPrice(orderRequestDto.getTotalPrice());
        order.setOrdersDate(LocalDateTime.now());
        order.setOrdersStatus(OrderStatus.READY);

        // 주문을 먼저 저장하고, ID 값을 가져옴
        Orders savedOrder = ordersRepository.save(order);
        System.out.println("저장된 주문 번호: " + savedOrder.getOrdersNo());

        // 주문 상세 저장
        List<OrderDetail> orderDetails = orderRequestDto.getOrderItems().stream().map(item -> {
            if (item.getProductNo() == null) {
                throw new IllegalArgumentException("상품 번호가 없습니다: " + item);
            }

            Product product = productRepository.findById(item.getProductNo())
                    .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다: " + item.getProductNo()));

            // OrderDetailId 객체 생성
            OrderDetailId orderDetailId = new OrderDetailId(product.getProductNo(), savedOrder.getOrdersNo());

            OrderDetail detail = new OrderDetail();
            detail.setOrderDetailId(orderDetailId); // 복합 키 설정
            detail.setOrder(savedOrder);
            detail.setProduct(product);
            detail.setOrderDetailQuantity(item.getQuantity());
            detail.setOrderDetailTotalPrice(item.getTotalPrice());

            return detail;
        }).collect(Collectors.toList());

        orderDetailRepository.saveAll(orderDetails);
        System.out.println("저장된 주문 상세 개수: " + orderDetails.size());
    }
}
